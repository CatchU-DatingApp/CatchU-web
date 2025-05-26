package catchu_datingapp.CatchU_Web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import catchu_datingapp.CatchU_Web.model.User;

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
            user.setId(document.getId());  // Set ID dokumen
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
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

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
            ApiFuture<WriteResult> future = newDoc.set(user);
            future.get(); // tunggu selesai
            return user.getId();
        } else {
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).set(user);
            future.get();
            return id;
        }
    }

    /**
     * Hapus user berdasarkan ID
     */
    public String deleteUser(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(id).delete();
        future.get();
        return id;
    }
}
