package catchu_datingapp.CatchU_Web.controller;

import catchu_datingapp.CatchU_Web.model.ProfileData;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import com.google.firebase.cloud.FirestoreClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
public class ProfileController {

    // Fungsi menghitung jarak (Haversine formula) dalam km
    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius bumi dalam km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Helper method parsing list location yang mungkin berisi String atau Number
    private List<Double> parseLocationList(Object locationObj) {
        if (!(locationObj instanceof List<?>)) return null;

        List<?> locList = (List<?>) locationObj;
        if (locList.size() < 2) return null;

        List<Double> result = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Object val = locList.get(i);
            Double d = parseDoubleSafe(val);
            if (d == null) return null;
            result.add(d);
        }
        return result;
    }

    private Double parseDoubleSafe(Object val) {
        if (val == null) return null;
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        if (val instanceof String) {
            String s = ((String) val).trim().replace(",", ".");
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Failed to parse double from string: " + s);
                return null;
            }
        }
        return null;
    }

    @GetMapping("/profiles")
    public List<ProfileData> fetchProfiles(@RequestHeader("Firebase-UID") String currentUserId) throws Exception {
        Firestore firestore = FirestoreClient.getFirestore();

        // Ambil data current user dari Firestore
        DocumentReference currentUserDocRef = firestore.collection("Users").document(currentUserId);
        DocumentSnapshot currentUserDoc = currentUserDocRef.get().get();

        if (!currentUserDoc.exists()) {
            throw new Exception("Data user tidak ditemukan");
        }

        Map<String, Object> currentUserData = currentUserDoc.getData();

        if (currentUserData == null || !currentUserData.containsKey("location")) {
            throw new Exception("Data lokasi user tidak ditemukan");
        }

        Object currentUserLocObj = currentUserData.get("location");
        List<Double> currentUserLocation = parseLocationList(currentUserLocObj);
        if (currentUserLocation == null) {
            throw new Exception("Data lokasi user tidak valid atau tidak ditemukan");
        }

        String currentUserGender = (String) currentUserData.get("gender");
        if (currentUserGender == null || currentUserGender.isEmpty()) {
            throw new Exception("Data gender user tidak ditemukan");
        }

        @SuppressWarnings("unchecked")
        List<String> currentUserInterests = (List<String>) currentUserData.getOrDefault("interest", new ArrayList<>());

        String targetGender = currentUserGender.equalsIgnoreCase("male") ? "female" : "male";

        // Ambil semua user dari koleksi Users
        ApiFuture<QuerySnapshot> future = firestore.collection("Users").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<ProfileData> profiles = new ArrayList<>();
        int skippedUsers = 0;

        for (DocumentSnapshot doc : documents) {
            try {
                if (doc.getId().equals(currentUserId)) {
                    skippedUsers++;
                    continue;
                }

                Map<String, Object> userData = doc.getData();

                if (userData == null) {
                    skippedUsers++;
                    continue;
                }

                String userGender = userData.getOrDefault("gender", "").toString().toLowerCase();
                if (!userGender.equals(targetGender)) {
                    skippedUsers++;
                    continue;
                }

                @SuppressWarnings("unchecked")
                List<String> userInterests = (List<String>) userData.getOrDefault("interest", new ArrayList<>());

                // Matching interests
                List<String> matchingInterests = new ArrayList<>();
                for (String interest : userInterests) {
                    if (currentUserInterests.contains(interest)) {
                        matchingInterests.add(interest);
                    }
                }

                Object userLocObj = userData.get("location");
                List<Double> userLocation = parseLocationList(userLocObj);
                if (userLocation == null) {
                    skippedUsers++;
                    continue;
                }
                if (userLocation.get(0) == 0.0 && userLocation.get(1) == 0.0) {
                    skippedUsers++;
                    continue;
                }

                double distanceInKm = calculateDistanceKm(
                        currentUserLocation.get(0),
                        currentUserLocation.get(1),
                        userLocation.get(0),
                        userLocation.get(1)
                );
                String distanceStr = String.format("%.1f km", distanceInKm);

                String name = (String) userData.get("nama");
                if (name == null || name.trim().isEmpty()) {
                    skippedUsers++;
                    continue;
                }

                @SuppressWarnings("unchecked")
                List<String> photos = (List<String>) userData.getOrDefault("photos", new ArrayList<>());

                String bio = (String) userData.getOrDefault("bio", "");
                String faculty = (String) userData.getOrDefault("faculty", "");

                profiles.add(new ProfileData(
                        doc.getId(),
                        name,
                        photos,
                        distanceStr,
                        bio,
                        faculty,
                        userInterests,
                        userLocation,
                        matchingInterests.size()
                ));
            } catch (Exception e) {
                skippedUsers++;
                System.out.println("Error parsing user doc id=" + doc.getId() + ": " + e.getMessage());
            }
        }

        // Sort profiles: matchingInterestsCount desc, distance asc
        profiles.sort((a, b) -> {
            int interestCompare = Integer.compare(b.getMatchingInterestsCount(), a.getMatchingInterestsCount());
            if (interestCompare != 0) return interestCompare;

            // Perbaikan parsing distance:
            double distA = Double.parseDouble(a.getDistance().replace(" km", "").replace(",", "."));
            double distB = Double.parseDouble(b.getDistance().replace(" km", "").replace(",", "."));
            return Double.compare(distA, distB);
        });

        System.out.println("Total users fetched: " + documents.size());
        System.out.println("Users skipped: " + skippedUsers);
        System.out.println("Users shown: " + profiles.size());

        return profiles;
    }
}
