package com.bsp.dnb.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "PAY_PYMT_DATA_DEV")
public class PayPymtData {

	@EmbeddedId
	private PayPymtDataId id;

    @Column(name = "XIREAD_FLAG", columnDefinition = "CHAR(1)")
    private String xiReadFlag;

    @Column(name = "DOC_TYPE", length = 4)
    private String docType;

    @Column(name = "POSTING_DATE", columnDefinition = "CHAR(8)")
    private String postingDate;

    @Column(name = "REF_TEXT", length = 16)
    private String refText;

    @Column(name = "HEADER_TEXT", length = 25)
    private String headerText;

    @Column(
    	    name = "ACCT_TYPE",
    	    columnDefinition = "CHAR(1)"
    	)
    	private String acctType;

    @Column(name = "DR_CR_ID" ,columnDefinition = "CHAR(1)")
    private String drCrId;

    @Column(name = "GL_ACCT", columnDefinition = "CHAR(7)")
    private String glAcct;

    @Column(name = "VENDOR_ACCT", length = 16)
    private String vendorAcct;

    @Column(name = "COST_CENTER", length = 10)
    private String costCenter;

    @Column(name = "ITEM_TEXT", length = 50)
    private String itemText;

    @Column(name = "AMOUNT", precision = 13, scale = 2)
    private BigDecimal amount;

    @Column(name = "CURR_KEY", length = 3)
    private String currKey;

    @Column(name = "BUSI_AREA", columnDefinition = "CHAR(4)")
    private String busiArea;

    @Column(name = "SP_GL_INDICATOR",  columnDefinition = "CHAR(1)")
    private String spGlIndicator;

    @Column(name = "ASSIGNMENT", length = 18)
    private String assignment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INS_DATE", nullable = false)
    private Date insDate;

    @Column(name = "PYMT_METHOD",  columnDefinition = "CHAR(1)")
    private String pymtMethod;

    @Column(name = "PYMT_BLOCK_KEY",  columnDefinition = "CHAR(1)")
    private String pymtBlockKey;

    public PayPymtData() {
    }

     

    public String getXiReadFlag() {
        return xiReadFlag;
    }

    public void setXiReadFlag(String xiReadFlag) {
        this.xiReadFlag = xiReadFlag;
    }

    public PayPymtDataId getId() {
        return id;
    }

    public void setId(
            PayPymtDataId id) {
        this.id = id;
    }

    public Integer getSlNo() {
        return id != null
                ? id.getSlNo()
                : null;
    }

    public String getDocNo() {
        return id != null
                ? id.getDocNo()
                : null;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getRefText() {
        return refText;
    }

    public void setRefText(String refText) {
        this.refText = refText;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public String getDrCrId() {
        return drCrId;
    }

    public void setDrCrId(String drCrId) {
        this.drCrId = drCrId;
    }

    public String getGlAcct() {
        return glAcct;
    }

    public void setGlAcct(String glAcct) {
        this.glAcct = glAcct;
    }

    public String getVendorAcct() {
        return vendorAcct;
    }

    public void setVendorAcct(String vendorAcct) {
        this.vendorAcct = vendorAcct;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrKey() {
        return currKey;
    }

    public void setCurrKey(String currKey) {
        this.currKey = currKey;
    }

    public String getBusiArea() {
        return busiArea;
    }

    public void setBusiArea(String busiArea) {
        this.busiArea = busiArea;
    }

    public String getSpGlIndicator() {
        return spGlIndicator;
    }

    public void setSpGlIndicator(String spGlIndicator) {
        this.spGlIndicator = spGlIndicator;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public Date getInsDate() {
        return insDate;
    }

    public void setInsDate(Date insDate) {
        this.insDate = insDate;
    }

    public String getPymtMethod() {
        return pymtMethod;
    }

    public void setPymtMethod(String pymtMethod) {
        this.pymtMethod = pymtMethod;
    }

    public String getPymtBlockKey() {
        return pymtBlockKey;
    }

    public void setPymtBlockKey(String pymtBlockKey) {
        this.pymtBlockKey = pymtBlockKey;
    }
}