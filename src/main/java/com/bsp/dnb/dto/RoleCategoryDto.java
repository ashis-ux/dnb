package com.bsp.dnb.dto;

public class RoleCategoryDto {

    private Long roleId;

    private Integer catg;

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