package com.bsp.dnb.serviceImpl;

 

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.bsp.dnb.entity.AuditEntity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class AuditListener {
	
	@Autowired
	private SecurityUtil SecurityUtil;
	

	public void setSecurityUtil(SecurityUtil securityUtil) {
		SecurityUtil = securityUtil;
	}

	@PrePersist
    public void prePersist(
            AuditEntity entity) {

        Date now = new Date();

        String user =
                SecurityUtil.getLoggedInUser();

        entity.setCreatedBy(user);

        entity.setCreatedDate(now);

        entity.setUpdatedBy(user);

        entity.setUpdatedDate(now);
    }

    @PreUpdate
    public void preUpdate(
            AuditEntity entity) {

        entity.setUpdatedBy(
                SecurityUtil.getLoggedInUser());

        entity.setUpdatedDate(
                new Date());
    }
}
