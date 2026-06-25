package com.bsp.dnb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "DNBCUM")
public class DnbCum {

    @EmbeddedId
    private DnbCumId id;

    @Column(name = "CUM_GR")
    private Integer cumGr;

    @Column(name = "CUM_SAV_80CCC")
    private Integer cumSav80ccc;

    @Column(name = "CUM_ITAX")
    private Integer cumItax;

    @Column(name = "CUM_CESS")
    private Integer cumCess;

    @Column(name = "CUM_CESS_ADDL")
    private Integer cumCessAddl;

    @Column(name = "CUM_SAV_80CCF")
    private Integer cumSav80ccf;

    @Column(name = "CUM_SAV_80D")
    private Integer cumSav80d;

    @Column(name = "CUM_SAV_80DD")
    private Integer cumSav80dd;

    @Column(name = "CUM_SAV_80DDB")
    private Integer cumSav80ddb;

    @Column(name = "CUM_SAV_80U")
    private Integer cumSav80u;

    @Column(name = "CUM_SAV_80E")
    private Integer cumSav80e;

	public DnbCumId getId() {
		return id;
	}

	public void setId(DnbCumId id) {
		this.id = id;
	}

	public Integer getCumGr() {
		return cumGr;
	}

	public void setCumGr(Integer cumGr) {
		this.cumGr = cumGr;
	}

	public Integer getCumSav80ccc() {
		return cumSav80ccc;
	}

	public void setCumSav80ccc(Integer cumSav80ccc) {
		this.cumSav80ccc = cumSav80ccc;
	}

	public Integer getCumItax() {
		return cumItax;
	}

	public void setCumItax(Integer cumItax) {
		this.cumItax = cumItax;
	}

	public Integer getCumCess() {
		return cumCess;
	}

	public void setCumCess(Integer cumCess) {
		this.cumCess = cumCess;
	}

	public Integer getCumCessAddl() {
		return cumCessAddl;
	}

	public void setCumCessAddl(Integer cumCessAddl) {
		this.cumCessAddl = cumCessAddl;
	}

	public Integer getCumSav80ccf() {
		return cumSav80ccf;
	}

	public void setCumSav80ccf(Integer cumSav80ccf) {
		this.cumSav80ccf = cumSav80ccf;
	}

	public Integer getCumSav80d() {
		return cumSav80d;
	}

	public void setCumSav80d(Integer cumSav80d) {
		this.cumSav80d = cumSav80d;
	}

	public Integer getCumSav80dd() {
		return cumSav80dd;
	}

	public void setCumSav80dd(Integer cumSav80dd) {
		this.cumSav80dd = cumSav80dd;
	}

	public Integer getCumSav80ddb() {
		return cumSav80ddb;
	}

	public void setCumSav80ddb(Integer cumSav80ddb) {
		this.cumSav80ddb = cumSav80ddb;
	}

	public Integer getCumSav80u() {
		return cumSav80u;
	}

	public void setCumSav80u(Integer cumSav80u) {
		this.cumSav80u = cumSav80u;
	}

	public Integer getCumSav80e() {
		return cumSav80e;
	}

	public void setCumSav80e(Integer cumSav80e) {
		this.cumSav80e = cumSav80e;
	}

     
}