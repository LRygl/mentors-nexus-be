package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.CourseDiscountDTO;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseDiscount;
import com.mentors.applicationstarter.Service.CourseDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class CourseDiscountController {

    private final CourseDiscountService courseDiscountService;

    @GetMapping("/all")
    public ResponseEntity<List<CourseDiscountDTO>> getAllDiscounts() {
        return new ResponseEntity<>(courseDiscountService.getAllDiscounts(), HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<CourseDiscountDTO> createCourseDiscount(@RequestBody CourseDiscount request) {
        return new ResponseEntity<>(courseDiscountService.createCourseDiscount(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{discountId}")
    public ResponseEntity<CourseDiscountDTO> updateCourseDiscnout(@PathVariable Long discountId, @RequestBody CourseDiscount request) {
        return new ResponseEntity<>(courseDiscountService.updateDiscount(discountId, request), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{discountId}")
    public ResponseEntity<CourseDiscountDTO> deleteCourseDiscount(@PathVariable Long discountId) {
        return new ResponseEntity<>(courseDiscountService.deleteDiscount(discountId), HttpStatus.GONE);
    }

}
