package com.bsp.dnb.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.data.domain.Page;

import com.bsp.dnb.dto.DnbMasterReportDto;

public interface ReportService {

	Page<DnbMasterReportDto>
	getDnbMasterReport(
	        Integer yymm,
	        int page,
	        int size);

    ByteArrayInputStream exportDnbMasterReport(Integer yymm);
    
    List<Integer> getAvailableYymm();
    
    
}