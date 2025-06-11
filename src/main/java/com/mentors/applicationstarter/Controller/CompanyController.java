package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.CompanyRequestDTO;
import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import com.mentors.applicationstarter.Service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // Unpaged endpoint for admin/export use
    // GET /api/v1/company/all
    @GetMapping("/all")
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        return new ResponseEntity<>(companyService.getAllCourses(), HttpStatus.OK);
    }

    // Paged endpoint for frontend (tables, pagination UI)
    // GET /api/v1/company?page=0&size=10&sort=name,asc
    @GetMapping
    public Page<CompanyResponseDTO> getPagedCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page,size,Sort.by(direction,sort[0]));
        return companyService.getPagedCompanies(pageable);
    }


    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> getCompany(@PathVariable Long companyId) {
        return new ResponseEntity<>(companyService.getCompanyById(companyId), HttpStatus.OK);
    }

    @GetMapping("/{companyId}/ares")
    public ResponseEntity<CompanyResponseDTO> getCompanyAresData(@PathVariable Long companyId, @RequestParam String companyVAT) {
        return new ResponseEntity<>(companyService.getCompanyAresData(companyId,companyVAT), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@RequestBody CompanyRequestDTO request) {
        return new ResponseEntity<>(companyService.createCompany(request), HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(@RequestBody CompanyRequestDTO request, @PathVariable Long companyId) {
        return new ResponseEntity<>(companyService.updateCompany(request,companyId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> deleteCompany(@PathVariable Long companyId) {
        return new ResponseEntity<>(companyService.deleteCompany(companyId), HttpStatus.GONE);
    }
}
