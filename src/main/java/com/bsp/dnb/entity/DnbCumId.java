package com.bsp.dnb.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DnbCumId implements Serializable {

    @Column(name = "YYMM")
    private Integer yymm;

    public DnbCumId(Integer yymm, Integer id) {
		super();
		this.yymm = yymm;
		this.id = id;
	}
    
    public DnbCumId() {
		 
	}


	@Column(name = "ID")
    private Integer id;

	@Override
	public int hashCode() {
		return Objects.hash(id, yymm);
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DnbCumId other = (DnbCumId) obj;
		return Objects.equals(id, other.id) && Objects.equals(yymm, other.yymm);
	}
}
