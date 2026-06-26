package com.bsp.dnb.service;


import java.util.List;

import com.bsp.dnb.dto.CategoryDto;

public interface CategoryService {

    CategoryDto saveCategory(CategoryDto categoryDto);

 

    List<CategoryDto> getAllCategories();
    
    List<CategoryDto> getCategoriesByRole(Long roleId);
    
    List<CategoryDto> getLoggedInUserCategories();
    
    public List<CategoryDto> getLoggedInUserAllCategories();
    
    public Integer findStipend(
            Integer catg,
            Integer year);
    
    
}
