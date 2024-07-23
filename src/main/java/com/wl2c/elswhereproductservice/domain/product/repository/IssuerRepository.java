package com.wl2c.elswhereproductservice.domain.product.repository;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Issuer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssuerRepository extends JpaRepository<Issuer, Long> {
    @Query("select i from Issuer i " +
            "where i.issuerState = 'ACTIVE' ")
    List<Issuer> findAllByIssuerState();
}
