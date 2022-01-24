package com.ec.bgm.movil.application.model;

public class UserGuest {

    private String id_userGuest;
    private String id_rol;
    private String username;
    private String password;
    private String email;
    private long register_date;

    public UserGuest() {
    }

    public UserGuest(String id_userGuest, String id_rol, String username, String password, String email, long register_date) {
        this.id_userGuest = id_userGuest;
        this.id_rol = id_rol;
        this.username = username;
        this.password = password;
        this.email = email;
        this.register_date = register_date;
    }

    public String getId_userGuest() {
        return id_userGuest;
    }

    public void setId_userGuest(String id_userGuest) {
        this.id_userGuest = id_userGuest;
    }

    public String getId_rol() {
        return id_rol;
    }

    public void setId_rol(String id_rol) {
        this.id_rol = id_rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getRegister_date() {
        return register_date;
    }

    public void setRegister_date(long register_date) {
        this.register_date = register_date;
    }
}
