package com.bsp.dnb.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsp.dnb.entity.RoleCategory;
import com.bsp.dnb.repo.RoleCategoryRepository;
import com.bsp.dnb.service.RoleCategoryService;

@Service
public class RoleCategoryServiceImpl
        implements RoleCategoryService {

    @Autowired
    private RoleCategoryRepository repository;

    @Override
    public void assignCategory(
            Long roleId,
            Integer catg) {

        if (repository.existsByRoleIdAndCatg(
                roleId,
                catg)) {

            return;
        }

        RoleCategory entity =
                new RoleCategory();

        entity.setRoleId(roleId);
        entity.setCatg(catg);

        repository.save(entity);
    }
}