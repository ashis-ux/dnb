package com.bsp.dnb.entity;


import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DNBMAST")
public class DnbMast {

    @Id
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "YYMM")
    private Integer yymm;

    @Column(name = "NAME", length = 50)
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOB")
    private Date dob;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOJ")
    private Date doj;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOS")
    private Date dos;

    @Column(name = "EMP_STATUS")
    private Integer empStatus;

    @Column(name = "STIPEND_RATE")
    private Integer stipendRate;

    @Column(name = "DAILY_RATE")
    private Integer dailyRate;

    @Column(name = "SEX_CODE", length = 1)
    private String sexCode;

    @Column(name = "BANK_CD", length = 3)
    private String bankCd;

    @Column(name = "BANK_ACNO", length = 17)
    private String bankAcno;

    @Column(name = "PAN", length = 10)
    private String pan;

    @Column(name = "CATG")
    private Integer catg;

    @Column(name = "CATG_DESC", length = 50)
    private String catgDesc;

    @Column(name = "SPECIALITY", length = 20)
    private String speciality;

    @Column(name = "TRG_DURATION")
    private Integer trgDuration;

    @Column(name = "STOP_PAY_IND")
    private Integer stopPayInd;

    @Column(name = "TUITION_FEE_IND")
    private Integer tuitionFeeInd;

    @Column(name = "DNB_TYPE", length = 5)
    private String dnbType;
    
    @Column(name = "MOBILE_NO", length = 15)
    private String mobileNo;

    @Column(name = "EMAIL_ID", length = 100)
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