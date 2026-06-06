package com.example.warmindone.historyuser;

import com.example.warmindone.historyuser.DetailHistoryAddonModel;

import java.util.ArrayList;

public class DetailHistoryModel {

    private String idDetail;
    private String idMenu;
    private String namaMenu;
    private Long jumlah;

    private ArrayList<DetailHistoryAddonModel>
            addons = new ArrayList<>();

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

    public ArrayList<DetailHistoryAddonModel> getAddons() {
        return addons;
    }

    public void setAddons(
            ArrayList<DetailHistoryAddonModel> addons) {

        this.addons = addons;
    }
}