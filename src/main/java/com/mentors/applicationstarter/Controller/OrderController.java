package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.Order.CheckoutRequestDTO;
import com.mentors.applicationstarter.DTO.Order.CheckoutResponseDTO;
import com.mentors.applicationstarter.DTO.Order.OrderDTO;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Process checkout - creates order, enrollments, and invoice.
     * Requires authenticated user (registration must happen before checkout).
     */
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(
            @RequestBody CheckoutRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        CheckoutResponseDTO response = orderService.checkout(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all orders for the authenticated user.
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getMyOrders(
            @AuthenticationPrincipal User user
    ) {
        return new ResponseEntity<>(orderService.getMyOrders(user.getId()), HttpStatus.OK);
    }

    /**
     * Get paginated orders for the authenticated user.
     */
    @GetMapping("/my-orders/page")
    public Page<OrderDTO> getMyOrdersPaged(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getMyOrdersPaged(user.getId(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    /**
     * Get a specific order by ID (must belong to authenticated user).
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        // TODO: Add ownership check
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }
}
