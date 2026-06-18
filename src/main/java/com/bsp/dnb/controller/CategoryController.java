package com.bsp.dnb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bsp.dnb.dto.CategoryDto;
import com.bsp.dnb.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {
	
	private static final Logger log =
	        LoggerFactory.getLogger(CategoryController.class);

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

    @PutMapping("/{catg}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Integer catg,
            @RequestBody CategoryDto dto) {

        log.info("Received request to update category : {}",
                catg);

        dto.setCatg(catg);

        CategoryDto response =
                categoryService.updateCategory(dto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{catg}")
    public ResponseEntity<CategoryDto> getCategoryById(
            @PathVariable Integer catg) {

        log.info("Received request to fetch category : {}",
                catg);

        CategoryDto response =
                categoryService.getCategoryById(catg);

        return ResponseEntity.ok(response);
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
    public ResponseEntity<List<CategoryDto>>
    getLoggedInUserCategories() {

        return ResponseEntity.ok(
                categoryService.getLoggedInUserCategories());
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