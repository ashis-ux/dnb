package com.bsp.dnb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsp.dnb.entity.RoleCategory;

public interface RoleCategoryRepository
        extends JpaRepository<RoleCategory, Long> {

    List<RoleCategory> findByRoleId(
            Long roleId);

    boolean existsByRoleIdAndCatg(
            Long roleId,
            Integer catg);

        boolean existsByRoleId(Long roleId);


}