package com.bsp.dnb.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.AdjustmentEntryDto;
import com.bsp.dnb.dto.DnbAdjDto;
import com.bsp.dnb.dto.DnbMastDto;
import com.bsp.dnb.entity.Category;
import com.bsp.dnb.entity.DnbAdj;
import com.bsp.dnb.entity.DnbAdjId;
import com.bsp.dnb.entity.DnbMast;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbAdjRepository;
import com.bsp.dnb.repo.DnbMastRepository;
import com.bsp.dnb.repo.DnbPbillRepository;
import com.bsp.dnb.service.DnbAdjService;
import com.bsp.dnb.service.DnbMastService;

import jakarta.transaction.Transactional;

@Service
public class DnbAdjServiceImpl implements DnbAdjService {
	
	private static final Logger log =
	        LoggerFactory.getLogger(DnbAdjServiceImpl.class);
	
	@Autowired
	private DnbAdjRepository repository;

	@Autowired
	private DnbMastService dnbMastService;
	
	@Autowired
	private DnbMastRepository dnbMastRepository;

	@Autowired
	private DnbPbillRepository pbillRepository;

	@Autowired
	private CategoryRepository catgRepository;
	
	@Override
	public List<AdjustmentEntryDto> getAdjustmentEntry(
	        Integer yymm) {
	    log.info(
	            "Preparing adjustment entry for YYMM : {}",
	            yymm);
	    validateYymm(yymm);
	    boolean paybillGenerated =
	            pbillRepository.existsByIdYymm(
	                    yymm);
	    List<DnbMastDto> employees =
	            dnbMastService.findEligibleForAttendance();
	    
	    employees =
	            new ArrayList<>(
	                    employees.stream()
	                            .collect(
	                                    Collectors.toMap(
	                                            DnbMastDto::getId,
	                                            Function.identity(),
	                                            (existing, duplicate) -> existing,
	                                            LinkedHashMap::new))
	                            .values());
	    List<DnbAdj> adjustments =
	            repository.findByIdYymm(
	                    yymm);
	    Map<Integer, List<DnbAdj>> adjustmentMap =
	            adjustments.stream()
	                    .collect(
	                            Collectors.groupingBy(
	                                    a -> a.getId().getId()));
	    List<AdjustmentEntryDto> response =
	            new ArrayList<>();
	    for (DnbMastDto emp : employees) {

	        List<DnbAdj> empAdjustments =
	                adjustmentMap.get(emp.getId());

	        if (empAdjustments != null &&
	            !empAdjustments.isEmpty()) {

	            for (DnbAdj adj : empAdjustments) {

	                AdjustmentEntryDto dto =
	                        new AdjustmentEntryDto();

	                dto.setYymm(yymm);
	                dto.setId(emp.getId());
	                dto.setName(emp.getName());
	                dto.setDoj(emp.getDoj());

	                dto.setEditable(
	                        paybillGenerated ? 0 : 1);

	                dto.setOriginalForym(
	                        adj.getId().getForym());

	                dto.setForym(
	                        adj.getId().getForym());

	                dto.setDays(
	                        adj.getDays());

	                dto.setCatg(
	                        adj.getCatg());

	                dto.setCatg_desc(
	                        emp.getCatgDesc());

	                dto.setYr(
	                        adj.getYr());

	                dto.setAmt(
	                        adj.getAmt());

	                dto.setStopAdjInd(
	                        adj.getStopAdjInd());

	                dto.setPaidInd(
	                        adj.getPaidInd());

	                response.add(dto);
	            }

	        } else {

	            AdjustmentEntryDto dto =
	                    new AdjustmentEntryDto();

	            dto.setYymm(yymm);
	            dto.setId(emp.getId());
	            dto.setName(emp.getName());
	            dto.setDoj(emp.getDoj());

	            dto.setEditable(
	                    paybillGenerated ? 0 : 1);

	            dto.setCatg(emp.getCatg());
	            dto.setCatg_desc(emp.getCatgDesc());

	            dto.setDays(0);
	            dto.setAmt(0);
	            dto.setStopAdjInd(0);
	            dto.setPaidInd(0);

	            response.add(dto);
	        }
	        
	    }

	    return response;
	}
	
