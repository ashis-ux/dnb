package com.bsp.dnb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DNB_ROLE_CATEGORY")
public class RoleCategory {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "CATG")
    private Integer catg;

    public RoleCategory() {
    }

    public RoleCategory(
            Long id,
            Long roleId,
            Integer catg) {

        this.id = id;
        this.roleId = roleId;
        this.catg = catg;
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(
            Long roleId) {
        this.roleId = roleId;
    }

    public Integer getCatg() {
        return catg;
    }

    public void setCatg(
            Integer catg) {
        this.catg = catg;
    }
}