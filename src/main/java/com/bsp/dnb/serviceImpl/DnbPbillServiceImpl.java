package com.bsp.dnb.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.DnbPbillDto;
import com.bsp.dnb.entity.DnbPbill;
import com.bsp.dnb.entity.DnbPbillId;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.DuplicateResourceException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.DnbPbillRepository;
import com.bsp.dnb.service.DnbPbillService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DnbPbillServiceImpl
        implements DnbPbillService {

    @Autowired
    private DnbPbillRepository repository;
    
    private static final Logger log =
	        LoggerFactory.getLogger(DnbMastServiceImpl.class);

    @Override
    public DnbPbillDto save(
            DnbPbillDto dto) {

        log.info(
                "Saving PBill for YYMM : {}, ID : {}",
                dto.getYymm(),
                dto.getId());

        validate(dto);

        DnbPbillId id =
                new DnbPbillId(
                        dto.getYymm(),
                        dto.getId());

        if (repository.existsById(id)) {

            throw new DuplicateResourceException(
                    "PBill already exists for ID : "
                            + dto.getId()
                            + " and YYMM : "
                            + dto.getYymm());
        }

        DnbPbill entity =
                dtoToEntity(dto);

        repository.save(entity);

        log.info(
                "PBill saved successfully");

        return entityToDto(entity);
    }

    @Override
    public DnbPbillDto update(
            DnbPbillDto dto) {

        log.info(
                "Updating PBill for YYMM : {}, ID : {}",
                dto.getYymm(),
                dto.getId());

        validate(dto);

        DnbPbillId id =
                new DnbPbillId(
                        dto.getYymm(),
                        dto.getId());

        DnbPbill entity =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "PBill not found"));

        entity.setStipend(dto.getStipend());
        entity.setAdj(dto.getAdj());
        entity.setItaxrec(dto.getItaxrec());
        entity.setCessrec(dto.getCessrec());
        entity.setCessaddl(dto.getCessaddl());
        entity.setGpay(dto.getGpay());
        entity.setNpay(dto.getNpay());
        entity.setHigherTaxInd(
                dto.getHigherTaxInd());

        repository.save(entity);

        log.info(
                "PBill updated successfully");

        return entityToDto(entity);
    }

    @Override
    public DnbPbillDto findById(
            Integer yymm,
            Integer id) {

        log.info(
                "Fetching PBill for YYMM : {}, ID : {}",
                yymm,
                id);

        DnbPbill entity =
                repository.findById(
                                new DnbPbillId(
                                        yymm,
                                        id))
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "PBill not found"));

        return entityToDto(entity);
    }

    @Override
    public List<DnbPbillDto> findByYymm(
            Integer yymm) {

        log.info(
                "Fetching PBills for YYMM : {}",
                yymm);

        List<DnbPbillDto> response =
                repository.findByIdYymm(yymm)
                        .stream()
                        .map(this::entityToDto)
                        .collect(Collectors.toList());

        log.info(
                "Fetched {} PBills",
                response.size());

        return response;
    }

    private void validate(
            DnbPbillDto dto) {

        if (dto.getYymm() == null) {

            throw new BadRequestException(
                    "YYMM is mandatory");
        }

        if (dto.getId() == null) {

            throw new BadRequestException(
                    "ID is mandatory");
        }
    }

    private DnbPbill dtoToEntity(
            DnbPbillDto dto) {

        DnbPbill entity =
                new DnbPbill();

        entity.setId(
                new DnbPbillId(
                        dto.getYymm(),
                        dto.getId()));

        entity.setStipend(dto.getStipend());
        entity.setAdj(dto.getAdj());
        entity.setItaxrec(dto.getItaxrec());
        entity.setCessrec(dto.getCessrec());
        entity.setCessaddl(dto.getCessaddl());
        entity.setGpay(dto.getGpay());
        entity.setNpay(dto.getNpay());
        entity.setHigherTaxInd(
                dto.getHigherTaxInd());

        return entity;
    }

    private DnbPbillDto entityToDto(
            DnbPbill entity) {

        DnbPbillDto dto =
                new DnbPbillDto();

        dto.setYymm(
                entity.getId().getYymm());

        dto.setId(
                entity.getId().getId());

        dto.setStipend(
                entity.getStipend());

        dto.setAdj(
                entity.getAdj());

        dto.setItaxrec(
                entity.getItaxrec());

        dto.setCessrec(
                entity.getCessrec());

        dto.setCessaddl(
                entity.getCessaddl());

        dto.setGpay(
                entity.getGpay());

        dto.setNpay(
                entity.getNpay());

        dto.setHigherTaxInd(
                entity.getHigherTaxInd());

        return dto;
    }
}