package catchu_datingapp.CatchU_Web.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import catchu_datingapp.CatchU_Web.model.User;
import catchu_datingapp.CatchU_Web.service.FirestoreService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*") // Boleh disesuaikan untuk keamanan
public class UserController {

    @Autowired
    private FirestoreService firestoreService;

    // Ambil semua user
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = firestoreService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Ambil user berdasarkan ID dokumen
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        try {
            User user = firestoreService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Simpan atau update user (POST)
    @PostMapping
    public ResponseEntity<String> saveOrUpdateUser(@RequestBody User user) {
        try {
            String userId = firestoreService.saveOrUpdateUser(user);
            return ResponseEntity.ok(userId);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().body("Gagal menyimpan data user.");
        }
    }

    // Hapus user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        try {
            String deletedId = firestoreService.deleteUser(id);
            return ResponseEntity.ok("User dengan ID " + deletedId + " berhasil dihapus.");
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().body("Gagal menghapus user.");
        }
    }
}
