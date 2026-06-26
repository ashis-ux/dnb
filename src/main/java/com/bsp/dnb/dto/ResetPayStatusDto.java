package com.bsp.dnb.dto;

public class ResetPayStatusDto {

    private boolean authorized;

    private boolean paybillExists;

    private Integer previousMonth;

    private String message;

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	public boolean isPaybillExists() {
		return paybillExists;
	}

	public void setPaybillExists(boolean paybillExists) {
		this.paybillExists = paybillExists;
	}

	public Integer getPreviousMonth() {
		return previousMonth;
	}

	public void setPreviousMonth(Integer previousMonth) {
		this.previousMonth = previousMonth;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    
}
