package com.bsp.dnb.service;

import java.util.List;

import com.bsp.dnb.dto.AttendanceEntryDto;
import com.bsp.dnb.dto.DnbAttDto;

public interface DnbAttService {
    
    List<DnbAttDto> saveAll(List<DnbAttDto> dtoList);
    
    
    List<DnbAttDto> findByYymm(Integer yymm);
    
    public List<AttendanceEntryDto> getAttendanceEntry(Integer yymm);
 

}