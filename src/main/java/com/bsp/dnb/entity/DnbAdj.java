package com.bsp.dnb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "DNBADJ")
public class DnbAdj extends AuditEntity{

    @EmbeddedId
    private DnbAdjId id;

    @Column(name = "AMT")
    private Integer amt;

    @Column(name = "DAYS")
    private Integer days;

    @Column(name = "STOPADJ_IND")
    private Integer stopAdjInd;

    @Column(name = "PAID_IND")
    private Integer paidInd;

    @Column(name = "CATG")
    private Integer catg;

    @Column(name = "YR")
    private Integer yr;
    
    public DnbAdj() {
    }

    public DnbAdjId getId() {
        return id;
    }

    public void setId(DnbAdjId id) {
        this.id = id;
    }

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getStopAdjInd() {
        return stopAdjInd;
    }

    public void setStopAdjInd(Integer stopAdjInd) {
        this.stopAdjInd = stopAdjInd;
    }

    public Integer getPaidInd() {
        return paidInd;
    }

    public void setPaidInd(Integer paidInd) {
        this.paidInd = paidInd;
    }

    public Integer getCatg() {
        return catg;
    }

    public void setCatg(Integer catg) {
        this.catg = catg;
    }

    public Integer getYr() {
        return yr;
    }

    public void setYr(Integer yr) {
        this.yr = yr;
    }
}