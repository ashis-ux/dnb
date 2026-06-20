package com.bsp.dnb.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.data.domain.Page;

import com.bsp.dnb.dto.AttendanceReportDto;

public interface AttendanceReportService {
	
	Page<AttendanceReportDto>
	getAttendanceReport(
	        Integer yymm,
	        int page,
	        int size);

	ByteArrayInputStream
	exportAttendanceReport(
	        Integer yymm);

	List<Integer>
	getAttendanceYymm();

}
