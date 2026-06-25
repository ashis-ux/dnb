package com.bsp.dnb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DNBATT")
public class DnbAtt extends AuditEntity{

    @EmbeddedId
    private DnbAttId id;

    @Column(name = "DUTY")
    private Integer duty;

    @Column(name = "AL")
    private Integer al;

    @Column(name = "CL")
    private Integer cl;

    @Column(name = "ABS")
    private Integer abs;

    @Column(name = "PL")
    private Integer pl;

    @Column(name = "ML")
    private Integer ml;

    public DnbAtt() {
    }

    public DnbAttId getId() {
        return id;
    }

    public void setId(DnbAttId id) {
        this.id = id;
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

	@Override
	public String toString() {
		return "DnbAtt [id=" + id + ", duty=" + duty + ", al=" + al + ", cl=" + cl + ", abs=" + abs + ", pl=" + pl
				+ ", ml=" + ml + "]";
	}
    
    
}
