package com.bsp.dnb.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.BankCdsRepository;
import com.bsp.dnb.service.BankCdsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BankCdsServiceImpl
        implements BankCdsService {

    @Autowired
    private BankCdsRepository repository;

    @Override
    public Integer getBankCode(
            String bankName,
            String ifscCode) {

        log.info(
                "Fetching Bank Code. Bank Name : {}, IFSC : {}",
                bankName,
                ifscCode);

        validateRequest(
                bankName,
                ifscCode);

        Optional<Integer> bankCode =
                repository.findBankCode(
                        bankName,
                        ifscCode);

        if (bankCode.isEmpty()) {

            log.error(
                    "Bank not found. Bank Name : {}, IFSC : {}",
                    bankName,
                    ifscCode);

            throw new ResourceNotFoundException(
                    "Bank not found.");
        }

        log.info(
                "Bank Code fetched successfully : {}",
                bankCode.get());

        return bankCode.get();
    }

    private void validateRequest(
            String bankName,
            String ifscCode) {

        if (bankName == null
                || bankName.trim().isEmpty()) {

            log.error(
                    "Bank Name is empty.");

            throw new BadRequestException(
                    "Bank Name is mandatory.");
        }

        if (ifscCode == null
                || ifscCode.trim().isEmpty()) {

            log.error(
                    "IFSC Code is empty.");

            throw new BadRequestException(
                    "IFSC Code is mandatory.");
        }
    }
    
    @Override
    public List<String> getAllBankNames() {

        log.info("Fetching all bank names.");

        List<String> banks =
                repository.findAllBankNames();

        log.info(
                "Fetched {} bank names.",
                banks.size());

        return banks;
    }
    
    @Override
    public List<String> getBankNamesByIfsc(
            String ifscCode) {

        log.info(
                "Fetching bank names for IFSC : {}",
                ifscCode);

        if (ifscCode == null
                || ifscCode.trim().isEmpty()) {

            throw new BadRequestException(
                    "IFSC Code is mandatory.");
        }

        List<String> bankNames =
                repository.findDistinctBankNamesByIfsc(
                        ifscCode.trim());

        if (bankNames.isEmpty()) {

            log.error(
                    "No bank found for IFSC : {}",
                    ifscCode);

            throw new ResourceNotFoundException(
                    "Invalid IFSC Code.");
        }

        log.info(
                "{} bank name(s) found for IFSC {}",
                bankNames.size(),
                ifscCode);

        return bankNames;
    }

}