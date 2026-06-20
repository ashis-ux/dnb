package com.bsp.dnb.serviceImpl;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.DnbMastDto;
import com.bsp.dnb.entity.DnbMast;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.DuplicateResourceException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbMastRepository;
import com.bsp.dnb.repo.RoleCategoryRepository;
import com.bsp.dnb.service.DnbMastService;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DnbMastServiceImpl implements DnbMastService {
	
	@Value("${app.logged-in-role}")
	private Long loggedInRole;
	
	@Autowired
    private CategoryRepository categoryRepository;
	
	@Autowired
	private RoleCategoryRepository roleCategoryRepository;
	
	 

	@Autowired
    private  DnbMastRepository repository;

	private static final Logger log =
	        LoggerFactory.getLogger(DnbMastServiceImpl.class);
	
	
	public DnbMastDto save(DnbMastDto dto) {

	    log.info("Saving DNB record");

	    validateRequest(dto);
	    validateDob(dto.getDob());
	    validatePan(dto.getPan().trim().toUpperCase());
	    Integer maxId = repository.findMaxId();
	    Integer nextId = maxId + 1;
	    dto.setId(nextId);
	    LocalDate doj = dto.getDoj()
	            .toInstant()
	            .atZone(ZoneId.systemDefault())
	            .toLocalDate();
	    Integer yymm = Integer.parseInt(
	            doj.format(DateTimeFormatter.ofPattern("yyyyMM")));
	    dto.setYymm(yymm);
	    DnbMast entity = dtoToEntity(dto);
	    DnbMast savedEntity = repository.save(entity);
	    log.info("DNB record saved successfully with ID : {}", nextId);
	    return entityToDto(savedEntity);
	}

	@Override
	public DnbMastDto update(DnbMastDto dto) {
	    log.info("Updating DNB record with ID : {}", dto.getId());
	    if (dto.getId() == null) {
	        throw new BadRequestException("ID is mandatory for update");
	    }
	    if (!repository.existsById(dto.getId())) {
	        throw new ResourceNotFoundException(
	                "DNB record not found with ID : " + dto.getId());
	    }
	    validateRequest(dto);
        validateDob(dto.getDob());
	    LocalDate doj = dto.getDoj()
	            .toInstant()
	            .atZone(ZoneId.systemDefault())
	            .toLocalDate();
	    Integer yymm = Integer.parseInt(
	            doj.format(DateTimeFormatter.ofPattern("yyyyMM")));
	    dto.setYymm(yymm);
	    dto.setYymm(yymm);
	    DnbMast updatedEntity =
	            repository.save(dtoToEntity(dto));
	    log.info("DNB record updated successfully with ID : {}",
	            updatedEntity.getId());
	    return entityToDto(updatedEntity);
	}

    @Override
    public DnbMastDto findById(Integer id) {
        log.info("Fetching DNB record with ID : {}", id);
        DnbMast entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("DNB record not found with ID : {}", id);
                    return new ResourceNotFoundException(
                            "DNB record not found with ID : " + id);
                });
        validateCategoryAccess(entity.getCatg());
        log.info("DNB record fetched successfully with ID : {}", id);
        return entityToDto(entity);
    }

    @Override
    public Page<DnbMastDto> findAll(int page,
                                    int size) {

        log.info("Fetching DNB records. Page: {}, Size: {}",
                page, size);

        Pageable pageable =
                PageRequest.of(page, size);

        return repository.findAll(pageable)
                .map(this::entityToDto);
    }
    
    @Override
    public List<DnbMastDto> findEligibleForAttendance() {
        log.info("Fetching attendance eligible DNBs for role {}",
                loggedInRole);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date previousMonthStart =
                calendar.getTime();
        List<DnbMast> employees =
                repository.findEligibleForAttendance(
                        loggedInRole,
                        previousMonthStart);
        log.info("Fetching attendance eligible DNBs for role {}",
        		employees.size());
        return employees.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
 
    private DnbMastDto entityToDto(DnbMast entity) {

        DnbMastDto dto = new DnbMastDto();

        dto.setId(entity.getId());
        dto.setYymm(entity.getYymm());
        dto.setName(entity.getName());
        dto.setDob(entity.getDob());
        dto.setDoj(entity.getDoj());
        dto.setDos(entity.getDos());
        dto.setEmpStatus(entity.getEmpStatus());
        dto.setStipendRate(entity.getStipendRate());
        dto.setDailyRate(entity.getDailyRate());
        dto.setSexCode(entity.getSexCode());
        dto.setBankCd(entity.getBankCd());
        dto.setBankAcno(entity.getBankAcno());
        dto.setPan(entity.getPan());
        dto.setCatg(entity.getCatg());
        dto.setCatgDesc(entity.getCatgDesc());
        dto.setSpeciality(entity.getSpeciality());
        dto.setTrgDuration(entity.getTrgDuration());
        dto.setStopPayInd(entity.getStopPayInd());
//        dto.setTuitionFeeInd(entity.getTuitionFeeInd());
//        dto.setDnbType(entity.getDnbType());

        return dto;
    }
    

    private void validateCategoryAccess(
            Integer catg) {

        boolean allowed =
                roleCategoryRepository
                        .existsByRoleIdAndCatg(
                                loggedInRole,
                                catg);

        if (!allowed) {

            throw new BadRequestException(
                    "You are not authorized to access DNB records for Category : "
                            + catg);
        }
    }

    private DnbMast dtoToEntity(DnbMastDto dto) {

        DnbMast entity = new DnbMast();

        entity.setId(dto.getId());
        entity.setYymm(dto.getYymm());

        entity.setName(toUpper(dto.getName()));

        entity.setDob(dto.getDob());
        entity.setDoj(dto.getDoj());
        entity.setDos(dto.getDos());

        entity.setEmpStatus(dto.getEmpStatus());

        entity.setStipendRate(dto.getStipendRate());
        entity.setDailyRate(dto.getDailyRate());

        entity.setSexCode(toUpper(dto.getSexCode()));

        entity.setBankCd(toUpper(dto.getBankCd()));

        entity.setBankAcno(
                dto.getBankAcno() == null
                        ? null
                        : dto.getBankAcno().trim());

        entity.setPan(toUpper(dto.getPan()));

        entity.setCatg(dto.getCatg());

        entity.setCatgDesc(toUpper(dto.getCatgDesc()));

        entity.setSpeciality(toUpper(dto.getSpeciality()));

        entity.setTrgDuration(dto.getTrgDuration());

        entity.setStopPayInd(dto.getStopPayInd());

//        entity.setTuitionFeeInd(dto.getTuitionFeeInd());
//
//        entity.setDnbType(toUpper(dto.getDnbType()));

        return entity;
    }
    
    private void validateRequest(DnbMastDto dto) {

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is mandatory");
        }

        if (dto.getDoj() == null) {
            throw new BadRequestException("Date of Joining is mandatory");
        }

        if (dto.getSexCode() == null || dto.getSexCode().trim().isEmpty()) {
            throw new BadRequestException("Gender is mandatory");
        }

        if (dto.getPan() == null || dto.getPan().trim().isEmpty()) {
            throw new BadRequestException("PAN Number is mandatory");
        }
        
        String pan = dto.getPan().trim().toUpperCase();

        if (dto.getId() == null) {

            if (repository.existsByPanIgnoreCase(pan)) {

                log.error("PAN Number already exists : {}", pan);

                throw new DuplicateResourceException(
                        "PAN Number already exists : " + pan);
            }

        } else {

            if (repository.existsByPanIgnoreCaseAndIdNot(
                    pan,
                    dto.getId())) {

                log.error("PAN Number already exists : {}", pan);

                throw new DuplicateResourceException(
                        "PAN Number already exists : " + pan);
            }
        }

        if (dto.getBankCd() == null || dto.getBankCd().trim().isEmpty()) {
            throw new BadRequestException("Bank Code is mandatory");
        }

        if (dto.getBankAcno() == null || dto.getBankAcno().trim().isEmpty()) {
            throw new BadRequestException("Bank Account Number is mandatory");
        }

        if (dto.getCatg() == null) {

            throw new BadRequestException(
                    "Category is mandatory");
        }

        boolean allowed =
                roleCategoryRepository
                        .existsByRoleIdAndCatg(
                                loggedInRole,
                                dto.getCatg());

        if (!allowed) {

            throw new BadRequestException(
                    "You are not authorized to use Category : "
                            + dto.getCatg());
        }

        if (dto.getSpeciality() == null
                || dto.getSpeciality().trim().isEmpty()) {

            throw new BadRequestException("Speciality is mandatory");
        }

        validatePan(dto.getPan());

        validateAccountNumber(dto.getBankAcno());
    }
    
    private void validatePan(String pan) {

        String panRegex = "^[A-Z]{5}[0-9]{4}[A-Z]$";

        if (!pan.matches(panRegex)) {

            throw new BadRequestException(
                    "Invalid PAN Number format");
       }
    }
    
    private void validateAccountNumber(String accountNumber) {
        String accountRegex = "^\\d{9,17}$";
        if (!accountNumber.matches(accountRegex)) {
            throw new BadRequestException(
                    "Invalid Bank Account Number");
        }
    }
    
    private void validateDob(Date dob) {
        if (dob == null) {
            return;
        }
        LocalDate birthDate = dob.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();
        if (age < 18) {
            throw new BadRequestException(
                    "Age should be at least 18 years");
        }
        if (age > 100) {
            throw new BadRequestException(
                    "Age should not exceed 100 years");
        }
    }
    
    
    private String toUpper(String value) {

        return value == null
                ? null
                : value.trim().toUpperCase();
    }
}
