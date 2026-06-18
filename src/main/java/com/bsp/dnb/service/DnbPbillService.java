package com.bsp.dnb.service;

import java.util.List;

import com.bsp.dnb.dto.DnbPbillDto;

public interface DnbPbillService {

    DnbPbillDto save(
            DnbPbillDto dto);

    DnbPbillDto update(
            DnbPbillDto dto);

    DnbPbillDto findById(
            Integer yymm,
            Integer id);

    List<DnbPbillDto> findByYymm(
            Integer yymm);

}