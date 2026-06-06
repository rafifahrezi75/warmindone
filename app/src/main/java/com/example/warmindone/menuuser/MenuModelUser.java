package com.example.warmindone.menuuser;

import com.google.firebase.firestore.DocumentId;

public class MenuModelUser {

    @DocumentId
    private String id;

    private String nama_menu;
    private Long harga;
    private String foto;
    private String id_kategori;
    private String status;
    private Long stok;
    private String deskripsi;

    public MenuModelUser() {
    }

    public String getId() {
        return id;
    }

    public String getNama_menu() {
        return nama_menu;
    }

    public Long getHarga() {
        return harga;
    }

    public String getFoto() {
        return foto;
    }

    public String getId_kategori() {
        return id_kategori;
    }

    public String getStatus() {
        return status;
    }

    public Long getStok() {
        return stok;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
}