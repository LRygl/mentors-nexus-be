package com.mentors.applicationstarter.Validator;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.BusinessRuleViolationException;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.CourseDiscount;
import com.mentors.applicationstarter.Repository.CourseDiscountRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class DiscountValidator {

    @Autowired
    CourseDiscountRepository courseDiscountRepository;

    public void validateCreate(CourseDiscount discount) {
        Instant now = Instant.now();

        validateValidFrom(discount.getValidFrom(), null, now);
        validateValidTo(discount.getValidFrom(), discount.getValidTo(), now);
        validateDiscountType(discount.getDiscountPercentage(), discount.getDiscountAmount());
        validateNoOtherActiveDiscount(discount.getCourse(), null, discount.isCurrentlyActive());
        validateValidTo(discount.getValidFrom(), discount.getValidTo(),now);
    }

    public void validateUpdate(CourseDiscount current, CourseDiscount update) {
        Instant now = Instant.now();

        validateValidFrom(update.getValidFrom(), current.getValidTo(), now);
        validateValidTo(current.getValidFrom(), update.getValidTo(), now);
        validateDiscountType(update.getDiscountPercentage(), update.getDiscountAmount());

        if (current.hasStarted()) {
            validatePriceChangeNotAllowed(current, update);
        }

        validateNoOtherActiveDiscount(current.getCourse(), current.getId(), Boolean.TRUE.equals(update.getActive()));
    }

    ///
    /// PRIVATE METHODS
    ///

    private void validateValidFrom(Instant validFrom, Instant validTo, Instant now) {
        if (validFrom != null && validFrom.isBefore(now)) {
            throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_VALID_FROM_IN_PAST);
        }
        if (validFrom != null && validTo != null && validFrom.isAfter(validTo)) {
            throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_VALID_FROM_AFTER_VALID_TO);
        }
    }

    private void validateValidTo(Instant validFrom, Instant validTo, Instant now) {
        if (validTo != null) {
            if (validTo.isBefore(now)) {
                throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_VALID_TO_IN_PAST);
            }
            if (validFrom != null && validFrom.isAfter(validTo)) {
                throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_VALID_FROM_AFTER_VALID_TO);
            }
        }
    }

    private void validateDiscountType(BigDecimal percentage, BigDecimal amount) {
        if (percentage != null && amount != null) {
            throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_TYPE_AMBIGUOUS);
        }
    }

    private void validatePriceChangeNotAllowed(CourseDiscount current, CourseDiscount update) {
        if (update.getDiscountPercentage() != null && !update.getDiscountPercentage().equals(current.getDiscountPercentage())) {
            throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_ALREADY_STARTED);
        }

        if (update.getDiscountAmount() != null && !update.getDiscountAmount().equals(current.getDiscountAmount())) {
            throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_ALREADY_STARTED);
        }
    }

    private void validateNoOtherActiveDiscount(Course course, Long currentId, boolean isMarkedActive) {
        if (!isMarkedActive || course == null) return;

        List<CourseDiscount> discounts = courseDiscountRepository.findByCourse(course);

        boolean anotherActive = discounts.stream()
                .filter(d -> currentId == null || !d.getId().equals(currentId))
                .anyMatch(CourseDiscount::isCurrentlyActive);

        if (anotherActive) {
            throw new BusinessRuleViolationException(ErrorCodes.DISCOUNT_CONCURRENT_DISCOUNT_NOT_ALOWED);
        }
    }
}
