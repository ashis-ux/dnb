package com.bsp.dnb.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PayPymtDataId
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "SL_NO")
    private Integer slNo;

    @Column(name = "DOC_NO", length = 10)
    private String docNo;

    public PayPymtDataId() {
    }

    public PayPymtDataId(
            Integer slNo,
            String docNo) {

        this.slNo = slNo;
        this.docNo = docNo;
    }

    public Integer getSlNo() {
        return slNo;
    }

    public void setSlNo(
            Integer slNo) {
        this.slNo = slNo;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(
            String docNo) {
        this.docNo = docNo;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PayPymtDataId)) {
            return false;
        }

        PayPymtDataId other =
                (PayPymtDataId) obj;

        return Objects.equals(slNo, other.slNo)
                && Objects.equals(docNo, other.docNo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                slNo,
                docNo);
    }

}