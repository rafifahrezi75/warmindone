package com.example.warmindone.addon;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class AddonModel {

    @DocumentId
    private String id;
    
    @PropertyName("nama_addon")
    private String nama_addon;
    
    @PropertyName("fotoaddon")
    private String fotoaddon;
    
    @PropertyName("stok")
    private int stok;
    
    @PropertyName("harga")
    private int harga;

    public AddonModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("nama_addon")
    public String getNama_addon() {
        return nama_addon;
    }

    @PropertyName("nama_addon")
    public void setNama_addon(String nama_addon) {
        this.nama_addon = nama_addon;
    }

    @PropertyName("fotoaddon")
    public String getFotoaddon() {
        return fotoaddon;
    }

    @PropertyName("fotoaddon")
    public void setFotoaddon(String fotoaddon) {
        this.fotoaddon = fotoaddon;
    }

    @PropertyName("stok")
    public int getStok() {
        return stok;
    }

    @PropertyName("stok")
    public void setStok(int stok) {
        this.stok = stok;
    }

    @PropertyName("harga")
    public int getHarga() {
        return harga;
    }

    @PropertyName("harga")
    public void setHarga(int harga) {
        this.harga = harga;
    }
}