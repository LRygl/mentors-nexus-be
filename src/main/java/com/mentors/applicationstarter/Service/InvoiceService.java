package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public interface InvoiceService {
    List<Invoice> getAllInvoices();
    Invoice getInvoiceById(Long id);
    Page<Invoice> getPagedInvoices(String invoiceNumber, Float minTotalAmount, Float maxTotalAmount, LocalDate issuedFrom, LocalDate issuedTo, Boolean paid, Pageable pageable);
    Invoice createInvoice(Invoice invoice);
    Invoice deleteInvoice(Long id);
    Invoice changeInvoicePaidStatus(Long id);
}
