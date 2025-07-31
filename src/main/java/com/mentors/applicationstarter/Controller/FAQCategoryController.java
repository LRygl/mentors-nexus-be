package com.mentors.applicationstarter.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public FAQ Category API Controller - accessible to all users
 * Provides read-only access to published FAQ categories
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/faq-category")
@RequiredArgsConstructor
@Tag(name = "FAQ Category Public API", description = "Public FAQ category operations for end users")
public class FAQCategoryController {
}
