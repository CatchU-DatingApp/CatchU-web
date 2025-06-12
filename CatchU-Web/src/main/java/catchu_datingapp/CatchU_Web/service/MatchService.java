package catchu_datingapp.CatchU_Web.service;

import catchu_datingapp.CatchU_Web.model.Match;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Comparator;

@Service
public class MatchService {

    private static final String COLLECTION_NAME = "Matches";


    public List<Match> getMatchesForUser(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereArrayContains("users", userId)
                .get();

        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        List<Match> matches = new ArrayList<>();

        for (DocumentSnapshot document : documents) {
            Match match = document.toObject(Match.class);
            assert match != null;
            match.setId(document.getId());
            matches.add(match);
        }

        // Manual sorting berdasarkan lastMessageTimestamp (terbaru di atas)
        matches.sort(new Comparator<Match>() {
            @Override
            public int compare(Match m1, Match m2) {
                // Handle null lastMessageTimestamp
                if (m1.getLastMessageTimestamp() == null && m2.getLastMessageTimestamp() == null) return 0;
                if (m1.getLastMessageTimestamp() == null) return 1;  // m1 ke bawah
                if (m2.getLastMessageTimestamp() == null) return -1; // m2 ke bawah

                // Sort descending (terbaru di atas)
                return m2.getLastMessageTimestamp().compareTo(m1.getLastMessageTimestamp());
            }
        });

        return matches;
    }

    public Match getMatchById(String matchId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(matchId);
        DocumentSnapshot snapshot = docRef.get().get();

        if (snapshot.exists()) {
            Match match = snapshot.toObject(Match.class);
            match.setId(snapshot.getId());
            return match;
        }

        return null;
    }

    public String createOrUpdateMatch(Match match) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (match.getId() == null || match.getId().isEmpty()) {
            DocumentReference newDoc = db.collection(COLLECTION_NAME).document();
            match.setId(newDoc.getId());
            newDoc.set(match).get();
            return match.getId();
        } else {
            db.collection(COLLECTION_NAME).document(match.getId()).set(match).get();
            return match.getId();
        }
    }

    public void deleteMatch(String matchId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME).document(matchId).delete().get();
    }
}