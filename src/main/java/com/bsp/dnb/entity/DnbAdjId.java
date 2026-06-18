package com.bsp.dnb.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DnbAdjId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "YYMM")
    private Integer yymm;

    @Column(name = "ID")
    private Integer id;

    @Column(name = "FORYM")
    private Integer forym;

    public DnbAdjId() {
    }

    public DnbAdjId(
            Integer yymm,
            Integer id,
            Integer forym) {

        this.yymm = yymm;
        this.id = id;
        this.forym = forym;
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

    public Integer getForym() {
        return forym;
    }

    public void setForym(Integer forym) {
        this.forym = forym;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof DnbAdjId)) {
            return false;
        }

        DnbAdjId that =
                (DnbAdjId) o;

        return Objects.equals(yymm, that.yymm)
                && Objects.equals(id, that.id)
                && Objects.equals(forym, that.forym);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                yymm,
                id,
                forym);
    }
}