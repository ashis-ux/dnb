package com.bsp.dnb.service;

import com.bsp.dnb.dto.PostSapStatusDto;

public interface PostSapService {

	 PostSapStatusDto getStatus();

	    String processPayment();

}