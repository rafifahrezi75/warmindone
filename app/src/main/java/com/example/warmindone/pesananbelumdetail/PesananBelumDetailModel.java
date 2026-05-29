package com.example.warmindone.pesananbelumdetail;

import com.example.warmindone.addondetail.AddonDetailModel;

import java.util.ArrayList;

public class PesananBelumDetailModel {
    private String namaMenu;
    private int jumlah;
    private long subtotal;

    private ArrayList<AddonDetailModel> addons;

    public PesananBelumDetailModel(
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