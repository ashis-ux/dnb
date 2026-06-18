package com.bsp.dnb.repo;

import com.bsp.dnb.entity.DnbAtt;
import com.bsp.dnb.entity.DnbAttId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DnbAttRepository
        extends JpaRepository<DnbAtt, DnbAttId> {

    List<DnbAtt> findByIdId(Integer id);

    List<DnbAtt> findByIdYymm(Integer yymm);

    List<DnbAtt> findByIdIdAndIdYymm(
            Integer id,
            Integer yymm);
}