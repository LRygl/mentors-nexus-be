package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Model.CourseDiscount;
import com.mentors.applicationstarter.Repository.CourseDiscountRepository;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Service.CourseDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseDiscountServiceImpl implements CourseDiscountService {

    private final CourseDiscountRepository courseDiscountRepository;

    @Override
    public List<CourseDiscount> getAllDiscounts() {
        return courseDiscountRepository.findAll();
    }
}
