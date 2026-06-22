package com.bsp.dnb.service;

import java.util.List;

import com.bsp.dnb.dto.DnbRoleDto;

public interface DnbRoleService {

    DnbRoleDto save(DnbRoleDto dto);

    DnbRoleDto update(DnbRoleDto dto);

    DnbRoleDto findById(Long id);

    List<DnbRoleDto> findAll();
    
    Long getRoleId();
}