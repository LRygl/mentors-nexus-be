package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.Order.InvoiceDetailDTO;
import com.mentors.applicationstarter.DTO.Order.InvoiceListDTO;
import com.mentors.applicationstarter.Model.Invoice;
import com.mentors.applicationstarter.Model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public interface InvoiceService {
    List<Invoice> getAllInvoices();
    Invoice getInvoiceById(Long id);
    Page<Invoice> getPagedInvoices(String invoiceNumber, BigDecimal minTotalAmount, BigDecimal maxTotalAmount, LocalDate issuedFrom, LocalDate issuedTo, Boolean paid, Pageable pageable);
    Invoice createInvoice(Invoice invoice);
    Invoice deleteInvoice(Long id);
    Invoice changeInvoicePaidStatus(Long id);

    // New methods for purchase flow
    Invoice createInvoiceForOrder(Order order);
    List<InvoiceListDTO> getMyInvoices(Long userId);
    Page<InvoiceListDTO> getMyInvoicesPaged(Long userId, Pageable pageable);
    InvoiceDetailDTO getInvoiceDetail(Long invoiceId, Long userId);
    byte[] generateInvoicePdf(Long invoiceId, Long userId);
}
