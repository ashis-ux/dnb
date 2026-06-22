package com.bsp.dnb.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.DnbRoleDto;
import com.bsp.dnb.entity.DnbRole;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.DuplicateResourceException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbRoleRepository;
import com.bsp.dnb.service.DnbRoleService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DnbRoleServiceImpl implements DnbRoleService {
	
	private static final Logger log =
	        LoggerFactory.getLogger(DnbRoleService.class);

    @Autowired
    private DnbRoleRepository repository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public DnbRoleDto save(DnbRoleDto dto) {
        log.info("Saving DNB Role");
        validateRequest(dto);
        Long nextId =
                repository.findAll()
                        .stream()
                        .mapToLong(DnbRole::getId)
                        .max()
                        .orElse(0L) + 1;

        dto.setId(nextId);
        if (repository.existsByNameIgnoreCase(dto.getName())) {

            throw new DuplicateResourceException(
                    "Role already exists : " + dto.getName());
        }
        DnbRole savedEntity =
                repository.save(dtoToEntity(dto));
        log.info("Role saved successfully with ID : {}",
                savedEntity.getId());
        return entityToDto(savedEntity);
    }

    @Override
    public DnbRoleDto update(DnbRoleDto dto) {
        log.info("Updating Role ID : {}", dto.getId());
        if (dto.getId() == null) {
            throw new BadRequestException(
                    "ID is mandatory");
        }
        repository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found"));
        repository.findByNameIgnoreCase(dto.getName())
                .ifPresent(role -> {
                    if (!role.getId().equals(dto.getId())) {
                        throw new DuplicateResourceException(
                                "Role already exists : "
                                        + dto.getName());
                    }
                });
        DnbRole updated =
                repository.save(dtoToEntity(dto));
        log.info("Role updated successfully");
        return entityToDto(updated);
    }

    @Override
    public DnbRoleDto findById(Long id) {
        log.info("Fetching Role ID : {}", id);
        DnbRole entity =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found"));
        return entityToDto(entity);
    }

    @Override
    public List<DnbRoleDto> findAll() {
        log.info("Fetching all roles");
        return repository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    

    private void validateRequest(DnbRoleDto dto) {
        if (dto.getName() == null ||
                dto.getName().trim().isEmpty()) {
            throw new BadRequestException(
                    "Role name is mandatory");
        }
        dto.setName(dto.getName().trim().toUpperCase());
        if (dto.getName().length() > 6) {
            throw new BadRequestException(
                    "Role name cannot exceed 6 characters");
        }

        if (dto.getId() == null &&
                repository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException(
                    "Role already exists with name : "
                            + dto.getName());
        }
        if (dto.getId() != null &&
                repository.existsByNameIgnoreCaseAndIdNot(
                        dto.getName(),
                        dto.getId())) {
            throw new DuplicateResourceException(
                    "Role already exists with name : "
                            + dto.getName());
        }
    }

    private DnbRole dtoToEntity(DnbRoleDto dto) {
        DnbRole entity = new DnbRole();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    private DnbRoleDto entityToDto(DnbRole entity) {
        DnbRoleDto dto = new DnbRoleDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
 
    public String getRoleName() {
        log.info(
                "Fetching logged-in user role name");

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {

            log.error(
                    "Authentication object is null");

            throw new ResourceNotFoundException(
                    "User authentication not found");
        }

        String roleName =
                authentication.getAuthorities()
                        .stream()
                        .map(authority ->
                                authority.getAuthority())
//                        .filter(role ->
//                                role.startsWith("ROLE_DNB"))
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error(
                                    "No DNB role assigned to logged-in user");
                            return new ResourceNotFoundException(
                                    "No DNB role assigned to logged-in user");
                        });

        log.info(
                "JWT Role : {}",
                roleName);

        if (roleName.startsWith("ROLE_")) {

            roleName =
                    roleName.substring(5);
        }

        log.info(
                "Resolved role name : {}",
                roleName);

        return roleName;
    }

        @Override
        public Long getRoleId() {

            log.info(
                    "Fetching role id for logged-in user");

            String roleName =
                    getRoleName();

            DnbRole role =
                    repository
                            .findByName(roleName)
                            .orElseThrow(() -> {

                                log.error(
                                        "Role not found in APP_ROLE table : {}",
                                        roleName);

                                return new ResourceNotFoundException(
                                        "Role not found : "
                                                + roleName);
                            });

            log.info(
                    "Resolved role id : {} for role : {}",
                    role.getId(),
                    roleName);

            return role.getId();
        }
    
}