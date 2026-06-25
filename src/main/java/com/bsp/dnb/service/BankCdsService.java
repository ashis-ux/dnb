package com.bsp.dnb.service;

import java.util.List;

public interface BankCdsService {

    Integer getBankCode(
            String bankName,
            String ifscCode);
    
    List<String> getAllBankNames();
    
    List<String> getBankNamesByIfsc(
            String ifscCode);

}