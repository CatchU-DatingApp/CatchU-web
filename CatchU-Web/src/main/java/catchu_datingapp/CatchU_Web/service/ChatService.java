package catchu_datingapp.CatchU_Web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import catchu_datingapp.CatchU_Web.model.Message;
import catchu_datingapp.CatchU_Web.model.Match;
import catchu_datingapp.CatchU_Web.model.User;

@Service
public class ChatService {

    private static final String MATCHES_COLLECTION = "Matches";
    private static final String MESSAGES_COLLECTION = "messages";
    private static final String USERS_COLLECTION = "Users";

    // Get user profile by ID
    public User getUserProfile(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference docRef = db.collection(USERS_COLLECTION).document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            User user = document.toObject(User.class);
            assert user != null;
            user.setId(document.getId());
            return user;
        }
        return null;
    }

    // Get messages for a match
    public List<Message> getMessages(String matchId, int limit) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        CollectionReference messagesRef = db.collection(MATCHES_COLLECTION)
                .document(matchId)
                .collection(MESSAGES_COLLECTION);

        Query query = messagesRef.orderBy("timestamp", Query.Direction.DESCENDING);
        if (limit > 0) {
            query = query.limit(limit);
        }

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Message> messages = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Message message = doc.toObject(Message.class);
            messages.add(message);
        }

        return messages;
    }

    // Send a new message
    public String sendMessage(String matchId, Message message) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // Set timestamp
        message.setTimestamp(Timestamp.now());
        message.setIsRead(false);

        // Add message to subcollection
        DocumentReference newMessageRef = db.collection(MATCHES_COLLECTION)
                .document(matchId)
                .collection(MESSAGES_COLLECTION)
                .document();

        ApiFuture<WriteResult> future = newMessageRef.set(message);
        future.get();


        updateLastMessage(matchId, message);

        return newMessageRef.getId();
    }

    // Mark messages as read
    public void markMessagesAsRead(String matchId, String otherUserId, String currentUserId)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        CollectionReference messagesRef = db.collection(MATCHES_COLLECTION)
                .document(matchId)
                .collection(MESSAGES_COLLECTION);

        // Get unread messages from other user
        Query query = messagesRef
                .whereEqualTo("senderId", otherUserId)
                .whereEqualTo("isRead", false);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        // Update each unread message
        for (QueryDocumentSnapshot doc : documents) {
            ApiFuture<WriteResult> updateFuture = doc.getReference().update("isRead", true);
            updateFuture.get();
        }
    }

    // Update last message in match document
    private void updateLastMessage(String matchId, Message message) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference matchRef = db.collection(MATCHES_COLLECTION).document(matchId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", message.getMessage());
        updates.put("lastMessageTimestamp", message.getTimestamp());
        updates.put("lastMessageSenderId", message.getSenderId());

        ApiFuture<WriteResult> future = matchRef.update(updates);
        future.get();
    }

    // Get match by ID
    public Match getMatch(String matchId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference docRef = db.collection(MATCHES_COLLECTION).document(matchId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Match match = document.toObject(Match.class);
            match.setId(document.getId());
            return match;
        }
        return null;
    }

    // Get all matches for a user
    public List<Match> getUserMatches(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection(MATCHES_COLLECTION)
                .whereArrayContains("userIds", userId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Match> matches = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Match match = doc.toObject(Match.class);
            match.setId(doc.getId());
            matches.add(match);
        }

        return matches;
    }

    // Get unread message count for a match
    public int getUnreadMessageCount(String matchId, String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        CollectionReference messagesRef = db.collection(MATCHES_COLLECTION)
                .document(matchId)
                .collection(MESSAGES_COLLECTION);

        Query query = messagesRef
                .whereNotEqualTo("senderId", userId)
                .whereEqualTo("isRead", false);

        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot querySnapshot = future.get();

        return querySnapshot.size();
    }
}