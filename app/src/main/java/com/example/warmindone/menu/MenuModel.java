package com.example.warmindone.menu;

public class MenuModel {

    private String id;
    private String namaMenu;
    private String kategoriId;
    private String namaKategori;
    private String imageUrl;

    public MenuModel() {
    }

    public String getId() {
        return id;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public String getKategoriId() {
        return kategoriId;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public void setKategoriId(String kategoriId) {
        this.kategoriId = kategoriId;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}