package com.example.warmindone.user;

public class UserModel {

    private String id;
    private String nama;
    private String email;
    private String no_telp;
    private String role;

    public UserModel() {
    }

    public UserModel(
            String id,
            String nama,
            String email,
            String no_telp,
            String role
    ) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.no_telp = no_telp;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public void setNo_telp(String no_telp) {
        this.no_telp = no_telp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}