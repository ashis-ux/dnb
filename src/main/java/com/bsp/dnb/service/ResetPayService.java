package com.bsp.dnb.service;

import com.bsp.dnb.dto.ResetPayStatusDto;

public interface ResetPayService {

    String resetPayData();

    ResetPayStatusDto getStatus();

}
