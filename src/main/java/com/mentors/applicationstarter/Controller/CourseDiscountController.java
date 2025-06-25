package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Model.CourseDiscount;
import com.mentors.applicationstarter.Service.CourseDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class CourseDiscountController {

    private final CourseDiscountService courseDiscountService;

    @GetMapping("/all")
    public ResponseEntity<List<CourseDiscount>> getAllDiscounts() {
        return new ResponseEntity<>(courseDiscountService.getAllDiscounts(), HttpStatus.OK);
    }


}
