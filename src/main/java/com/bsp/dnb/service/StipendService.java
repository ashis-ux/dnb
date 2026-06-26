package com.bsp.dnb.service;

import com.bsp.dnb.dto.StipendStatusDto;

public interface StipendService {

    String createStipend();
    
    public StipendStatusDto getStatus();

}