	@Override
	@Transactional
	public List<DnbAdjDto> saveAll(
	        List<DnbAdjDto> dtoList) {
	    log.info(
	            "Bulk adjustment save started");

	    if (dtoList == null ||
	            dtoList.isEmpty()) {

	        throw new BadRequestException(
	                "Adjustment list cannot be empty");
	    }

	    Integer yymm =
	            dtoList.get(0)
	                    .getYymm();

	    if (pbillRepository.existsByIdYymm(
	            yymm)) {

	        throw new BadRequestException(
	                "Paybill has already been generated for YYMM : "
	                        + yymm);
	    }

	    List<DnbAdjDto> response =
	            new ArrayList<>();

	    for (DnbAdjDto dto : dtoList) {
	    	
	    	if (dto.getDays() == null
	                || dto.getDays() <= 0) {
	            log.info(
	                    "Skipping adjustment for ID : {} FORYM : {} because Days is zero",
	                    dto.getId(),
	                    dto.getForym());
	            continue;
	    	}

	        validateAdjustment(dto);
	        DnbAdjId oldId =
	                new DnbAdjId(
	                        dto.getYymm(),
	                        dto.getId(),
	                        dto.getOriginalForym() == null
	                                ? dto.getForym()
	                                : dto.getOriginalForym());

	        boolean existingRecord =
	                repository.existsById(
	                        oldId);

	        if (existingRecord
	                && !oldId.getForym()
	                        .equals(
	                                dto.getForym())) {

	            repository.deleteById(
	                    oldId);

	            log.info(
	                    "Deleted old adjustment record. FORYM changed from {} to {}",
	                    oldId.getForym(),
	                    dto.getForym());
	        }

	        DnbAdjId newId =
	                new DnbAdjId(
	                        dto.getYymm(),
	                        dto.getId(),
	                        dto.getForym());

	        DnbAdj entity =
	                repository.findById(
	                        newId)
	                        .orElse(
	                                new DnbAdj());

	        entity.setId(
	                newId);

	        entity.setDays(
	                dto.getDays());

	        entity.setCatg(
	                dto.getCatg());

	        entity.setYr(
	                dto.getYr());

	        entity.setAmt(
	        		 dto.getAmt());

	        entity.setStopAdjInd(
	                dto.getStopAdjInd());

	        entity.setPaidInd(
	                dto.getPaidInd());

	        repository.save(
	                entity);
	        response.add(
	                entityToDto(entity));
	    }

	    log.info(
	            "Bulk adjustment save completed");

	    return response;
	}
	
	
	
	private void validateAdjustment(
	        DnbAdjDto dto) {

	    /*
	     * FORYM < YYMM
	     */
	    if (dto.getForym() >=
	            dto.getYymm()) {

	        throw new BadRequestException(
	                "For Month must be less than Adjustment Month");
	    }

	    validatePeriod(
	            dto.getCatg(),
	            dto.getYr());

	    validateDays(
	            dto.getForym(),
	            dto.getDays());
	}
	
	private void validatePeriod(
	        Integer catg,
	        Integer yr) {

	    if (yr == null) {
	        return;
	    }

	    if (List.of(
	            1, 2, 3, 4)
	            .contains(catg)
	            && yr > 1) {

	        throw new BadRequestException(
	                "Period Yr cant be more than 1 year for selected category");
	    }

	    if (List.of(
	            0, 5)
	            .contains(catg)
	            && yr > 3) {

	        throw new BadRequestException(
	                "Period Yr cant be more than 3 years for selected category");
	    }

	    if (catg == 6 &&
	            yr > 2) {

	        throw new BadRequestException(
	                "Period Yr cant be more than 2 years for selected category");
	    }
	}
	
	private void validateDays(
	        Integer forym,
	        Integer days) {

	    YearMonth ym =
	            YearMonth.of(
	                    forym / 100,
	                    forym % 100);

	    int maxDays =
	            ym.lengthOfMonth();

	    if (days > maxDays) {

	        throw new BadRequestException(
	                "Days cannot exceed "
	                        + maxDays
	                        + " for month "
	                        + forym);
	    }
	}
	
	private Integer calculateAmount(
	        DnbAdjDto dto) {

	    if (dto.getAmt() != null &&
	            dto.getAmt() > 0) {

	        return dto.getAmt();
	    }
	    Integer stipend =
	    		catgRepository.findStipend(
	                    dto.getCatg(),
	                    dto.getYr());

	    if (stipend == null) {

	        throw new BadRequestException(
	                "Stipend not found for Category : "
	                        + dto.getCatg()
	                        + " Year : "
	                        + dto.getYr());
	    }

	    YearMonth ym =
	            YearMonth.of(
	                    dto.getForym() / 100,
	                    dto.getForym() % 100);

	    int monthDays =
	            ym.lengthOfMonth();

	    return (int)
	            Math.round(
	                    ((double) stipend
	                            / monthDays)
	                            * dto.getDays());
	}
	
	private void validateYymm(
	        Integer yymm) {
	    if (yymm == null) {

	        throw new BadRequestException(
	                "YYMM is mandatory");
	    }
	    String value =
	            yymm.toString();
	    if (value.length() != 6) {
	        throw new BadRequestException(
	                "Invalid YYMM format");
	    }
	    int month =
	            Integer.parseInt(
	                    value.substring(4));
	    if (month < 1 ||
	            month > 12) {
	        throw new BadRequestException(
	                "Invalid month in YYMM");
	    }
	}
	
