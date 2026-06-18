package com.bsp.dnb.dto;

public class DnbPbillDto {

    private Integer yymm;
    private Integer id;

    private Integer stipend;
    private Integer adj;
    private Integer itaxrec;
    private Integer cessrec;
    private Integer cessaddl;
    private Integer gpay;
    private Integer npay;
    private Integer higherTaxInd;

    public DnbPbillDto() {
    }

    public Integer getYymm() {
        return yymm;
    }

    public void setYymm(Integer yymm) {
        this.yymm = yymm;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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