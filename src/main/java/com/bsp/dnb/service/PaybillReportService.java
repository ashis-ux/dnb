package com.bsp.dnb.service;

import java.io.ByteArrayInputStream;

import org.springframework.data.domain.Page;

import com.bsp.dnb.dto.PaybillReportDto;

public interface PaybillReportService {

	Page<PaybillReportDto>
    getPaybillReport(
            Integer yymm,
            int page,
            int size);

    ByteArrayInputStream
    exportPaybillReport(
            Integer yymm);
}