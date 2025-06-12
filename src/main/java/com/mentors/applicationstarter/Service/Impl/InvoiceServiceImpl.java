package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Invoice;
import com.mentors.applicationstarter.Repository.InvoiceRepository;
import com.mentors.applicationstarter.Service.InvoiceService;
import com.mentors.applicationstarter.Specification.InvoiceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private static final String PREFIX = "MN";
    private static final Integer DUE_DATE_OFFSET = 14;

    @Override
    public List<Invoice> getAllInvoices() {
        //Placeholder function to generate some rando invoices
        generateInvoice();
        return invoiceRepository.findAll();
    }

    @Override
    public Page<Invoice> getPagedInvoices(String invoiceNumber, Float minTotalAmount, Float maxTotalAmount, LocalDate issuedFrom, LocalDate issuedTo, Boolean paid, Pageable pageable) {

        Instant fromInstant = null;
        Instant toInstant = null;

        if(issuedFrom != null) {
            fromInstant = issuedFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
        }
        if(issuedTo != null) {
            toInstant = issuedTo.atStartOfDay(ZoneOffset.UTC).toInstant();

        }

        Specification<Invoice> spec = InvoiceSpecification.withFilters(invoiceNumber, minTotalAmount, maxTotalAmount, fromInstant, toInstant, paid);
        Page<Invoice> invoicePage = invoiceRepository.findAll(spec,pageable);

        return invoicePage;
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {

        Invoice newInvoice = Invoice.builder()
                .UUID(UUID.randomUUID())
                .issuedDate(Instant.now())
                .dueDate(Instant.now().plus(DUE_DATE_OFFSET, ChronoUnit.DAYS))
                .paid(false)
                .build();

        invoiceRepository.save(newInvoice);
        newInvoice.setInvoiceNumber(generateInvoiceNumber(newInvoice.getId()));
        invoiceRepository.save(newInvoice);
        return newInvoice;
    }

    @Override
    public Invoice deleteInvoice(Long id) {
        Invoice invoice = findInvoiceById(id);
        invoiceRepository.delete(invoice);
        return invoice;
    }

    @Override
    public Invoice changeInvoicePaidStatus(Long id) {
        Invoice invoice = findInvoiceById(id);
        invoice.setPaid(!invoice.getPaid());
        invoice.setPaidDate(Instant.now());
        invoiceRepository.save(invoice);
        return invoice;
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(ErrorCodes.INVOICE_DOES_NOT_EXIST));
    }

    public Invoice generateInvoice() {

        Invoice invoice = Invoice.builder()
                .UUID(UUID.randomUUID())
                .issuedDate(Instant.now())
                .paid(false)
                .dueDate(Instant.now().plus(DUE_DATE_OFFSET, ChronoUnit.DAYS))
                .build();

        invoiceRepository.save(invoice);

        invoice.setInvoiceNumber(generateInvoiceNumber(invoice.getId()));
        invoiceRepository.save(invoice);
        return invoice;
    }


    private String generateInvoiceNumber(Long invoiceId){
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE); // e.g. 20250611
        return PREFIX + "-" + datePart + "-" + invoiceId;
    }

    private Invoice findInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.INVOICE_DOES_NOT_EXIST));
        return invoice;
    }
}
