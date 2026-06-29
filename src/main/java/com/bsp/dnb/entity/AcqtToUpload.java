package com.bsp.dnb.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACQT_TOUPLOAD")
public class AcqtToUpload {

    @Id
    @Column(name = "PERNO")
    private Integer perno;

    @Column(name = "AMT")
    private BigDecimal amt;
    
    @Column(name = "PAN", length = 10)
    private String pan;

    public AcqtToUpload() {
    }

    public Integer getPerno() {
        return perno;
    }

    public void setPerno(Integer perno) {
        this.perno = perno;
    }

    
    public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    @Override
    public String toString() {
        return "AcqtToUpload [perno=" + perno
                + ", amt=" + amt
                + ", pan=" + pan + "]";
    }
}