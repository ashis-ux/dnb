package com.bsp.dnb.dto;

import java.util.Date;

public class AdjustmentEntryDto {

    private Integer yymm;

    private Integer id;

    private String name;

    private Integer forym;

    private Integer days;

    private Integer catg;

    private Integer yr;

    private Integer amt;

    private Integer stopAdjInd;

    private Integer paidInd;

    private Integer editable;
    
    private Date doj;
    
    private Integer originalForym;

    public Integer getOriginalForym() {
		return originalForym;
	}

	public void setOriginalForym(Integer originalForym) {
		this.originalForym = originalForym;
	}

	public Date getDoj() {
		return doj;
	}

	public void setDoj(Date doj) {
		this.doj = doj;
	}

	public AdjustmentEntryDto() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getForym() {
        return forym;
    }

    public void setForym(Integer forym) {
        this.forym = forym;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
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

    public Integer getEditable() {
        return editable;
    }

    public void setEditable(Integer editable) {
        this.editable = editable;
    }
}