	private DnbAdjDto entityToDto(
	        DnbAdj entity) {

	    DnbAdjDto dto =
	            new DnbAdjDto();

	    dto.setYymm(
	            entity.getId()
	                    .getYymm());

	    dto.setId(
	            entity.getId()
	                    .getId());

	    dto.setForym(
	            entity.getId()
	                    .getForym());

	    dto.setAmt(
	            entity.getAmt());

	    dto.setDays(
	            entity.getDays());

	    dto.setStopAdjInd(
	            entity.getStopAdjInd());

	    dto.setPaidInd(
	            entity.getPaidInd());

	    dto.setCatg(
	            entity.getCatg());

	    dto.setYr(
	            entity.getYr());

	    return dto;
	}
	
	
	@Override
	public Integer calculateAmount(
	        Integer id,
	        Integer forym,
	        Integer days) {

	    log.info(
	            "Calculating adjustment amount. Employee={}, FORYM={}, Days={}",
	            id,
	            forym,
	            days);

	    if (days == null || days <= 0) {

	        log.warn("Invalid adjustment days : {}", days);

	        return 0;
	    }

	    DnbMast employee =
	            dnbMastRepository.findById(id)
	                    .orElseThrow(() -> {

	                        log.error(
	                                "Employee not found : {}",
	                                id);

	                        return new RuntimeException(
	                                "Employee not found");
	                    });

	    Date dojDate = employee.getDoj();

	    LocalDate doj =
	            Instant.ofEpochMilli(dojDate.getTime())
	                   .atZone(ZoneId.systemDefault())
	                   .toLocalDate();
	    LocalDate monthStart =
	            YearMonth.of(
	                    forym / 100,
	                    forym % 100)
	                    .atDay(1);

	    int totalDaysInMonth =
	            monthStart.lengthOfMonth();

	    log.info(
	            "Employee DOJ={}, Category={}, Month={}, DaysInMonth={}",
	            doj,
	            employee.getCatg(),
	            monthStart,
	            totalDaysInMonth);

	    List<Category> slabs =
	    		catgRepository
	                    .findByCatg(
	                            employee.getCatg());
	    
	    log.info(
	            "Loaded {} stipend slabs : {}",slabs);

	    if (slabs.isEmpty()) {

	        log.error(
	                "No stipend slab configured for category {}",
	                employee.getCatg());

	        throw new RuntimeException(
	                "No stipend configured for category");
	    }

	    Map<Integer, Integer> stipendMap =
	            slabs.stream()
	                    .collect(Collectors.toMap(
	                            Category::getYear,
	                            Category::getStipend));

	    log.info(
	            "Loaded {} stipend slabs : {}",
	            stipendMap.size(),
	            stipendMap);

	    Integer amount =
	            calculateAdjustmentAmount(
	                    doj,
	                    monthStart,
	                    days,
	                    stipendMap);

	    log.info(
	            "Final calculated amount = {}",
	            amount);

	    return amount;
	}
	
	private Integer calculateAdjustmentAmount(
	        LocalDate doj,
	        LocalDate monthStart,
	        Integer adjustmentDays,
	        Map<Integer, Integer> stipendMap) {

	    log.info("Starting adjustment calculation");

	    int totalDaysInMonth = monthStart.lengthOfMonth();

	    /*
	     * Adjustment always starts from the first day
	     * of FORYM.
	     */
	    LocalDate currentDate = monthStart;

	    int remainingDays = adjustmentDays;

	    double totalAmount = 0.0;

	    while (remainingDays > 0) {

	        /*
	         * Determine Training Year
	         */
	        int trainingYear = 1;

	        LocalDate anniversary = doj;

	        while (!currentDate.isBefore(anniversary.plusYears(1))) {
	            trainingYear++;
	            anniversary = anniversary.plusYears(1);
	        }

	        if (trainingYear <= 0) {
	            trainingYear = 1;
	        }

	        /*
	         * If stipend is not available
	         * use last available year.
	         */
	        if (!stipendMap.containsKey(trainingYear)) {

	            trainingYear =
	                    stipendMap.keySet()
	                            .stream()
	                            .max(Integer::compareTo)
	                            .orElse(1);
	        }

	        Integer stipend = stipendMap.get(trainingYear);

	        /*
	         * Anniversary of NEXT year
	         */
	        LocalDate nextAnniversary = anniversary.plusYears(1);

	        /*
	         * Days remaining before anniversary
	         */
	        int daysTillAnniversary =
	                (int) ChronoUnit.DAYS.between(
	                        currentDate,
	                        nextAnniversary);

	        /*
	         * If anniversary not in current month
	         */
	        if (daysTillAnniversary <= 0 ||
	                nextAnniversary.getMonthValue() != currentDate.getMonthValue()) {

	            daysTillAnniversary = remainingDays;
	        }

	        int applicableDays =
	                Math.min(remainingDays, daysTillAnniversary);

	        double dailyRate =
	                ((double) stipend) / totalDaysInMonth;

	        double amount =
	                dailyRate * applicableDays;

	        // ❗ FIXED: you were resetting totalAmount every loop
	        totalAmount = totalAmount + amount;

	        log.info(
	                "TrainingYear={}, Stipend={}, Days={}, DailyRate={}, Amount={}",
	                trainingYear,
	                stipend,
	                applicableDays,
	                dailyRate,
	                amount);

	        remainingDays -= applicableDays;

	        currentDate = currentDate.plusDays(applicableDays);
	    }

	    Integer finalAmount =
	            (int) Math.round(totalAmount);

	    log.info("Final Adjustment Amount={}", finalAmount);

	    return finalAmount;
	}

}
