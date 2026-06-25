package com.bsp.dnb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "BANK_CDS")
public class BankCds {

    @EmbeddedId
    private BankCdsId id;

    @Column(name = "BANK_NAME", length = 64)
    private String bankName;

    @Column(name = "CATG", length = 1)
    private String catg;

    @Column(name = "AC_NO", length = 16)
    private String acNo;

    @Column(name = "LOC_IND", length = 1)
    private String locInd;

    public BankCds() {
    }

    public BankCdsId getId() {
        return id;
    }

    public void setId(BankCdsId id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCatg() {
        return catg;
    }

    public void setCatg(String catg) {
        this.catg = catg;
    }

    public String getAcNo() {
        return acNo;
    }

    public void setAcNo(String acNo) {
        this.acNo = acNo;
    }

    public String getLocInd() {
        return locInd;
    }

    public void setLocInd(String locInd) {
        this.locInd = locInd;
    }

    // Convenience methods

    public Integer getBankCode() {
        return id != null ? id.getBankCode() : null;
    }

    public String getIfsCd() {
        return id != null ? id.getIfsCd() : null;
    }

    @Override
    public String toString() {
        return "BankCds [bankCode=" + getBankCode()
                + ", bankName=" + bankName
                + ", ifsCd=" + getIfsCd()
                + "]";
    }
}