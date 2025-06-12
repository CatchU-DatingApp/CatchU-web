package catchu_datingapp.CatchU_Web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import catchu_datingapp.CatchU_Web.model.User;
import catchu_datingapp.CatchU_Web.service.FirestoreService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
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

    @PutMapping("/update-fields/{id}")
    public ResponseEntity<String> updateUserFields(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        try {
            firestoreService.updateUserFields(id, updates);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Gagal update data: " + e.getMessage());
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
    @GetMapping("/{id}/photos")
    public ResponseEntity<List<String>> getUserPhotos(@PathVariable String id) {
        try {
            User user = firestoreService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            List<String> photos = user.getPhotos() != null ? user.getPhotos() : new ArrayList<>();
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/{id}/photos/delete")
    public ResponseEntity<String> deleteUserPhoto(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        try {
            String photoUrl = (String) body.get("photoUrl");
            if (photoUrl == null || photoUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("photoUrl is required");
            }

            User user = firestoreService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            List<String> photos = user.getPhotos() != null ? user.getPhotos() : new ArrayList<>();
            if (photos.remove(photoUrl)) {
                Map<String, Object> update = Map.of("photos", photos);
                firestoreService.updateUserFields(id, update);
                return ResponseEntity.ok("Photo deleted successfully.");
            } else {
                return ResponseEntity.badRequest().body("Photo not found in user's photos.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Gagal menghapus foto: " + e.getMessage());
        }
    }
    @PostMapping("/{id}/photos")
    public ResponseEntity<String> addPhotoUrl(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        try {
            String photoUrl = (String) body.get("photoUrl");
            User user = firestoreService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            List<String> photos = user.getPhotos() != null ? user.getPhotos() : new ArrayList<>();
            if (!photos.contains(photoUrl)) {
                photos.add(photoUrl);
            }

            Map<String, Object> update = Map.of("photos", photos);
            firestoreService.updateUserFields(id, update);

            return ResponseEntity.ok("Photo URL added successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Gagal menambahkan foto: " + e.getMessage());
        }
    }

}
