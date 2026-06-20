package com.bsp.dnb.serviceImpl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.AttendanceEntryDto;
import com.bsp.dnb.dto.DnbAttDto;
import com.bsp.dnb.dto.DnbMastDto;
import com.bsp.dnb.entity.DnbAtt;
import com.bsp.dnb.entity.DnbAttId;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.DuplicateResourceException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.DnbAttRepository;
import com.bsp.dnb.repo.DnbMastRepository;
import com.bsp.dnb.repo.DnbPbillRepository;
import com.bsp.dnb.service.DnbAttService;
import com.bsp.dnb.service.DnbMastService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DnbAttServiceImpl implements DnbAttService {

	private static final Logger log =
	        LoggerFactory.getLogger(DnbAttServiceImpl.class);
    @Autowired
    private DnbAttRepository repository;

    @Autowired
    private DnbMastRepository dnbMastRepository;
    
    @Autowired
    private DnbMastService dnbMastService;
    
    @Autowired
    private DnbPbillRepository pbillRepository;

    

    @Override
    @Transactional
    public List<DnbAttDto> saveAll(
            List<DnbAttDto> dtoList) {

        log.info(
                "Bulk attendance save started");

        if (dtoList == null ||
                dtoList.isEmpty()) {

            throw new BadRequestException(
                    "Attendance list cannot be empty");
        }

        Integer yymm =
                dtoList.get(0).getYymm();

         
        if (pbillRepository.existsByIdYymm(yymm)) {
            throw new BadRequestException(
                    "Paybill has already been generated for YYMM : "
                            + yymm
                            + ". Attendance cannot be modified.");
        }

        List<DnbAttDto> response =
                new ArrayList<>();

        for (DnbAttDto dto : dtoList) {

            int total=validateAttendance(dto);
            DnbAttId id =
                    new DnbAttId(
                            dto.getYymm(),
                            dto.getId());
            
            //if the Total number of 
            if (total == 0) {

                if (repository.existsById(id)) {
                    repository.deleteById(id);
                    log.info(
                            "Attendance deleted for ID : {}, YYMM : {} because total days is 0",
                            dto.getId(),
                            dto.getYymm());
                }

                continue;
            }
            DnbAtt entity;
            /*
             * UPDATE
             */
            if (repository.existsById(id)) {

                entity =
                        repository.findById(id)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Attendance not found"));

                log.info(
                        "Updating attendance for ID : {}, YYMM : {}",
                        dto.getId(),
                        dto.getYymm());
            }

            /*
             * INSERT
             */
            else {

                entity =
                        new DnbAtt();

                entity.setId(id);
                

                log.info(
                        "Creating attendance for ID : {}, YYMM : {}",
                        dto.getId(),
                        dto.getYymm());
            }

            entity.setDuty(dto.getDuty());
            entity.setAl(dto.getAl());
            entity.setCl(dto.getCl());
            entity.setPl(dto.getPl());
            entity.setMl(dto.getMl());
            entity.setAbs(dto.getAbs());

            entity =
                    repository.save(entity);

            response.add(
                    entityToDto(entity));
        }

        log.info(
                "Bulk attendance save completed. Total records saved : {}",
                response.size());

        return response;
    }
    
    private int validateAttendance(
            DnbAttDto dto) {

        DnbMastDto employee =
                dnbMastService.findById(
                        dto.getId());

        int eligibleDays =
                calculateEligibleDays(
                        employee,
                        dto.getYymm());

        int total =
                nvl(dto.getDuty())
                + nvl(dto.getAl())
                + nvl(dto.getCl())
                + nvl(dto.getPl())
                + nvl(dto.getMl())
                + nvl(dto.getAbs());
        
        if (total==0)return total;

        if (total != eligibleDays) {

            throw new BadRequestException(
                    "Attendance total for ID : "
                            + dto.getId()
                            + " must be "
                            + eligibleDays
                            + " days. Entered : "
                            + total);
        }
        return total;
    }
    
    private int calculateEligibleDays(
            DnbMastDto employee,
            Integer yymm) {

        YearMonth ym =
                YearMonth.of(
                        yymm / 100,
                        yymm % 100);

        LocalDate start =
                ym.atDay(1);

        LocalDate end =
                ym.atEndOfMonth();

        /*
         * DOJ
         */
        if (employee.getDoj() != null) {

            LocalDate doj =
                    ((java.sql.Date)
                            employee.getDoj())
                            .toLocalDate();

            if (doj.isAfter(start)) {

                start = doj;
            }
        }

        /*
         * DOS
         */
        if (employee.getDos() != null) {

            LocalDate dos =
                    ((java.sql.Date)
                            employee.getDos())
                            .toLocalDate();

            if (dos.isBefore(end)) {

                end = dos;
            }
        }

        if (start.isAfter(end)) {

            return 0;
        }

        return (int)
                ChronoUnit.DAYS.between(
                        start,
                        end)
                + 1;
    }
    
    private int nvl(
            Integer value) {

        return value == null
                ? 0
                : value;
    }
    
    @Override
    public List<DnbAttDto> findByYymm(Integer yymm) {

        log.info("Fetching attendance records for YYMM : {}", yymm);

        validateYymm(yymm);

        List<DnbAttDto> response =
                repository.findByIdYymm(yymm)
                        .stream()
                        .map(this::entityToDto)
                        .toList();

        log.info("Fetched {} attendance records for YYMM : {}",
                response.size(),
                yymm);

        return response;
    }
    
    @Override
    public List<AttendanceEntryDto> getAttendanceEntry(
            Integer yymm) {

        log.info(
                "Preparing attendance entry for YYMM : {}",
                yymm);

        validateYymm(yymm);

        boolean paybillGenerated =
                pbillRepository.existsByIdYymm(yymm);

        log.info(
                "Paybill generated for YYMM {} : {}",
                yymm,
                paybillGenerated);

        List<DnbMastDto> eligibleEmployees =
                dnbMastService.findEligibleForAttendance();
        log.info(
        	    "Employee Count : {}",
        	    eligibleEmployees.size());
        eligibleEmployees =
                new ArrayList<>(
                        eligibleEmployees.stream()
                                .collect(Collectors.toMap(
                                        DnbMastDto::getId,
                                        Function.identity(),
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new))
                                .values());

        log.info(
                "Fetched {} eligible employees",
                eligibleEmployees.size());

        List<DnbAtt> existingAttendance =
                repository.findByIdYymm(yymm);

        log.info(
                "Fetched {} existing attendance records",
                existingAttendance.size());

        if (paybillGenerated &&
                existingAttendance.isEmpty()) {

            throw new BadRequestException(
                    "Invalid state. Paybill exists but attendance "
                            + "does not exist for YYMM : "
                            + yymm);
        }

        Map<Integer, DnbAtt> attendanceMap =
                existingAttendance.stream()
                        .collect(Collectors.toMap(
                                att -> att.getId().getId(),
                                Function.identity()));

        YearMonth yearMonth =
                YearMonth.of(
                        yymm / 100,
                        yymm % 100);

        LocalDate monthStart =
                yearMonth.atDay(1);

        LocalDate monthEnd =
                yearMonth.atEndOfMonth();

        List<AttendanceEntryDto> response =
                new ArrayList<>();

        for (DnbMastDto employee : eligibleEmployees) {

            AttendanceEntryDto dto =
                    new AttendanceEntryDto();

            dto.setYymm(yymm);

            dto.setId(employee.getId());

            dto.setName(employee.getName());

            dto.setDoj(employee.getDoj());

            dto.setEditable(
                    paybillGenerated ? 0 : 1);

            /*
             * Calculate Eligible Days
             */
            LocalDate effectiveStart =
                    monthStart;

            LocalDate effectiveEnd =
                    monthEnd;

            if (employee.getDoj() != null) {

                LocalDate doj =
                        ((java.sql.Date)
                                employee.getDoj())
                                .toLocalDate();

                if (doj.isAfter(monthStart)) {

                    effectiveStart = doj;
                }
            }

            if (employee.getDos() != null) {

                LocalDate dos =
                        ((java.sql.Date)
                                employee.getDos())
                                .toLocalDate();

                if (dos.isBefore(monthEnd)) {

                    effectiveEnd = dos;
                }
            }

            int eligibleDays = 0;

            if (!effectiveStart.isAfter(
                    effectiveEnd)) {

                eligibleDays =
                        (int) ChronoUnit.DAYS.between(
                                effectiveStart,
                                effectiveEnd)
                        + 1;
            }

            dto.setEligibleDays(
                    eligibleDays);

            DnbAtt attendance =
                    attendanceMap.get(
                            employee.getId());

            /*
             * Existing Attendance
             */
            if (attendance != null) {

                dto.setDuty(
                        attendance.getDuty());

                dto.setAl(
                        attendance.getAl());

                dto.setCl(
                        attendance.getCl());

                dto.setPl(
                        attendance.getPl());

                dto.setMl(
                        attendance.getMl());

                dto.setAbs(
                        attendance.getAbs());
            }

            /*
             * New Attendance
             */
            else {

                /*
                 * Default Duty =
                 * Eligible Days
                 */
            	
            	dto.setEligibleDays(eligibleDays);
                dto.setDuty(
                        0);

                dto.setAl(0);

                dto.setCl(0);

                dto.setPl(0);

                dto.setMl(0);

                dto.setAbs(0);
            }
            response.add(dto);
        }

        log.info(
                "Prepared {} attendance rows for YYMM : {}",
                response.size(),
                yymm);

        return response;
    }


    private void validateRequest(DnbAttDto dto) {
        if (dto.getYymm() == null) {
            throw new BadRequestException(
                    "YYMM is mandatory");
        }
        validateYymm(dto.getYymm());
        if (dto.getId() == null) {
            throw new BadRequestException(
                    "ID is mandatory");
        }
        validateAttendance(dto.getDuty(), "DUTY");
        validateAttendance(dto.getAl(), "AL");
        validateAttendance(dto.getCl(), "CL");
        validateAttendance(dto.getAbs(), "ABS");
        validateAttendance(dto.getPl(), "PL");
        validateAttendance(dto.getMl(), "ML");
        validateTotalDays(dto);
    }

    private void validateYymm(Integer yymm) {
        int year = yymm / 100;
        int month = yymm % 100;
        if (month < 1 || month > 12) {
            throw new BadRequestException(
                    "Invalid YYMM format");
        }
        if (year < 1970 || year > 2100) {
            throw new BadRequestException(
                    "Invalid year in YYMM");
        }
    }

    private void validateAttendance(Integer value,
                                    String fieldName) {

        if (value != null &&
                (value < 0 || value > 31)) {
            throw new BadRequestException(
                    fieldName
                            + " should be between 0 and 31");
        }
    }

    private void validateTotalDays(DnbAttDto dto) {
        int total =
                defaultValue(dto.getDuty()) +
                defaultValue(dto.getAl()) +
                defaultValue(dto.getCl()) +
                defaultValue(dto.getAbs()) +
                defaultValue(dto.getPl()) +
                defaultValue(dto.getMl());

        int year = dto.getYymm() / 100;
        int month = dto.getYymm() % 100;
        int maxDays =
                YearMonth.of(year, month)
                        .lengthOfMonth();
        if (total > maxDays) {
            throw new BadRequestException(
                    "Total attendance days (" + total +
                            ") cannot exceed " + maxDays);
        }
    }

    private int defaultValue(Integer value) {
        return value == null ? 0 : value;
    }

    private DnbAttDto entityToDto(DnbAtt entity) {

        DnbAttDto dto = new DnbAttDto();

        dto.setYymm(entity.getId().getYymm());
        dto.setId(entity.getId().getId());

        dto.setDuty(entity.getDuty());
        dto.setAl(entity.getAl());
        dto.setCl(entity.getCl());
        dto.setAbs(entity.getAbs());
        dto.setPl(entity.getPl());
        dto.setMl(entity.getMl());

        return dto;
    }

    private DnbAtt dtoToEntity(DnbAttDto dto) {

        DnbAtt entity = new DnbAtt();

        entity.setId(
                new DnbAttId(
                        dto.getYymm(),
                        dto.getId()));

        entity.setDuty(dto.getDuty());
        entity.setAl(dto.getAl());
        entity.setCl(dto.getCl());
        entity.setAbs(dto.getAbs());
        entity.setPl(dto.getPl());
        entity.setMl(dto.getMl());

        return entity;
    }
}