/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package catchu_datingapp.CatchU_Web.model;

/**
 *
 * @author valde
 */

import java.util.Date;
import java.util.List;


public class User {
    private String id;
    private String bio;
    private String email;
    private String faculty;
    private String gender;
    private String instagram;
    private List<String> interest;
    private Date lastLogin;
    private List<Double> location;
    private String nama;
    private String nomorTelepon;
    private List<String> photos;
    private int umur;
    private boolean verified;

    // GETTER & SETTER SEMUA FIELD
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public List<String> getInterest() { return interest; }
    public void setInterest(List<String> interest) { this.interest = interest; }

    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }

    public List<Double> getLocation() { return location; }
    public void setLocation(List<Double> location) { this.location = location; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public int getUmur() { return umur; }
    public void setUmur(int umur) { this.umur = umur; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}

