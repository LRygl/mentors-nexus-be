package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CourseDiscountDTO;
import com.mentors.applicationstarter.Model.CourseDiscount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseDiscountService {
    List<CourseDiscountDTO> getAllDiscounts();

    CourseDiscountDTO createCourseDiscount(CourseDiscount request);

    CourseDiscountDTO updateDiscount(Long discountId, CourseDiscount request);

    CourseDiscountDTO deleteDiscount(Long discountId);
}
