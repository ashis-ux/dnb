package com.bsp.dnb.controller;

import java.util.List;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bsp.dnb.dto.CategoryDto;
import com.bsp.dnb.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categories")
@Slf4j
@PreAuthorize("hasAuthority('APP_DNB')")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategory(
            @RequestBody CategoryDto dto) {

        log.info("Received request to save category");

        CategoryDto response =
                categoryService.saveCategory(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {

        log.info("Received request to fetch all categories");

        List<CategoryDto> response =
                categoryService.getAllCategories();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<CategoryDto>>
    getCategoriesByRole(
            @PathVariable Long roleId) {

        log.info("Received request to fetch categories for role : {}",
                roleId);

        List<CategoryDto> response =
                categoryService.getCategoriesByRole(roleId);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/logged-in-user")
    public ResponseEntity<List<CategoryDto>> getLoggedInUserCategories() {

        log.info("Received request to fetch categories for logged-in user.");

        List<CategoryDto> response =
                categoryService.getLoggedInUserCategories();

        log.info("Returning {} categories for logged-in user.", response.size());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/logged-in-user-all-categories")
    public ResponseEntity<List<CategoryDto>> getLoggedInUserAllCategories() {

        log.info("Received request to fetch all categories for logged-in user.");

        List<CategoryDto> response =
                categoryService.getLoggedInUserAllCategories();

        log.info("Returning {} categories.", response.size());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stipend")
    public ResponseEntity<Integer>
    findStipend(

            @RequestParam Integer catg,

            @RequestParam Integer year) {

        log.info(
                "Fetching stipend for CATG : {} YEAR : {}",
                catg,
                year);

        return ResponseEntity.ok(

        		categoryService.findStipend(
                        catg,
                        year));
    }
    
    
}