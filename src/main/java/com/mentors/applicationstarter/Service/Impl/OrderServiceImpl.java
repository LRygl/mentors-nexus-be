package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.Course.EnrolledCourseDTO;
import com.mentors.applicationstarter.DTO.Order.*;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.OrderStatus;
import com.mentors.applicationstarter.Exception.BusinessRuleViolationException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.*;
import com.mentors.applicationstarter.Repository.CourseEnrollmentRepository;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Repository.OrderRepository;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.CourseEnrollmentService;
import com.mentors.applicationstarter.Service.InvoiceService;
import com.mentors.applicationstarter.Service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;
    private final InvoiceService invoiceService;
    private final CourseEnrollmentService courseEnrollmentService;

    @Override
    @Transactional
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request, User user) {
        log.info("[Order] Starting checkout for user: {}", user.getEmail());

        // 1. Validate request
        validateCheckoutRequest(request, user);

        // 2. Fetch and validate courses
        List<Course> courses = fetchAndValidateCourses(request.getCourseIds(), user);

        // 3. Update user address if provided
        updateUserAddress(user, request);

        // 4. Calculate total
        BigDecimal totalAmount = courses.stream()
                .map(Course::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Create Order
        Order order = Order.builder()
                .uuid(UUID.randomUUID())
                .user(user)
                .status(OrderStatus.COMPLETED) // No payment gateway yet, mark as completed
                .totalAmount(totalAmount)
                .selectedPaymentMethod(request.getSelectedPaymentMethod())
                .billingFirstName(request.getFirstName())
                .billingLastName(request.getLastName())
                .billingEmail(request.getEmail())
                .billingPhone(request.getTelephoneNumber())
                .billingStreet(request.getStreet())
                .billingCity(request.getCity())
                .billingPostalCode(request.getPostalCode())
                .billingCountry(request.getCountry())
                .termsAccepted(request.getTermsAccepted())
                .privacyAccepted(request.getPrivacyAccepted())
                .consentTimestamp(Instant.now())
                .createdAt(Instant.now())
                .completedAt(Instant.now())
                .deleted(false)
                .archived(false)
                .build();

        // 6. Create OrderItems
        Set<OrderItem> orderItems = courses.stream()
                .map(course -> OrderItem.builder()
                        .order(order)
                        .course(course)
                        .courseName(course.getName())
                        .originalPrice(course.getPrice())
                        .pricePaid(course.getPrice()) // No discounts applied yet
                        .build())
                .collect(Collectors.toSet());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        log.info("[Order] Order created with ID: {}", savedOrder.getId());

        // 7. Create CourseEnrollments for each purchased course
        List<EnrolledCourseDTO> enrolledCourses = new ArrayList<>();
        for (Course course : courses) {
            EnrolledCourseDTO enrolled = courseEnrollmentService.enroll(user, course.getId());

            // Link enrollment to this order
            courseEnrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId())
                    .ifPresent(enrollment -> {
                        enrollment.setOrderId(savedOrder.getId());
                        courseEnrollmentRepository.save(enrollment);
                    });

            enrolledCourses.add(enrolled);
        }

        // 8. Generate Invoice
        Invoice invoice = invoiceService.createInvoiceForOrder(savedOrder);
        savedOrder.setInvoice(invoice);
        orderRepository.save(savedOrder);

        log.info("[Order] Checkout complete. Order: {}, Invoice: {}", savedOrder.getId(), invoice.getInvoiceNumber());

        // 9. Build response
        return CheckoutResponseDTO.builder()
                .orderId(savedOrder.getId())
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .totalAmount(totalAmount)
                .status(savedOrder.getStatus().name())
                .enrolledCourses(enrolledCourses)
                .build();
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.ORDER_DOES_NOT_EXIST));
        return toDTO(order);
    }

    @Override
    public List<OrderDTO> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Page<OrderDTO> getMyOrdersPaged(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::toDTO);
    }

    // ============================================
    // PRIVATE HELPERS
    // ============================================

    private void validateCheckoutRequest(CheckoutRequestDTO request, User user) {
        if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {
            throw new BusinessRuleViolationException(ErrorCodes.ORDER_EMPTY_CART);
        }

        if (!Boolean.TRUE.equals(request.getTermsAccepted())) {
            throw new BusinessRuleViolationException(ErrorCodes.ORDER_TERMS_NOT_ACCEPTED);
        }
    }

    private List<Course> fetchAndValidateCourses(List<Long> courseIds, User user) {
        List<Course> courses = courseRepository.findAllById(courseIds);

        if (courses.size() != courseIds.size()) {
            throw new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST);
        }

        // Check user is not already enrolled in any of these courses
        for (Course course : courses) {
            if (courseEnrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
                throw new BusinessRuleViolationException(ErrorCodes.ORDER_COURSE_ALREADY_ENROLLED);
            }
        }

        return courses;
    }

    private void updateUserAddress(User user, CheckoutRequestDTO request) {
        boolean updated = false;

        if (request.getStreet() != null && !request.getStreet().isBlank()) {
            user.setStreet(request.getStreet());
            updated = true;
        }
        if (request.getCity() != null && !request.getCity().isBlank()) {
            user.setCity(request.getCity());
            updated = true;
        }
        if (request.getPostalCode() != null && !request.getPostalCode().isBlank()) {
            user.setPostalCode(request.getPostalCode());
            updated = true;
        }
        if (request.getCountry() != null && !request.getCountry().isBlank()) {
            user.setCountry(request.getCountry());
            updated = true;
        }

        // Update name/phone if provided
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
            updated = true;
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
            updated = true;
        }
        if (request.getTelephoneNumber() != null && !request.getTelephoneNumber().isBlank()) {
            user.setTelephoneNumber(request.getTelephoneNumber());
            updated = true;
        }

        if (updated) {
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems() != null
                ? order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .courseId(item.getCourse().getId())
                        .courseName(item.getCourseName())
                        .originalPrice(item.getOriginalPrice())
                        .pricePaid(item.getPricePaid())
                        .build())
                .toList()
                : List.of();

        return OrderDTO.builder()
                .id(order.getId())
                .uuid(order.getUuid() != null ? order.getUuid().toString() : null)
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .selectedPaymentMethod(order.getSelectedPaymentMethod() != null ? order.getSelectedPaymentMethod().name() : null)
                .createdAt(order.getCreatedAt())
                .completedAt(order.getCompletedAt())
                .invoiceId(order.getInvoice() != null ? order.getInvoice().getId() : null)
                .invoiceNumber(order.getInvoice() != null ? order.getInvoice().getInvoiceNumber() : null)
                .items(itemDTOs)
                .build();
    }
}
