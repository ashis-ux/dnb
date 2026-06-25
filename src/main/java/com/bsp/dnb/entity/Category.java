package com.bsp.dnb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "DNB_CATG_MAST")
public class Category {

    @EmbeddedId
    private CategoryId id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STIPEND")
    private Integer stipend;

    @Column(name = "TYPE")
    private String type;

    public CategoryId getId() {
        return id;
    }

    public void setId(CategoryId id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStipend() {
        return stipend;
    }

    public void setStipend(Integer stipend) {
        this.stipend = stipend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Convenience methods

    public Integer getCatg() {
        return id.getCatg();
    }

    public Integer getYear() {
        return id.getYear();
    }

}