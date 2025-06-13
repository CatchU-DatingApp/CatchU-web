package catchu_datingapp.CatchU_Web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import catchu_datingapp.CatchU_Web.model.Match;
import catchu_datingapp.CatchU_Web.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

@Service
public class FirestoreService {

    private static final String COLLECTION_NAME = "Users";

    /**
     * Ambil semua user dari Firestore
     */
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            User user = document.toObject(User.class);
            user.setId(document.getId());
            users.add(user);
        }

        return users;
    }

    /**
     * Ambil satu user berdasarkan ID
     */
    public User getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (document.exists()) {
            User user = document.toObject(User.class);
            user.setId(document.getId());
            return user;
        } else {
            return null;
        }
    }

    /**
     * Tambah atau perbarui data user
     */
    public String saveOrUpdateUser(User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        String id = user.getId();
        if (id == null || id.isEmpty()) {
            DocumentReference newDoc = db.collection(COLLECTION_NAME).document();
            user.setId(newDoc.getId());
            newDoc.set(user).get(); // tunggu selesai
            return user.getId();
        } else {
            db.collection(COLLECTION_NAME).document(id).set(user).get();
            return id;
        }
    }

    /**
     * Hapus user berdasarkan ID
     */
    public String deleteUser(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME).document(id).delete().get();
        return id;
    }

    /**
     * Perbarui sebagian field dari user
     */
    public void updateUserFields(String id, Map<String, Object> updates) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME).document(id).update(updates).get();
    }

    /**
     * Cek apakah nomor telepon sudah terdaftar
     */
    public boolean checkPhoneNumberExists(String phoneNumber) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection(COLLECTION_NAME)
                .whereEqualTo("nomorTelepon", phoneNumber)
                .limit(1);

        return !query.get().get().isEmpty();
    }

    /**
     * Cek apakah email sudah terdaftar
     */
    public boolean checkEmailExists(String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1);

        return !query.get().get().isEmpty();
    }

    /**
     * Cek apakah user sudah pernah menyukai user lain
     */
    public boolean checkAlreadyLiked(String likedBy, String likedUser) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        return !db.collection("Likes")
                .whereEqualTo("likedBy", likedBy)
                .whereEqualTo("likedUser", likedUser)
                .get()
                .get()
                .isEmpty();
    }

    /**
     * Simpan like baru
     */
    public void addLike(String likedBy, String likedUser) {
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = Map.of(
                "likedBy", likedBy,
                "likedUser", likedUser,
                "timestamp", Timestamp.now()
        );

        db.collection("Likes").add(data);
    }

    /**
     * Cek apakah terjadi mutual like
     */
    public boolean checkMutualLike(String likedBy, String likedUser) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        return !db.collection("Likes")
                .whereEqualTo("likedBy", likedBy)
                .whereEqualTo("likedUser", likedUser)
                .get()
                .get()
                .isEmpty();
    }

    /**
     * Buat match jika belum ada antara dua user
     */
    public void createMatchIfNotExists(String user1, String user2) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        // Pastikan tidak duplicate
        List<String> usersOrdered = user1.compareTo(user2) < 0 ?
                List.of(user1, user2) : List.of(user2, user1);

        QuerySnapshot existing = db.collection("Matches")
                .whereEqualTo("users", usersOrdered)
                .get()
                .get();

        if (!existing.isEmpty()) return;

        // Ambil user info
        DocumentSnapshot user1Doc = db.collection(COLLECTION_NAME).document(user1).get().get();
        DocumentSnapshot user2Doc = db.collection(COLLECTION_NAME).document(user2).get().get();

        String name1 = user1Doc.getString("nama");
        String name2 = user2Doc.getString("nama");

        List<String> photos1 = (List<String>) user1Doc.get("photos");
        List<String> photos2 = (List<String>) user2Doc.get("photos");

        Match match = new Match();
        match.setUsers(usersOrdered);
        match.setUserNames(user1.compareTo(user2) < 0 ? List.of(name1, name2) : List.of(name2, name1));
        match.setUserPhotos(List.of(
                photos1 != null && !photos1.isEmpty() ? photos1.get(0) : "",
                photos2 != null && !photos2.isEmpty() ? photos2.get(0) : ""
        ));
        match.setTimestamp(Timestamp.now());

        db.collection("Matches").add(match);
    }
}
