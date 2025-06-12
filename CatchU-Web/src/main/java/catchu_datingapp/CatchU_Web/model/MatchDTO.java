package catchu_datingapp.CatchU_Web.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private String matchId;
    private String name;
    private String image;
    private String message;
    private String otherUserId;
    private Timestamp timestamp;
}

