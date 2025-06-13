package catchu_datingapp.CatchU_Web.controller;

import catchu_datingapp.CatchU_Web.model.Match;
import catchu_datingapp.CatchU_Web.service.FirestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private FirestoreService firestoreService;

    @PostMapping
    public ResponseEntity<String> handleLike(@RequestBody Map<String, String> payload) {
        String likedBy = payload.get("likedBy");       // currentUser UID
        String likedUser = payload.get("likedUser");   // likedProfile UID

        if (likedBy == null || likedUser == null || likedBy.equals(likedUser)) {
            return ResponseEntity.badRequest().body("Invalid like data.");
        }

        try {
            boolean alreadyLiked = firestoreService.checkAlreadyLiked(likedBy, likedUser);
            if (alreadyLiked) {
                return ResponseEntity.ok("Already liked.");
            }

            firestoreService.addLike(likedBy, likedUser);

            boolean isMutual = firestoreService.checkMutualLike(likedUser, likedBy);
            if (isMutual) {
                firestoreService.createMatchIfNotExists(likedBy, likedUser);
                return ResponseEntity.ok("Match created.");
            }

            return ResponseEntity.ok("Like saved.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to handle like: " + e.getMessage());
        }
    }
}
