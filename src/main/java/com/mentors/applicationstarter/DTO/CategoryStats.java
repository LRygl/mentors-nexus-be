package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Model.FAQCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStats {

    private Long totalActiveCategories;
    private List<FAQCategory> mostPopularCategories;

}
