package com.bsp.dnb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DNB_MONTHLY_UPDATE_AUDIT")
public class DnbMonthlyUpdateAudit extends AuditEntity {

    @Id
    @Column(name = "YYMM")
    private Integer yymm;

    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    public Integer getYymm() {
        return yymm;
    }

    public void setYymm(Integer yymm) {
        this.yymm = yymm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}