package catchu_datingapp.CatchU_Web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import catchu_datingapp.CatchU_Web.model.ChatResponse;
import catchu_datingapp.CatchU_Web.model.Match;
import catchu_datingapp.CatchU_Web.model.Message;
import catchu_datingapp.CatchU_Web.model.MessageRequest;
import catchu_datingapp.CatchU_Web.model.User;
import catchu_datingapp.CatchU_Web.service.ChatService;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Get user profile for chat
    @GetMapping("/user/{userId}")
    public ResponseEntity<ChatResponse> getUserProfile(@PathVariable String userId) {
        try {
            User user = chatService.getUserProfile(userId);
            if (user != null) {
                return ResponseEntity.ok(new ChatResponse(true, "User profile retrieved successfully", user));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, "Error retrieving user profile: " + e.getMessage(), null));
        }
    }

    // Get messages for a match
    @GetMapping("/match/{matchId}/messages")
    public ResponseEntity<ChatResponse> getMessages(
            @PathVariable String matchId,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<Message> messages = chatService.getMessages(matchId, limit);
            return ResponseEntity.ok(new ChatResponse(true, "Messages retrieved successfully", messages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, "Error retrieving messages: " + e.getMessage(), null));
        }
    }

    // Send a message
    @PostMapping("/match/{matchId}/messages")
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable String matchId,
            @RequestBody MessageRequest messageRequest) {
        try {
            // Validate input
            if (messageRequest.getSenderId() == null || messageRequest.getSenderId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse(false, "Sender ID is required", null));
            }
            if (messageRequest.getMessage() == null || messageRequest.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse(false, "Message content is required", null));
            }

            Message message = new Message();
            message.setSenderId(messageRequest.getSenderId());
            message.setMessage(messageRequest.getMessage());
            String messageId = chatService.sendMessage(matchId, message);
            return ResponseEntity.ok(new ChatResponse(true, "Message sent successfully", messageId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, "Error sending message: " + e.getMessage(), null));
        }
    }

    // Mark messages as read
    @PutMapping("/match/{matchId}/messages/read")
    public ResponseEntity<ChatResponse> markMessagesAsRead(
            @PathVariable String matchId,
            @RequestBody Map<String, String> request) {
        try {
            String otherUserId = request.get("otherUserId");
            String currentUserId = request.get("currentUserId");

            if (otherUserId == null || currentUserId == null) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse(false, "Both otherUserId and currentUserId are required", null));
            }

            chatService.markMessagesAsRead(matchId, otherUserId, currentUserId);
            return ResponseEntity.ok(new ChatResponse(true, "Messages marked as read successfully", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, "Error marking messages as read: " + e.getMessage(), null));
        }
    }

    // Get match details
    @GetMapping("/match/{matchId}")
    public ResponseEntity<ChatResponse> getMatch(@PathVariable String matchId) {
        try {
            Match match = chatService.getMatch(matchId);
            if (match != null) {
                return ResponseEntity.ok(new ChatResponse(true, "Match retrieved successfully", match));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, "Error retrieving match: " + e.getMessage(), null));
        }
    }

    // Get all matches for a user
    @GetMapping("/user/{userId}/matches")
    public ResponseEntity<ChatResponse> getUserMatches(@PathVariable String userId) {
        try {
            List<Match> matches = chatService.getUserMatches(userId);
            return ResponseEntity.ok(new ChatResponse(true, "Matches retrieved successfully", matches));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, "Error retrieving matches: " + e.getMessage(), null));
        }
    }

    // Get unread message count
    @GetMapping("/match/{matchId}/unread-count/{userId}")
    public ResponseEntity<ChatResponse> getUnreadMessageCount(
            @PathVariable String matchId,
            @PathVariable String userId) {
        try {
            int count = chatService.getUnreadMessageCount(matchId, userId);
            return ResponseEntity.ok(new ChatResponse(true, "Unread count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse(false, "Error retrieving unread count: " + e.getMessage(), null));
        }
    }
}