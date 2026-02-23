package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    List<Invoice> findByUserIdOrderByIssuedDateDesc(Long userId);

    Page<Invoice> findByUserId(Long userId, Pageable pageable);
}
