package com.example.warmindone.pendapatan;

import com.google.firebase.Timestamp;

public class PendapatanModel {
    private String id_order;
    private Timestamp tanggal;
    private long total;

    public PendapatanModel() {
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
