package catchu_datingapp.CatchU_Web.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequest {
    private String senderId;
    private String receiverId;
    private String message;
    private String type;
}