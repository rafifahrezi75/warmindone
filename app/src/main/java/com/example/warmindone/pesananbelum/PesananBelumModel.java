package com.example.warmindone.pesananbelum;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class PesananBelumModel {

    private String id_order;
    private String id_user;
    private String status;
    private String metode;
    private Timestamp tanggal_order;

    @Exclude // Hindari crash otomatis jika tipe data di DB tidak konsisten
    private long total_harga;

    public PesananBelumModel() {
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetode() {
        return metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }

    public Timestamp getTanggal_order() {
        return tanggal_order;
    }

    public void setTanggal_order(Timestamp tanggal_order) {
        this.tanggal_order = tanggal_order;
    }

    @Exclude
    public long getTotal_harga() {
        return total_harga;
    }

    @Exclude
    public void setTotal_harga(long total_harga) {
        this.total_harga = total_harga;
    }
}