package com.example.warmindone.addondetail;

public class AddonDetailModel {

    private String namaAddon;
    private int jumlah;

    public AddonDetailModel(
            String namaAddon,
            int jumlah
    ) {
        this.namaAddon = namaAddon;
        this.jumlah = jumlah;
    }

    public String getNamaAddon() {
        return namaAddon;
    }

    public int getJumlah() {
        return jumlah;
    }
}