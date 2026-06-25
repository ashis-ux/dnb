package com.bsp.dnb.service;

import java.util.List;

import com.bsp.dnb.dto.AdjustmentEntryDto;
import com.bsp.dnb.dto.DnbAdjDto;

public interface DnbAdjService {

//    DnbAdjDto save(
//            DnbAdjDto dto);
//
//    DnbAdjDto update(
//            DnbAdjDto dto);
//
//    DnbAdjDto findById(
//            Integer yymm,
//            Integer id,
//            Integer forym);

    List<DnbAdjDto> saveAll(
            List<DnbAdjDto> dtoList);

    List<AdjustmentEntryDto> getAdjustmentEntry(
            Integer yymm);
    
    Integer calculateAmount(
            Integer id,
            Integer forym,
            Integer days);
}