package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CourseDiscountDTO;
import com.mentors.applicationstarter.Model.CourseDiscount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseDiscountService {
    List<CourseDiscount> getAllDiscounts();

    CourseDiscountDTO createCourseDiscount(CourseDiscount request);

    CourseDiscount updateDiscount(Long discountId, CourseDiscount request);

    CourseDiscount deleteDiscount(Long discountId);
}
