package com.bsp.dnb.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DnbPbillId implements Serializable {

    @Column(name = "YYMM")
    private Integer yymm;

    @Column(name = "ID")
    private Integer id;

    public DnbPbillId() {
    }

    public DnbPbillId(Integer yymm,
                      Integer id) {

        this.yymm = yymm;
        this.id = id;
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

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof DnbPbillId))
            return false;

        DnbPbillId that =
                (DnbPbillId) o;

        return Objects.equals(
                yymm,
                that.yymm)
                &&
                Objects.equals(
                        id,
                        that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                yymm,
                id);
    }
}