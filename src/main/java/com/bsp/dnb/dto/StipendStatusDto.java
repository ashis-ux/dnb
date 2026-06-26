package com.bsp.dnb.dto;

public class StipendStatusDto {

	 

	    private boolean authorized;

	    private boolean paybillGenerated;

	    private String message;

	    private Integer previousMonth;

	 

	public Integer getPreviousMonth() {
			return previousMonth;
		}

		public void setPreviousMonth(Integer previousMonth) {
			this.previousMonth = previousMonth;
		}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	public boolean isPaybillGenerated() {
		return paybillGenerated;
	}

	public void setPaybillGenerated(boolean paybillGenerated) {
		this.paybillGenerated = paybillGenerated;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    

}
