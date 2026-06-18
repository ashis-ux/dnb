package com.bsp.dnb.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bsp.dnb.dto.PaybillReportDto;

public interface PaybillReportDao {

    Page<PaybillReportDto>
    getPaybillReport(
            Integer yymm,
            List<Integer> allowedCategories,
            int page,
            int size);

    List<PaybillReportDto>
    getPaybillReportForExport(
            Integer yymm,
            List<Integer> allowedCategories);

    boolean tableExists(
            String tableName);
}