package com.example.warmindone.history;

import java.util.ArrayList;

public class HistoryModel {

    private String idOrder;
    private String status;

    private String namaMenu;

    private Long jumlah;
    private Long totalHarga;

    private com.google.firebase.Timestamp tanggalOrder;

    private ArrayList<HistoryAddonModel>
            addons = new ArrayList<>();

    public HistoryModel() {
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public Long getJumlah() {
        return jumlah;
    }

    public void setJumlah(Long jumlah) {
        this.jumlah = jumlah;
    }

    public Long getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(Long totalHarga) {
        this.totalHarga = totalHarga;
    }

    public com.google.firebase.Timestamp getTanggalOrder() {
        return tanggalOrder;
    }

    public void setTanggalOrder(
            com.google.firebase.Timestamp tanggalOrder) {

        this.tanggalOrder = tanggalOrder;
    }

    public ArrayList<HistoryAddonModel> getAddons() {
        return addons;
    }

    public void setAddons(
            ArrayList<HistoryAddonModel> addons) {

        this.addons = addons;
    }
}