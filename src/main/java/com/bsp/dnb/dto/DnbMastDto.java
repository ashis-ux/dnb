package com.bsp.dnb.dto;


import java.util.Date;

public class DnbMastDto {

    private Integer id;
    private Integer yymm;
    private String name;
    private Date dob;
    private Date doj;
    private Date dos;
    private Integer empStatus;
    private Integer stipendRate;
    private Integer dailyRate;
    private String sexCode;
    private String bankCd;
    private String bankAcno;
    private String pan;
    public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	private Integer catg;
    private String catgDesc;
    private String speciality;
    private Integer trgDuration;
    private Integer stopPayInd;
    private Integer tuitionFeeInd;
    private String dnbType;
    private String ifscCode;

    private String bankName;
    
    private String mobileNo;

    private String emailId;
    
    
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getDoj() {
		return doj;
	}
	public void setDoj(Date doj) {
		this.doj = doj;
	}
	public Date getDos() {
		return dos;
	}
	public void setDos(Date dos) {
		this.dos = dos;
	}
	public Integer getEmpStatus() {
		return empStatus;
	}
	public void setEmpStatus(Integer empStatus) {
		this.empStatus = empStatus;
	}
	public Integer getStipendRate() {
		return stipendRate;
	}
	public void setStipendRate(Integer stipendRate) {
		this.stipendRate = stipendRate;
	}
	public Integer getDailyRate() {
		return dailyRate;
	}
	public void setDailyRate(Integer dailyRate) {
		this.dailyRate = dailyRate;
	}
	public String getSexCode() {
		return sexCode;
	}
	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
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
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
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
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public Integer getTrgDuration() {
		return trgDuration;
	}
	public void setTrgDuration(Integer trgDuration) {
		this.trgDuration = trgDuration;
	}
	public Integer getStopPayInd() {
		return stopPayInd;
	}
	public void setStopPayInd(Integer stopPayInd) {
		this.stopPayInd = stopPayInd;
	}
	public Integer getTuitionFeeInd() {
		return tuitionFeeInd;
	}
	public void setTuitionFeeInd(Integer tuitionFeeInd) {
		this.tuitionFeeInd = tuitionFeeInd;
	}
	public String getDnbType() {
		return dnbType;
	}
	public void setDnbType(String dnbType) {
		this.dnbType = dnbType;
	}

    
}
