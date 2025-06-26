package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CourseDiscountDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.CourseDiscountMapper;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseDiscount;
import com.mentors.applicationstarter.Repository.CourseDiscountRepository;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Service.CourseDiscountService;
import com.mentors.applicationstarter.Validator.DiscountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseDiscountServiceImpl implements CourseDiscountService {

    private final CourseDiscountRepository courseDiscountRepository;
    private final CourseRepository courseRepository;
    private final DiscountValidator discountValidator;

    @Override
    public List<CourseDiscount> getAllDiscounts() {
        return courseDiscountRepository.findAll();
    }

    //TODO needs to be able to provide the list of courses
    //TODO cannot create concurrent discounts for one course
    @Override
    public CourseDiscountDTO createCourseDiscount(CourseDiscount request) {
        Instant now = Instant.now();
        Course course = courseRepository.findById(request.getCourse().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST)
        );

        discountValidator.validateCreate(request);

        CourseDiscount courseDiscount = CourseDiscount.builder()
                .course(course) // ← required!
                .active(request.getActive())
                .discountAmount(request.getDiscountAmount())
                .discountPercentage(request.getDiscountPercentage())
                .createdBy(request.getCreatedBy())
                .description(request.getDescription())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .build();

        courseDiscountRepository.save(courseDiscount);
        return CourseDiscountMapper.toResponseDTO(courseDiscount);
    }

    @Override
    public CourseDiscount updateDiscount(Long discountId, CourseDiscount request) {
        CourseDiscount discount = findCourseDiscountById(discountId);
        discountValidator.validateUpdate(discount,request);

        discount.setDescription(request.getDescription());

        if (request.getValidFrom() != null) {
            discount.setValidFrom(request.getValidFrom());
        }
        if (request.getValidTo() != null) {
            discount.setValidTo(request.getValidTo());
        }

        return courseDiscountRepository.save(discount);
    }

    @Override
    public CourseDiscount deleteDiscount(Long discountId) {
        CourseDiscount discount = findCourseDiscountById(discountId);
        courseDiscountRepository.delete(discount);
        return discount;
    }

    private CourseDiscount findCourseDiscountById(Long discountId) {
        return courseDiscountRepository.findById(discountId).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.DISCOUNT_COURSE_DOES_NOT_EXIST)
        );

    }

}
