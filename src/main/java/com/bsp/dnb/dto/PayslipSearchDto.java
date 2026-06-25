package com.bsp.dnb.dto;


public class PayslipSearchDto {

    private Integer id;

    private String name;

    private String category;

    private Integer yymm;

    private String month;

    public PayslipSearchDto() {
    }

    public PayslipSearchDto(
            Integer id,
            String name,
            String category,
            Integer yymm) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.yymm = yymm;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getYymm() {
        return yymm;
    }

    public void setYymm(Integer yymm) {
        this.yymm = yymm;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}