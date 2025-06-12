package catchu_datingapp.CatchU_Web.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private boolean isRead;
    private String senderId;
    private String message;
    private Timestamp timestamp;



    public boolean getIsRead() { return isRead; }  // Penting: getIsRead, bukan isRead
    public void setIsRead(boolean isRead) { this.isRead = isRead; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }


    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

}
