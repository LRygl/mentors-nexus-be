package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.Order.CheckoutRequestDTO;
import com.mentors.applicationstarter.DTO.Order.CheckoutResponseDTO;
import com.mentors.applicationstarter.DTO.Order.OrderDTO;
import com.mentors.applicationstarter.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    /**
     * Process the full checkout flow:
     * 1. Validate cart items
     * 2. Create Order + OrderItems
     * 3. Create CourseEnrollments
     * 4. Generate Invoice
     * 5. Return checkout summary
     *
     * @param request  checkout data from the frontend
     * @param user     the authenticated user (null if registering during checkout)
     * @return checkout response with order and invoice details
     */
    CheckoutResponseDTO checkout(CheckoutRequestDTO request, User user);

    OrderDTO getOrderById(Long orderId);

    List<OrderDTO> getMyOrders(Long userId);

    Page<OrderDTO> getMyOrdersPaged(Long userId, Pageable pageable);
}
