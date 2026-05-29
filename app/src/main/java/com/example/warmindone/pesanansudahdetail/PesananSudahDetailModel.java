package com.example.warmindone.pesanansudahdetail;

import com.example.warmindone.addondetail.AddonDetailModel;

import java.util.ArrayList;

public class PesananSudahDetailModel {

    private String namaMenu;
    private int jumlah;
    private long subtotal;

    private ArrayList<AddonDetailModel> addons;

    public PesananSudahDetailModel(
            String namaMenu,
            int jumlah,
            long subtotal,
            ArrayList<AddonDetailModel> addons
    ) {
        this.namaMenu = namaMenu;
        this.jumlah = jumlah;
        this.subtotal = subtotal;
        this.addons = addons;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public int getJumlah() {
        return jumlah;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public ArrayList<AddonDetailModel> getAddons() {
        return addons;
    }
}