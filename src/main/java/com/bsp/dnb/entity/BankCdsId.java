package com.bsp.dnb.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BankCdsId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "BANK_CODE")
    private Integer bankCode;

    @Column(name = "IFS_CD", length = 11)
    private String ifsCd;

    public BankCdsId() {
    }

    public BankCdsId(
            Integer bankCode,
            String ifsCd) {

        this.bankCode = bankCode;
        this.ifsCd = ifsCd;
    }

    public Integer getBankCode() {
        return bankCode;
    }

    public void setBankCode(Integer bankCode) {
        this.bankCode = bankCode;
    }

    public String getIfsCd() {
        return ifsCd;
    }

    public void setIfsCd(String ifsCd) {
        this.ifsCd = ifsCd;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof BankCdsId))
            return false;

        BankCdsId that = (BankCdsId) o;

        return Objects.equals(bankCode, that.bankCode)
                && Objects.equals(ifsCd, that.ifsCd);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                bankCode,
                ifsCd);
    }
}