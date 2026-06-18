package com.bsp.dnb.dto;

import java.util.Date;

public class AttendanceEntryDto {

    private Integer yymm;
    private Integer id;

    private String name;
    private Date doj;

    private Integer duty;
    private Integer al;
    private Integer cl;
    private Integer abs;
    private Integer pl;
    private Integer ml;
    private Integer editable;
    private Integer eligibleDays;

    public Integer getEligibleDays() {
        return eligibleDays;
    }

    public void setEligibleDays(Integer eligibleDays) {
        this.eligibleDays = eligibleDays;
    }
    
    
	public Integer getEditable() {
		return editable;
	}
	public void setEditable(Integer editable) {
		this.editable = editable;
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
	public Date getDoj() {
		return doj;
	}
	public void setDoj(Date doj) {
		this.doj = doj;
	}
	public Integer getDuty() {
		return duty;
	}
	public void setDuty(Integer duty) {
		this.duty = duty;
	}
	public Integer getAl() {
		return al;
	}
	public void setAl(Integer al) {
		this.al = al;
	}
	public Integer getCl() {
		return cl;
	}
	public void setCl(Integer cl) {
		this.cl = cl;
	}
	public Integer getAbs() {
		return abs;
	}
	public void setAbs(Integer abs) {
		this.abs = abs;
	}
	public Integer getPl() {
		return pl;
	}
	public void setPl(Integer pl) {
		this.pl = pl;
	}
	public Integer getMl() {
		return ml;
	}
	public void setMl(Integer ml) {
		this.ml = ml;
	}

    
}
