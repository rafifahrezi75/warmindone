package com.example.warmindone.kategori;

public class KategoriModel {
    private String id;
    private String kategori;

    public KategoriModel() {}

    public KategoriModel(String id, String kategori) {
        this.id = id;
        this.kategori = kategori;
    }

    public String getId() { return id; }
    public String getKategori() { return kategori; }

    public void setId(String id) { this.id = id; }
    public void setKategori(String kategori) { this.kategori = kategori; }
}