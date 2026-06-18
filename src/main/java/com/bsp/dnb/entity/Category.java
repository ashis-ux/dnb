package com.bsp.dnb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "DNB_CATG_MAST")
public class Category {

    @Id
    @Column(name = "CATG", nullable = false)
    private Integer catg;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

    @Column(name = "STIPEND")
    private Integer stipend;

    @Column(name = "YEAR")
    private Integer year;

    @Column(name = "TYPE", length = 5)
    private String type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE", referencedColumnName = "ID")
    private DnbRole dnbRole;

	public Integer getCatg() {
		return catg;
	}

	public void setCatg(Integer catg) {
		this.catg = catg;
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

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DnbRole getDnbRole() {
		return dnbRole;
	}

	public void setDnbRole(DnbRole dnbRole) {
		this.dnbRole = dnbRole;
	}

	public Category() {
	}
	public Category(Integer catg, String description, Integer stipend, Integer year, String type, DnbRole dnbRole) {
		super();
		this.catg = catg;
		this.description = description;
		this.stipend = stipend;
		this.year = year;
		this.type = type;
		this.dnbRole = dnbRole;
	}

}