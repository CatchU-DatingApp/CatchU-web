package catchu_datingapp.CatchU_Web.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private String id;
    private List<String> users;
    private List<String> userNames;
    private List<String> userPhotos;
    private String lastMessage;
    private String lastMessageSenderId;
    private Timestamp lastMessageTimestamp;
    private Timestamp timestamp;
}
