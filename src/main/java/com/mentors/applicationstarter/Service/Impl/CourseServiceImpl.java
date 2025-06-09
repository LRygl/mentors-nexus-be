package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Enum.CourseStatus;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.Role;
import com.mentors.applicationstarter.Exception.BusinessRuleViolationException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.CourseMapper;
import com.mentors.applicationstarter.Model.Category;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.Label;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.CategoryRepository;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Repository.LabelRepository;
import com.mentors.applicationstarter.Repository.UserRepository;
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
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        Set<Label> labels = resolveLabels(request.getLabels());
        Set<Category> categories = resolveCategories(request.getCategories());

        User owner = getCourseOwner(request.getCourseOwnerId());

        Course course = Course.builder()
                .uuid(UUID.randomUUID())
                .name(request.getName())
                .created(Instant.now())
                .status(CourseStatus.UNPUBLISHED)
                .price(request.getPrice())
                .categories((categories))
                .labels(labels)
                .owner(owner)
                .build();

        Course savedCourse = courseRepository.save(course);

        return CourseMapper.toDto(savedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO getCourseById(Long courseId) {
        Course course = findCourseById(courseId);
        return CourseMapper.toDto(course);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDTO updateCourse(CourseRequestDTO dto) {
        Course course = findCourseById(dto.getId());

        if(dto.getName() != null){
            course.setName(dto.getName());
        }
        course.setUpdated(Instant.now());
        if (dto.getLabels() != null){
            course.setLabels(resolveLabels(dto.getLabels()));
        }
        if (dto.getCategories() != null){
            course.setCategories(resolveCategories(dto.getCategories()));
        }
        if (dto.getCourseOwnerId() != null) {
            User owner = getCourseOwner(dto.getCourseOwnerId());
            course.setOwner(owner);
        }

        Course updatedCourse = courseRepository.save(course);
        return CourseMapper.toDto(updatedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO deleteCourse(Long courseId) {
        Course course = findCourseById(courseId);

        detachCourseFromLabels(course);
        courseRepository.delete(course);

        return CourseMapper.toDto(course);
    }

    @Override
    public CourseResponseDTO updateCourseStatus(Long id, CourseStatusDTO courseStatusDTO) {
        Course course = findCourseById(id);
        CourseStatus newStatus = courseStatusDTO.getStatus();

        if(course.getStatus() == CourseStatus.PUBLISHED && newStatus == CourseStatus.PUBLISHED){
            return CourseMapper.toDto(course);
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
        course.setPublished(courseStatusDTO.getPublished());


        courseRepository.save(course);
        return CourseMapper.toDto(course);

    }

    @Override
    public Page<CourseResponseDTO> getPagedCourses(String name, Set<String> categoryName, Pageable pageable) {
        Specification<Course> specification = Specification.where(CourseSpecification.hasName(name))
                .and(CourseSpecification.hasCategories(categoryName));
        Page<Course> coursePage = courseRepository.findAll(specification, pageable);

        return coursePage.map(CourseMapper::toDto);

    }

    @Override
    @Transactional
    public void enrollUserToCourse(Long courseId, UUID userUUID) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow( () -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        User user = userRepository.findByUUID(userUUID)
                .orElseThrow( () -> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));

        course.getStudents().add(user);
        courseRepository.save(course);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Course findCourseById(Long courseId){
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));
    }

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
                .map(name -> Category.builder()
                        .name(name)
                        .UUID(UUID.randomUUID())
                        .created(Instant.now())
                        .build()
                )
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

    private User getCourseOwner(Long ownerId){
        User owner = null;
        if (ownerId != null) {
            owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));

            if(owner.getRole() == Role.USER) {
                throw new BusinessRuleViolationException(ErrorCodes.COURSE_CANNOT_BE_ASSIGNED);
            }
        }
        return owner;
    }

}
