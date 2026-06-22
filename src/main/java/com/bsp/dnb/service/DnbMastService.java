package com.bsp.dnb.service;
 
 

import java.util.List;

import org.springframework.data.domain.Page;
import com.bsp.dnb.dto.DnbMastDto;

public interface DnbMastService {

    DnbMastDto save(DnbMastDto dto);

    DnbMastDto update(DnbMastDto dto);

    DnbMastDto findById(Integer id);

    Page<DnbMastDto> findAll(int page,
            int size);
    
    List<DnbMastDto> findEligibleForAttendance();
    
    public DnbMastDto searchDnb(
            String searchValue);
    
     

//    void delete(Integer id);
}