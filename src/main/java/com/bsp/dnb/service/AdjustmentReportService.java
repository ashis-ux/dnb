package com.bsp.dnb.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.data.domain.Page;

import com.bsp.dnb.dto.AdjustmentReportDto;

public interface AdjustmentReportService {

    Page<AdjustmentReportDto>
    getAdjustmentReport(
            Integer yymm,
            int page,
            int size);

    ByteArrayInputStream
    exportAdjustmentReport(
            Integer yymm);

    List<Integer>
    getAdjustmentYymm();
    
}