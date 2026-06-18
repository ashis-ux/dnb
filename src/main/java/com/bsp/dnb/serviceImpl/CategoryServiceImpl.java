package com.bsp.dnb.serviceImpl;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.CategoryDto;
import com.bsp.dnb.entity.Category;
import com.bsp.dnb.entity.DnbRole;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbRoleRepository;
import com.bsp.dnb.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	private static final Logger log =
	        LoggerFactory.getLogger(DnbAttServiceImpl.class);
	
	@Autowired
    private CategoryRepository categoryRepository;
	
	@Autowired
	private DnbRoleRepository dnbRoleRepository;
	
	@Value("${app.logged-in-role}")
	private Long loggedInRole;

	@Override
	public CategoryDto saveCategory(CategoryDto categoryDto) {
	    log.info("Saving category with CATG : {}",
	            categoryDto.getCatg());
	    validateRequest(categoryDto);
	    Category category = dtoToEntity(categoryDto);
	    Category savedCategory =
	            categoryRepository.save(category);
	    log.info("Category saved successfully");
	    return entityToDto(savedCategory);
	}

	@Override
	public CategoryDto updateCategory(CategoryDto categoryDto) {
	    log.info("Updating category with CATG : {}",
	            categoryDto.getCatg());
	    if (categoryDto.getCatg() == null) {
	        throw new BadRequestException(
	                "Category is mandatory");
	    }

	    categoryRepository.findById(categoryDto.getCatg())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Category not found with CATG : "
	                                    + categoryDto.getCatg()));
	    validateRequest(categoryDto);
	    Category updatedCategory =
	            categoryRepository.save(
	                    dtoToEntity(categoryDto));
	    log.info("Category updated successfully");
	    return entityToDto(updatedCategory);
	}

	@Override
	public CategoryDto getCategoryById(Integer catg) {
	    log.info("Fetching category with CATG : {}",
	            catg);
	    Category category =
	            categoryRepository.findById(catg)
	                    .orElseThrow(() ->
	                            new ResourceNotFoundException(
	                                    "Category not found with CATG : "
	                                            + catg));
	    return entityToDto(category);
	}
	
	@Override
	public List<CategoryDto> getAllCategories() {
	    log.info("Fetching all categories");
	    return categoryRepository.findAll()
	            .stream()
	            .map(this::entityToDto)
	            .collect(Collectors.toList());
	}

	 
	
	@Override
	public List<CategoryDto> getCategoriesByRole(Long roleId) {

	    log.info("Fetching categories for role ID : {}",
	            roleId);

	    if (!dnbRoleRepository.existsById(roleId)) {

	        throw new ResourceNotFoundException(
	                "Role not found with ID : "
	                        + roleId);
	    }

	    List<Category> categories =
	            categoryRepository.findByDnbRole_Id(roleId);

	    return categories.stream()
	            .map(this::entityToDto)
	            .collect(Collectors.toList());
	}
    
    private void validateRequest(CategoryDto dto) {
        if (dto.getDescription() == null
                || dto.getDescription().trim().isEmpty()) {
            throw new BadRequestException(
                    "Description is mandatory");
        }
        if (dto.getRoleId() == null) {
            throw new BadRequestException(
                    "Role is mandatory");
        }
        if (dto.getYear() == null
                || dto.getYear() < 1
                || dto.getYear() > 9) {
            throw new BadRequestException(
                    "Year should be between 1 and 9");
        }
        if (!dnbRoleRepository.existsById(
                dto.getRoleId())) {
            throw new ResourceNotFoundException(
                    "Role not found with ID : "
                            + dto.getRoleId());
        }
    }

    private CategoryDto entityToDto(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setCatg(entity.getCatg());
        dto.setDescription(
                entity.getDescription());
        dto.setStipend(entity.getStipend());
        dto.setYear(entity.getYear());
        dto.setType(entity.getType());
        if (entity.getDnbRole() != null) {
            dto.setRoleId(
                    entity.getDnbRole().getId());
            dto.setRoleName(
                    entity.getDnbRole().getName());
        }
        return dto;
    }

    private Category dtoToEntity(CategoryDto dto) {
        Category category = new Category();
        category.setCatg(dto.getCatg());
        category.setDescription(
                dto.getDescription()
                        .trim()
                        .toUpperCase());
        category.setStipend(dto.getStipend());
        category.setYear(dto.getYear());
        category.setType(
                dto.getType() == null
                        ? null
                        : dto.getType()
                                .trim()
                                .toUpperCase());
        DnbRole role =
                dnbRoleRepository.findById(
                        dto.getRoleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found with ID : "
                                                + dto.getRoleId()));
        category.setDnbRole(role);
        return category;
    }
    
    @Override
    public List<CategoryDto> getLoggedInUserCategories() {

        log.info("Fetching categories for role : {}",
                loggedInRole);

        return categoryRepository
                .findCategoriesForDropdown(loggedInRole)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Integer findStipend(
            Integer catg,
            Integer year) {

        Integer stipend =
                categoryRepository.findStipend(
                        catg,
                        year);

        if (stipend == null) {

            throw new ResourceNotFoundException(
                    "Stipend not found");
        }

        return stipend;
    }
}


