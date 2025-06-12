package catchu_datingapp.CatchU_Web.model;

import com.google.cloud.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private String id;
    private List<String> userIds;
    private Timestamp createdAt;
    private Timestamp lastMessageTime;
    private String lastMessage;
    private String lastMessageSenderId;
}