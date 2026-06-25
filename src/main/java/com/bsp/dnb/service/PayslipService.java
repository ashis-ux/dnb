package com.bsp.dnb.service;

import org.springframework.data.domain.Page;

import com.bsp.dnb.dto.PayslipDto;

import com.bsp.dnb.dto.PayslipSearchDto;

public interface PayslipService {
	
	public PayslipDto getPayslip(
	        Integer yymm,
	        Integer id);
	
	byte[] generatePayslip(
            Integer yymm,
            Integer id);
	
	Page<PayslipSearchDto> searchPayslips(

            Integer id,

            String pan,

            Integer yymm,

            int page,

            int size);

}
