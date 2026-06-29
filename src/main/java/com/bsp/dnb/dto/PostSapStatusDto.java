package com.bsp.dnb.dto;

public class PostSapStatusDto {

	 private boolean authorized;

	    private boolean paybillGenerated;

	    private boolean sapPaymentAvailable;

	    private Integer previousMonth;

	    private String message;

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public Integer getPreviousMonth() {
        return previousMonth;
    }

    public void setPreviousMonth(Integer previousMonth) {
        this.previousMonth = previousMonth;
    }

    public boolean isPaybillGenerated() {
        return paybillGenerated;
    }

    public void setPaybillGenerated(boolean paybillGenerated) {
        this.paybillGenerated = paybillGenerated;
    }

    public boolean isSapPaymentAvailable() {
        return sapPaymentAvailable;
    }

    public void setSapPaymentAvailable(boolean sapPaymentAvailable) {
        this.sapPaymentAvailable = sapPaymentAvailable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}