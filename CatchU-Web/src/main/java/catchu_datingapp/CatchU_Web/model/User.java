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
    private String instagram;
    private List<String> interest;
    private Date lastLogin;
    private List<Double> location;
    private String nama;
    private String nomorTelepon;
    private List<String> photos;
    private int umur;
    private boolean verified;

}

