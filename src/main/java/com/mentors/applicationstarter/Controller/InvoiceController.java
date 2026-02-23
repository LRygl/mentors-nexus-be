package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.Order.InvoiceDetailDTO;
import com.mentors.applicationstarter.DTO.Order.InvoiceListDTO;
import com.mentors.applicationstarter.Model.Invoice;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ============================================
    // ADMIN ENDPOINTS (existing)
    // ============================================

    @GetMapping("/all")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return new ResponseEntity<>(invoiceService.getAllInvoices(), HttpStatus.OK);
    }

    @GetMapping
    public Page<Invoice> getPagedInvoices(
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false) BigDecimal minTotalAmount,
            @RequestParam(required = false) BigDecimal maxTotalAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate issuedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate issuedTo,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        return invoiceService.getPagedInvoices(invoiceNumber, minTotalAmount, maxTotalAmount, issuedFrom, issuedTo, paid, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return new ResponseEntity<>(invoiceService.getInvoiceById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Invoice> setInvoicePaid(@PathVariable Long id) {
        return new ResponseEntity<>(invoiceService.changeInvoicePaidStatus(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return new ResponseEntity<>(invoiceService.createInvoice(invoice), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Invoice> deleteInvoice(@PathVariable Long id) {
        return new ResponseEntity<>(invoiceService.deleteInvoice(id), HttpStatus.GONE);
    }

    // ============================================
    // USER-FACING ENDPOINTS (new)
    // ============================================

    /**
     * Get all invoices for the authenticated user.
     */
    @GetMapping("/my-invoices")
    public ResponseEntity<List<InvoiceListDTO>> getMyInvoices(
            @AuthenticationPrincipal User user
    ) {
        return new ResponseEntity<>(invoiceService.getMyInvoices(user.getId()), HttpStatus.OK);
    }

    /**
     * Get paginated invoices for the authenticated user.
     */
    @GetMapping("/my-invoices/page")
    public Page<InvoiceListDTO> getMyInvoicesPaged(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return invoiceService.getMyInvoicesPaged(
                user.getId(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "issuedDate"))
        );
    }

    /**
     * Get full invoice detail for the authenticated user.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<InvoiceDetailDTO> getInvoiceDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return new ResponseEntity<>(invoiceService.getInvoiceDetail(id, user.getId()), HttpStatus.OK);
    }

    /**
     * Download invoice as PDF.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        byte[] pdfBytes = invoiceService.generateInvoicePdf(id, user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + id + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
