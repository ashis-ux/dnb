package com.bsp.dnb.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;


@Builder
public class PayslipDto {

	   private Integer id;
	    private Integer yymm;          // YYYYMM
	    private String  mthYr;         // formatted "MON-YYYY"  e.g. MAY-2026
	 
	    /* ── Employee master ─────────────────────────────────────── */
	    private String name;
	    private String pan;
	    private String bankCd;
	    private String bankAcno;
	    private Integer catg;
	    private String catgDesc;
	    private String doj;            // formatted dd-MON-yyyy
	    private String dos;            // formatted dd-MON-yyyy
	 
	    /* ── Stipend / Rate info ─────────────────────────────────── */
	    private BigDecimal stipendRate;
	    private BigDecimal dailyRate;
	    private Integer    duty;       // attendance days
	 
	    /* ── Current month earnings / deductions ─────────────────── */
	    private BigDecimal stipend;
	    private BigDecimal adj;
	    private BigDecimal tds;        // itaxrec + cessrec + cessaddl
	 
	    /* ── Derived totals ──────────────────────────────────────── */
	    private BigDecimal grossPay;
	    private BigDecimal netPay;
	 
	    /* ── Cumulative figures ──────────────────────────────────── */
	    private BigDecimal cumGross;
	    private BigDecimal cumTax;     // CUM_ITAX + CUM_CESS + CUM_CESS_ADDL
	    private BigDecimal cumSavings; // cum_sav_80ccc + cum_sav_80ccf
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getYymm() {
			return yymm;
		}
		public void setYymm(Integer yymm) {
			this.yymm = yymm;
		}
		public String getMthYr() {
			return mthYr;
		}
		public void setMthYr(String mthYr) {
			this.mthYr = mthYr;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPan() {
			return pan;
		}
		public void setPan(String pan) {
			this.pan = pan;
		}
		public String getBankCd() {
			return bankCd;
		}
		public void setBankCd(String bankCd) {
			this.bankCd = bankCd;
		}
		public String getBankAcno() {
			return bankAcno;
		}
		public void setBankAcno(String bankAcno) {
			this.bankAcno = bankAcno;
		}
		public Integer getCatg() {
			return catg;
		}
		public void setCatg(Integer catg) {
			this.catg = catg;
		}
		public String getCatgDesc() {
			return catgDesc;
		}
		public void setCatgDesc(String catgDesc) {
			this.catgDesc = catgDesc;
		}
		public String getDoj() {
			return doj;
		}
		public void setDoj(String doj) {
			this.doj = doj;
		}
		public String getDos() {
			return dos;
		}
		public void setDos(String dos) {
			this.dos = dos;
		}
		public BigDecimal getStipendRate() {
			return stipendRate;
		}
		public void setStipendRate(BigDecimal stipendRate) {
			this.stipendRate = stipendRate;
		}
		public BigDecimal getDailyRate() {
			return dailyRate;
		}
		public void setDailyRate(BigDecimal dailyRate) {
			this.dailyRate = dailyRate;
		}
		public Integer getDuty() {
			return duty;
		}
		public void setDuty(Integer duty) {
			this.duty = duty;
		}
		public BigDecimal getStipend() {
			return stipend;
		}
		public void setStipend(BigDecimal stipend) {
			this.stipend = stipend;
		}
		public BigDecimal getAdj() {
			return adj;
		}
		public void setAdj(BigDecimal adj) {
			this.adj = adj;
		}
		public BigDecimal getTds() {
			return tds;
		}
		public void setTds(BigDecimal tds) {
			this.tds = tds;
		}
		public BigDecimal getGrossPay() {
			return grossPay;
		}
		public void setGrossPay(BigDecimal grossPay) {
			this.grossPay = grossPay;
		}
		public BigDecimal getNetPay() {
			return netPay;
		}
		public void setNetPay(BigDecimal netPay) {
			this.netPay = netPay;
		}
		public BigDecimal getCumGross() {
			return cumGross;
		}
		public void setCumGross(BigDecimal cumGross) {
			this.cumGross = cumGross;
		}
		public BigDecimal getCumTax() {
			return cumTax;
		}
		public void setCumTax(BigDecimal cumTax) {
			this.cumTax = cumTax;
		}
		public BigDecimal getCumSavings() {
			return cumSavings;
		}
		public void setCumSavings(BigDecimal cumSavings) {
			this.cumSavings = cumSavings;
		}
	    
	    
	 
    
}