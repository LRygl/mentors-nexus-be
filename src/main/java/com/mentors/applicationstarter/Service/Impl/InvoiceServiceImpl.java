package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.Order.InvoiceDetailDTO;
import com.mentors.applicationstarter.DTO.Order.InvoiceListDTO;
import com.mentors.applicationstarter.DTO.Order.OrderItemDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Invoice;
import com.mentors.applicationstarter.Model.Order;
import com.mentors.applicationstarter.Model.OrderItem;
import com.mentors.applicationstarter.Repository.InvoiceRepository;
import com.mentors.applicationstarter.Service.InvoiceService;
import com.mentors.applicationstarter.Specification.InvoiceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private static final String PREFIX = "MN";
    private static final Integer DUE_DATE_OFFSET = 14;

    // ============================================
    // EXISTING METHODS (admin CRUD)
    // ============================================

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Page<Invoice> getPagedInvoices(String invoiceNumber, BigDecimal minTotalAmount, BigDecimal maxTotalAmount, LocalDate issuedFrom, LocalDate issuedTo, Boolean paid, Pageable pageable) {
        Instant fromInstant = null;
        Instant toInstant = null;

        if (issuedFrom != null) {
            fromInstant = issuedFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
        }
        if (issuedTo != null) {
            toInstant = issuedTo.atStartOfDay(ZoneOffset.UTC).toInstant();
        }

        Specification<Invoice> spec = InvoiceSpecification.withFilters(invoiceNumber, minTotalAmount, maxTotalAmount, fromInstant, toInstant, paid);
        return invoiceRepository.findAll(spec, pageable);
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
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.INVOICE_DOES_NOT_EXIST));
    }

    // ============================================
    // NEW: Purchase flow methods
    // ============================================

    @Override
    public Invoice createInvoiceForOrder(Order order) {
        log.info("[Invoice] Creating invoice for order: {}", order.getId());

        Invoice invoice = Invoice.builder()
                .UUID(UUID.randomUUID())
                .user(order.getUser())
                .order(order)
                .totalAmount(order.getTotalAmount())
                .currency("CZK")
                .issuedDate(Instant.now())
                .dueDate(Instant.now().plus(DUE_DATE_OFFSET, ChronoUnit.DAYS))
                .paid(false)
                // Snapshot billing details from order
                .billingFirstName(order.getBillingFirstName())
                .billingLastName(order.getBillingLastName())
                .billingEmail(order.getBillingEmail())
                .billingStreet(order.getBillingStreet())
                .billingCity(order.getBillingCity())
                .billingPostalCode(order.getBillingPostalCode())
                .billingCountry(order.getBillingCountry())
                .build();

        invoiceRepository.save(invoice);

        // Generate invoice number after save (needs ID)
        invoice.setInvoiceNumber(generateInvoiceNumber(invoice.getId()));
        invoiceRepository.save(invoice);

        log.info("[Invoice] Invoice created: {}", invoice.getInvoiceNumber());
        return invoice;
    }

    @Override
    public List<InvoiceListDTO> getMyInvoices(Long userId) {
        return invoiceRepository.findByUserIdOrderByIssuedDateDesc(userId).stream()
                .map(this::toListDTO)
                .toList();
    }

    @Override
    public Page<InvoiceListDTO> getMyInvoicesPaged(Long userId, Pageable pageable) {
        return invoiceRepository.findByUserId(userId, pageable)
                .map(this::toListDTO);
    }

    @Override
    public InvoiceDetailDTO getInvoiceDetail(Long invoiceId, Long userId) {
        Invoice invoice = findInvoiceById(invoiceId);

        // Security check: ensure the invoice belongs to the requesting user
        if (invoice.getUser() == null || !invoice.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(ErrorCodes.INVOICE_DOES_NOT_EXIST);
        }

        return toDetailDTO(invoice);
    }

    @Override
    public byte[] generateInvoicePdf(Long invoiceId, Long userId) {
        Invoice invoice = findInvoiceById(invoiceId);

        // Security check
        if (invoice.getUser() == null || !invoice.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(ErrorCodes.INVOICE_DOES_NOT_EXIST);
        }

        // TODO: Implement PDF generation with OpenHTML2PDF
        // For now, return a placeholder. PDF generation will be implemented in InvoicePdfService.
        log.warn("[Invoice] PDF generation not yet implemented for invoice: {}", invoiceId);
        return new byte[0];
    }

    // ============================================
    // PRIVATE HELPERS
    // ============================================

    private String generateInvoiceNumber(Long invoiceId) {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        return PREFIX + "-" + datePart + "-" + invoiceId;
    }

    private Invoice findInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.INVOICE_DOES_NOT_EXIST));
    }

    private InvoiceListDTO toListDTO(Invoice invoice) {
        int itemCount = 0;
        if (invoice.getOrder() != null && invoice.getOrder().getItems() != null) {
            itemCount = invoice.getOrder().getItems().size();
        }

        return InvoiceListDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .totalAmount(invoice.getTotalAmount())
                .currency(invoice.getCurrency())
                .paid(invoice.getPaid())
                .issuedDate(invoice.getIssuedDate())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .itemCount(itemCount)
                .build();
    }

    private InvoiceDetailDTO toDetailDTO(Invoice invoice) {
        List<OrderItemDTO> items = List.of();
        if (invoice.getOrder() != null && invoice.getOrder().getItems() != null) {
            items = invoice.getOrder().getItems().stream()
                    .map(this::toOrderItemDTO)
                    .toList();
        }

        return InvoiceDetailDTO.builder()
                .id(invoice.getId())
                .uuid(invoice.getUUID() != null ? invoice.getUUID().toString() : null)
                .invoiceNumber(invoice.getInvoiceNumber())
                .billingFirstName(invoice.getBillingFirstName())
                .billingLastName(invoice.getBillingLastName())
                .billingEmail(invoice.getBillingEmail())
                .billingStreet(invoice.getBillingStreet())
                .billingCity(invoice.getBillingCity())
                .billingPostalCode(invoice.getBillingPostalCode())
                .billingCountry(invoice.getBillingCountry())
                .totalAmount(invoice.getTotalAmount())
                .currency(invoice.getCurrency())
                .paid(invoice.getPaid())
                .issuedDate(invoice.getIssuedDate())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .items(items)
                .invoicePdfLink(invoice.getInvoicePdfLink())
                .invoicePdfName(invoice.getInvoicePdfName())
                .build();
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .courseId(item.getCourse().getId())
                .courseName(item.getCourseName())
                .originalPrice(item.getOriginalPrice())
                .pricePaid(item.getPricePaid())
                .build();
    }
}
