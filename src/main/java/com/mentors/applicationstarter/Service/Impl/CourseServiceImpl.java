package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.Enum.CourseStatus;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.CourseMapper;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.Label;
import com.mentors.applicationstarter.Repository.CategoryRepository;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Repository.LabelRepository;
import com.mentors.applicationstarter.Service.CourseService;
import com.mentors.applicationstarter.Specification.CourseSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final LabelRepository labelRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        Set<Label> labels = resolveLabels(request.getLabels());
        Set<Category> categories = resolveCategories(request.getCategories());

        Course course = Course.builder()
                .UUID(UUID.randomUUID())
                .name(request.getName())
                .created(Instant.now())
                .status(CourseStatus.UNPUBLISHED)
                .price(request.getPrice())
                .categories((categories))
                .labels(labels)
                .build();

        Course savedCourse = courseRepository.save(course);

        return mapObjectToDTO(savedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));
        return mapObjectToDTO(course);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapObjectToDTO)
                .collect(Collectors.toList());
    }




    @Override
    public CourseResponseDTO updateCourse(CourseRequestDTO dto) {
        Course course = courseRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        course.setName(dto.getName());
        course.setUpdated(Instant.now());
        course.setLabels(resolveLabels(dto.getLabels()));
        course.setCategories(resolveCategories(dto.getCategories()));

        Course updatedCourse = courseRepository.save(course);
        return mapObjectToDTO(updatedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        detachCourseFromLabels(course);
        courseRepository.delete(course);

        return mapObjectToDTO(course);
    }

    @Override
    public CourseResponseDTO updateCourseStatus(Long id, CourseStatusDTO courseStatusDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        CourseStatus newStatus = courseStatusDTO.getStatus();

        if(course.getStatus() == CourseStatus.PUBLISHED && newStatus == CourseStatus.PUBLISHED){
            return mapObjectToDTO(course);
        }

        //TODO Course has to have a category when published
        switch (newStatus) {
            case PUBLISHED, HIDDEN, PRIVATE -> {
                if(course.getPublished() == null) {
                    course.setPublished(Instant.now());
                }
            }
            case UNPUBLISHED -> course.setPublished(null);
        }

        course.setStatus(courseStatusDTO.getStatus());
        course.setUpdated(Instant.now());

        courseRepository.save(course);
        return mapObjectToDTO(course);

    }

    @Override
    public Page<CourseResponseDTO> getPagedCourses(String name, Set<String> categoryName, Pageable pageable) {
        Specification<Course> specification = Specification.where(CourseSpecification.hasName(name))
                .and(CourseSpecification.hasCategories(categoryName));
        Page<Course> coursePage = courseRepository.findAll(specification, pageable);

        return coursePage.map(CourseMapper::toDto);

    }
    // PRIVATE METHODS

    private Set<Label> resolveLabels(Set<String> requestedLabelNames) {
        List<Label> existingLabels = labelRepository.findByNameIn(requestedLabelNames);
        Set<String> existingNames = existingLabels.stream()
                .map(Label::getName)
                .collect(Collectors.toSet());

        Set<Label> newLabels = requestedLabelNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> Label.builder().name(name).build())
                .collect(Collectors.toSet());

        List<Label> savedNewLabels = labelRepository.saveAll(newLabels);

        Set<Label> allLabels = new HashSet<>(existingLabels);
        allLabels.addAll(savedNewLabels);
        return allLabels;
    }

    private Set<Category> resolveCategories(Set<String> requestedCategoryNames) {
        List<Category> existingCategories = categoryRepository.findByNameIn(requestedCategoryNames);
        Set<String> existingNames = existingCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toSet());

        Set<Category> newCategories = requestedCategoryNames.stream()
                .filter(name-> !existingNames.contains(name))
                .map(name -> Category.builder().name(name).build())
                .collect(Collectors.toSet());

        List<Category> savedNewCategories = categoryRepository.saveAll(newCategories);

        Set<Category> allCategories = new HashSet<>(existingCategories);
        allCategories.addAll(savedNewCategories);
        return allCategories;
    }

    private void detachCourseFromLabels(Course course) {
        for (Label label : course.getLabels()) {
            label.getCourses().remove(course);
        }
    }

    private CourseResponseDTO mapObjectToDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .uuid(course.getUUID())
                .name(course.getName())
                .labels(course.getLabels().stream()
                        .map(Label::getName)
                        .collect(Collectors.toSet()))
                .categories(course.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toSet()))
                .created(course.getCreated())
                .published(course.getPublished())
                .status(String.valueOf(course.getStatus()))
                .build();
    }


}
