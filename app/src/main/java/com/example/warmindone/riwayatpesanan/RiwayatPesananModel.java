package com.example.warmindone.riwayatpesanan;

import java.util.ArrayList;

public class RiwayatPesananModel {

    private String idDetail;
    private String idMenu;

    private String namaMenu;
    private Long jumlah;

    private ArrayList<RiwayatPesananAddonModel>
            addons = new ArrayList<>();

    public RiwayatPesananModel() {
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
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

    public ArrayList<RiwayatPesananAddonModel> getAddons() {
        return addons;
    }

    public void setAddons(
            ArrayList<RiwayatPesananAddonModel> addons) {

        this.addons = addons;
    }
}