/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package catchu_datingapp.CatchU_Web.model;

/**
 *
 * @author valde
 */


import java.util.List;
import com.google.cloud.Timestamp;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class User {
    private String id;
    private String bio;
    private String email;
    private String faculty;
    private String gender;
    private String facebook;
    private String line;
    private String x;
    private String kodeOtp;
    private String instagram;
    private String whatsapp;
    private List<String> interest;
    private Timestamp lastLogin;
    private List<Double> location;
    private String nama;
    private String nomorTelepon;
    private List<String> photos;
    private int umur;
    private boolean verified;
    private Timestamp createdAt;


}

