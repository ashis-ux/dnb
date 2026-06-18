package com.bsp.dnb.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CategoryId
        implements Serializable {

    @Column(name = "CATG")
    private Integer catg;

    @Column(name = "YEAR")
    private Integer year;

	public Integer getCatg() {
		return catg;
	}

	public void setCatg(Integer catg) {
		this.catg = catg;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		return Objects.hash(catg, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryId other = (CategoryId) obj;
		return Objects.equals(catg, other.catg) && Objects.equals(year, other.year);
	}

}