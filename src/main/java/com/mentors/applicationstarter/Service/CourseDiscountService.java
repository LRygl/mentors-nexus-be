package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.CourseDiscount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseDiscountService {
    List<CourseDiscount> getAllDiscounts();
}
