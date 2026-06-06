package com.example.warmindone.cart;

import com.example.warmindone.addonuser.AddonUserModel;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;

public class CartModel {

    @DocumentId
    private String id;

    private String id_menu;
    private String id_user;

    private Long jumlah;
    private Long harga;

    private String namaMenu;
    private String fotoMenu;

    private Long stokMenu;

    public Long getStokMenu() {
        return stokMenu;
    }

    public void setStokMenu(Long stokMenu) {
        this.stokMenu = stokMenu;
    }

    private ArrayList<AddonUserModel> addons =
            new ArrayList<>();

    public CartModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_menu() {
        return id_menu;
    }

    public String getId_user() {
        return id_user;
    }

    public Long getJumlah() {
        return jumlah;
    }

    public void setJumlah(Long jumlah) {
        this.jumlah = jumlah;
    }

    public Long getHarga() {
        return harga;
    }

    public void setHarga(Long harga) {
        this.harga = harga;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public String getFotoMenu() {
        return fotoMenu;
    }

    public void setFotoMenu(String fotoMenu) {
        this.fotoMenu = fotoMenu;
    }

    public ArrayList<AddonUserModel> getAddons() {
        return addons;
    }

    public void setAddons(
            ArrayList<AddonUserModel> addons) {
        this.addons = addons;
    }
}