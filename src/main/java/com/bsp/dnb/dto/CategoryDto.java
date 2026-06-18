package com.bsp.dnb.dto;

public class CategoryDto {

    private Integer catg;
    private String description;
    private Integer stipend;
    private Integer year;
    private String type;

    private Long roleId;
    private String roleName;
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
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

     
}