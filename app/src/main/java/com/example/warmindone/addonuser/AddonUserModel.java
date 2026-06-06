package com.example.warmindone.addonuser;

import com.google.firebase.firestore.DocumentId;

public class AddonUserModel {

    @DocumentId
    private String id;

    private String nama_addon;
    private String fotoaddon;
    private Long harga;
    private Long stok;

    private boolean selected;

    public AddonUserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_addon() {
        return nama_addon;
    }

    public String getFotoaddon() {
        return fotoaddon;
    }

    public Long getHarga() {
        return harga;
    }

    public Long getStok() {
        return stok;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}