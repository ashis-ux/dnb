package com.bsp.dnb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "DNBPBILL")
public class DnbPbill {

    @EmbeddedId
    private DnbPbillId id;

    @Column(name = "STIPEND")
    private Integer stipend;

    @Column(name = "ADJ")
    private Integer adj;

    @Column(name = "ITAXREC")
    private Integer itaxrec;

    @Column(name = "CESSREC")
    private Integer cessrec;

    @Column(name = "CESSADDL")
    private Integer cessaddl;

    @Column(name = "GPAY")
    private Integer gpay;

    @Column(name = "NPAY")
    private Integer npay;

    @Column(name = "HIGHER_TAX_IND")
    private Integer higherTaxInd;

    public DnbPbill() {
    }

    public DnbPbillId getId() {
        return id;
    }

    public void setId(DnbPbillId id) {
        this.id = id;
    }

    public Integer getStipend() {
        return stipend;
    }

    public void setStipend(Integer stipend) {
        this.stipend = stipend;
    }

    public Integer getAdj() {
        return adj;
    }

    public void setAdj(Integer adj) {
        this.adj = adj;
    }

    public Integer getItaxrec() {
        return itaxrec;
    }

    public void setItaxrec(Integer itaxrec) {
        this.itaxrec = itaxrec;
    }

    public Integer getCessrec() {
        return cessrec;
    }

    public void setCessrec(Integer cessrec) {
        this.cessrec = cessrec;
    }

    public Integer getCessaddl() {
        return cessaddl;
    }

    public void setCessaddl(Integer cessaddl) {
        this.cessaddl = cessaddl;
    }

    public Integer getGpay() {
        return gpay;
    }

    public void setGpay(Integer gpay) {
        this.gpay = gpay;
    }

    public Integer getNpay() {
        return npay;
    }

    public void setNpay(Integer npay) {
        this.npay = npay;
    }

    public Integer getHigherTaxInd() {
        return higherTaxInd;
    }

    public void setHigherTaxInd(Integer higherTaxInd) {
        this.higherTaxInd = higherTaxInd;
    }
}