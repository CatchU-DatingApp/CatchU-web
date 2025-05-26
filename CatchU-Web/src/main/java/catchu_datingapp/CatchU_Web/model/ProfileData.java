package catchu_datingapp.CatchU_Web.model;

// ProfileData.java (DTO untuk response)
import java.util.List;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class ProfileData {
    private String userId;
    private String name;
    private List<String> images;
    private String distance;
    private String bio;
    private String faculty;
    private List<String> interests;
    private List<Double> location;
    private int matchingInterestsCount;

    // constructors, getters, setters
    public ProfileData() {}

    public ProfileData(String userId, String name, List<String> images, String distance,
                       String bio, String faculty, List<String> interests,
                       List<Double> location, int matchingInterestsCount) {
        this.userId = userId;
        this.name = name;
        this.images = images;
        this.distance = distance;
        this.bio = bio;
        this.faculty = faculty;
        this.interests = interests;
        this.location = location;
        this.matchingInterestsCount = matchingInterestsCount;
    }

}
