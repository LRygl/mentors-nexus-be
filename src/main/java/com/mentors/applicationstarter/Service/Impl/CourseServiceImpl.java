package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseStatusDTO;
import com.mentors.applicationstarter.Enum.CourseStatus;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.Role;
import com.mentors.applicationstarter.Exception.BusinessRuleViolationException;
import com.mentors.applicationstarter.Exception.InvalidRequestException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.CourseMapper;
import com.mentors.applicationstarter.Model.*;
import com.mentors.applicationstarter.Repository.*;
import com.mentors.applicationstarter.Service.CourseService;
import com.mentors.applicationstarter.Service.FileStorageService;
import com.mentors.applicationstarter.Specification.CourseSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.mentors.applicationstarter.Constant.FileConstant.COURSE_FOLDER;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LabelRepository labelRepository;
    private final CategoryRepository categoryRepository;
    private final LessonRepository lessonRepository;
    private final FileStorageService fileStorageService;
    private final CourseMapper courseMapper;

    @Override
    public Page<CourseResponseDTO> getPagedCourses(String name, Set<String> categoryName, Pageable pageable) {
        Specification<Course> specification = CourseSpecification.hasName(name)
                .and(CourseSpecification.hasCategories(categoryName));
        Page<Course> coursePage = courseRepository.findAll(specification, pageable);

        return coursePage.map(courseMapper::toDto);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDTO> getAllFeaturedCourses() {
        return courseRepository.findByFeaturedTrue().stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponseDTO getCourseById(Long courseId) {
        Course course = findCourseById(courseId);
        return courseMapper.toDto(course);
    }

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request, MultipartFile file) {
        Set<Label> labels = resolveLabels(request.getLabels());
        Set<Category> categories = resolveCategoriesByIds(request.getCategoryIds());
        User owner = getCourseOwner(request.getCourseOwnerId());

        UUID courseUUID = UUID.randomUUID();
        //UUID authenticatedUserUuid = getAuthenticatedUserUuid();

        // Create Folder for course data and store the image
        fileStorageService.createEntityDirectory(COURSE_FOLDER, courseUUID.toString());

        String imagePath = null;
        if (file != null) {
            imagePath = fileStorageService.storeFile(
                    COURSE_FOLDER,
                    "Image",
                    courseUUID,
                    file
            );
        }

        Course course = Course.builder()
                .uuid(courseUUID)
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(Instant.now())
                //.createdBy(authenticatedUserUuid)
                .status(CourseStatus.UNPUBLISHED)
                .price(request.getPrice())
                .categories((categories))
                .labels(labels)
                .owner(owner)
                .imageUrl(imagePath)
                .build();

        if (request.getGoals() != null) {
            course.getGoals().clear();

            int position = 0;
            for (String outcome : request.getGoals()) {
                course.getGoals().add(
                        CourseGoals.builder()
                                .description(outcome)
                                .position(position++)
                                .course(course)
                                .build()
                );
            }
        }

        if (request.getRequirements() != null) {
            course.getRequirements().clear();

            int position = 0;
            for (String req : request.getRequirements()) {
                course.getRequirements().add(
                        CourseRequirement.builder()
                                .description(req)
                                .position(position++)
                                .course(course)
                                .build()
                );
            }
        }


        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDto(savedCourse);
    }

    @Override
    public CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO dto, MultipartFile file) {
        Course course = findCourseById(courseId);

        UUID userUuid = getAuthenticatedUserUuid();

        course.setUpdatedBy(userUuid);
        course.setUpdatedAt(Instant.now());

        if(dto.getName() != null){
            course.setName(dto.getName());
        }
        if(dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            CourseStatus status = CourseStatus.valueOf(dto.getStatus().toUpperCase());
            course.setStatus(status);

            // Set published date logic
            if (dto.getPublished() != null) {
                course.setPublished(dto.getPublished());
            } else if (status == CourseStatus.PUBLISHED) {
                course.setPublishedAt(Instant.now());
            }
        }
        // TODO change to toggle
        if (dto.getFeatured() != null) {
            course.setFeatured(dto.getFeatured());
        }

        if (dto.getCourseOwnerId() != null) {
            User user = userRepository.findById(dto.getCourseOwnerId()).orElseThrow(
                    () -> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST)
            );
            course.setOwner(user);
        }

        if (dto.getLabels() != null){
            course.setLabels(resolveLabels(dto.getLabels()));
        }
        if (dto.getCategoryIds() != null) {
            Set<Category> categories = resolveCategoriesByIds(dto.getCategoryIds());
            course.setCategories(categories);
        }

        if (dto.getCourseOwnerId() != null) {
            User owner = getCourseOwner(dto.getCourseOwnerId());
            course.setOwner(owner);
        }

        if (file != null) {
            String path = fileStorageService.storeFile(
                    COURSE_FOLDER,
                    "Image",
                    course.getUuid(),
                    file
            );
            course.setImageUrl(path);
        }

        if (dto.getGoals() != null) {
            course.getGoals().clear();

            int position = 0;
            for (String outcome : dto.getGoals()) {
                course.getGoals().add(
                        CourseGoals.builder()
                                .description(outcome)
                                .position(position++)
                                .course(course)
                                .build()
                );
            }
        }

        if (dto.getRequirements() != null) {
            course.getRequirements().clear();

            int position = 0;
            for (String req : dto.getRequirements()) {
                course.getRequirements().add(
                        CourseRequirement.builder()
                                .description(req)
                                .position(position++)
                                .course(course)
                                .build()
                );
            }
        }

        Course updatedCourse = courseRepository.save(course);

        return courseMapper.toDto(updatedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO deleteCourse(Long courseId) {
        Course course = findCourseById(courseId);

        detachCourseAssociations(course);
        courseRepository.delete(course);

        return courseMapper.toDto(course);
    }

    @Override
    public CourseResponseDTO updateCourseStatus(Long id, CourseStatusDTO courseStatusDTO) {
        Course course = findCourseById(id);
        CourseStatus newStatus = courseStatusDTO.getStatus();

        if(course.getStatus() == CourseStatus.PUBLISHED && newStatus == CourseStatus.PUBLISHED){
            return courseMapper.toDto(course);
        }

        //TODO Course has to have a category when published
        switch (newStatus) {
            case PUBLISHED, HIDDEN, PRIVATE -> {
                if(course.getPublished() == null) {
                    course.setPublishedAt(Instant.now());
                }
            }
            case UNPUBLISHED -> course.setPublished(null);
        }

        course.setStatus(courseStatusDTO.getStatus());
        course.setUpdatedAt(Instant.now());
        course.setPublishedAt(courseStatusDTO.getPublishedAt());

        courseRepository.save(course);
        return courseMapper.toDto(course);

    }

    // TODO Reorder sections
    // TODO New section is assigned maxindex+1
    // TODO Method to reorder section index in BE
    @Override
    public CourseResponseDTO addLessonToCourseSection(Long sectionId, Long lessonId) {

        CourseSection section = findSectionById(sectionId);
        Lesson lesson = findLessonById(lessonId);

        lesson.setSection(section);

        // Set course section order
        if (lesson.getOrderIndex() == null) {
            int nextOrder = section.getLessons().isEmpty() ? 1 :
                    section.getLessons().stream().mapToInt(Lesson::getOrderIndex).max().orElse(0) + 1;
            lesson.setOrderIndex(nextOrder);
        }

        section.getLessons().add(lesson);
        lessonRepository.save(lesson);

        Course course = courseRepository.findById(section.getCourse().getId()).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        return courseMapper.toDto(course);

    }

    @Override
    public CourseResponseDTO removeLessonFromCourse(Long sectionId, Long lessonId) {
        Lesson lesson = findLessonById(lessonId);
        CourseSection section = findSectionById(sectionId);

        section.getLessons().remove(lesson);
        courseSectionRepository.save(section);
        lesson.setSection(null);
        lesson.setOrderIndex(null);
        lessonRepository.save(lesson);

        return courseMapper.toDto(findCourseById(section.getCourse().getId()));

    }

    @Override
    @Transactional
    public CourseResponseDTO reorderCourseSections(List<Long> sectionOrderIds) {

        if (sectionOrderIds == null ||  sectionOrderIds.isEmpty()) {
            throw new InvalidRequestException(ErrorCodes.COURSE_DOES_NOT_EXIST);
        }

        // Find parent course based on the first section in the array
        Course course = courseRepository.findByCourseSection(sectionOrderIds.getFirst());

        // Convert course sections to a mutable list for ordering
        List<CourseSection> courseSections = new ArrayList<>(course.getSections());

        // Build the ordered list
        List<CourseSection> orderedSections = new ArrayList<>();

        // Add only those sections in the request that belong to the course
        for (int i = 0; i < sectionOrderIds.size(); i++) {
            Long id = sectionOrderIds.get(i);
            for (CourseSection section : courseSections) {
                if (section.getId().equals(id)) {
                    section.setOrderIndex(i + 1);
                    orderedSections.add(section);
                    break;
                }
            }
        }

        // Append any remaining sections that were not part of the request to the end
        int nextOrder = orderedSections.size() + 1;
        for (CourseSection section : courseSections) {
            if (orderedSections.stream().noneMatch(s->s.getId().equals(section.getId()))) {
                section.setOrderIndex(nextOrder++);
                orderedSections.add(section);
            }
        }

        // Persist updated order
        course.setSections(new HashSet<>(orderedSections));
        courseRepository.save(course);

        return courseMapper.toDto(course);
    }



    @Override
    public CourseResponseDTO createCourseSection(CourseSection section, Long courseId) {
        // Get parent course
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        // Get existing sections for a course orded by orderIndex
        List<CourseSection> sections = courseSectionRepository.findByCourseOrderByOrderIndexAsc(course);

        // Set course section order
        if (section.getOrderIndex() == null) {
            int nextOrder = course.getSections().isEmpty() ? 1 :
                    course.getSections().stream().mapToInt(CourseSection::getOrderIndex).max().orElse(0) + 1;
            section.setOrderIndex(nextOrder);
        } else {
        // Insert item at specific position
            int insertAt = section.getOrderIndex();
            for (CourseSection existing : sections) {
                if (existing.getOrderIndex() >= insertAt) {
                    existing.setOrderIndex(existing.getOrderIndex() + 1);
                }
            }
            courseSectionRepository.saveAll(sections);
        }

        section.setTitle(section.getTitle());
        section.setUuid(UUID.randomUUID());
        section.setDescription(section.getDescription());
        section.setOrderIndex(section.getOrderIndex());
        section.setCourse(course);

        courseSectionRepository.save(section);

        course.getSections().add(section);
        courseRepository.save(course);

        return courseMapper.toDto(course);
    }

    @Override
    public CourseResponseDTO delteCourseSection(Long id) {
        CourseSection section = courseSectionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        Course course = section.getCourse();
        course.getSections().remove(section);

        // Todo unassign all lessons from the section

        courseSectionRepository.deleteById(id);

        return courseMapper.toDto(course);
    }


    @Override
    public CourseResponseDTO featureCourse(Long id) {
        Course course = findCourseById(id);

        course.setFeatured(true);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Override
    public CourseResponseDTO unfeatureCourse(Long id) {
        Course course = findCourseById(id);

        course.setFeatured(false);
        courseRepository.save(course);

        return courseMapper.toDto(course);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Lesson findLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.LESSON_NOT_FOUND));
    }

    private Course findCourseById(Long courseId){
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));
    }

    private CourseSection findSectionById(Long sectionId) {
        return courseSectionRepository.findById(sectionId).orElseThrow(
                ()-> new ResourceNotFoundException(ErrorCodes.COURSE_SECTION_DOES_NOT_EXIST));
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

    private Set<Category> resolveCategoriesByIds(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }

        LOGGER.info("Resolving categories by IDs: {}", categoryIds);

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        // Validate that all requested categories exist
        if (categories.size() != categoryIds.size()) {
            Set<Long> foundIds = categories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());

            Set<Long> missingIds = categoryIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            LOGGER.warn("Some category IDs not found: {}", missingIds);
            throw new ResourceNotFoundException(ErrorCodes.CATEGORY_DOES_NOT_EXIST);
        }

        LOGGER.info("Resolved {} categories", categories.size());
        return new HashSet<>(categories);
    }

    private void detachCourseAssociations(Course course) {
        // Detach from Labels
        for (Label label : course.getLabels()) {
            label.getCourses().remove(course);
        }
        course.getLabels().clear();

        // Detach from Categories
        for (Category category : course.getCategories()) {
            category.getCourses().remove(course);
        }
        course.getCategories().clear();

        // Detach Sections (and their Lessons)y
        for (CourseSection section : course.getSections()) {
            for (Lesson lesson : section.getLessons()) {
                lesson.setSection(null); // remove reference to parent Section
            }
            section.getLessons().clear();
            section.setCourse(null); // remove reference to parent Course
        }
        course.getSections().clear();
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

    private UUID getAuthenticatedUserUuid() {
        return ((User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUUID();
    }
}
