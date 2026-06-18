package com.bsp.dnb.service;


import java.util.List;

import com.bsp.dnb.dto.CategoryDto;

public interface CategoryService {

    CategoryDto saveCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    CategoryDto getCategoryById(Integer catg);

    List<CategoryDto> getAllCategories();
    
    List<CategoryDto> getCategoriesByRole(Long roleId);
    
    List<CategoryDto> getLoggedInUserCategories();
    
    public Integer findStipend(
            Integer catg,
            Integer year);
    
    
}
