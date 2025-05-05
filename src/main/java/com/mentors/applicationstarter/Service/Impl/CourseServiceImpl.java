package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CourseRequestDTO;
import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.Label;
import com.mentors.applicationstarter.Repository.CourseRepository;
import com.mentors.applicationstarter.Repository.LabelRepository;
import com.mentors.applicationstarter.Service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final LabelRepository labelRespository;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {

        Set<String> requestedLabelNames = request.getLabels();
        List<Label> existingLabels = labelRespository.findByNameIn(requestedLabelNames);

        Set<String> existingNames = existingLabels.stream()
                .map(Label::getName)
                .collect(Collectors.toSet());

        Set<Label> newLabels = requestedLabelNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> Label.builder().name(name).build())
                .collect(Collectors.toSet());

        List<Label> savedNewLabels = labelRespository.saveAll(newLabels);
        System.out.println("Saved new labels: " + savedNewLabels.size());

        Set<Label> allLabels = new HashSet<>(existingLabels);
        allLabels.addAll(savedNewLabels);
        System.out.println("Labels to be associated with the course: " + allLabels.size()); // Log this to check

        Course course = Course.builder()
                .created(Instant.now())
                .UUID(UUID.randomUUID())
                .name(request.getName())
                .created(Instant.now())
                .published(Instant.parse(request.getPublished().toString()))
                .price(request.getPrice())
                .labels(allLabels)
                .build();

        // Associate the course with the labels by adding the course to the `courses` set in each label.
        for (Label label : allLabels) {
            label.getCourses().add(course);  // Add the course to the label's courses set
        }

        courseRepository.save(course);

        System.out.println("saved course labels: " + course.getLabels());


        return CourseResponseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .labels(course.getLabels().stream()
                        .map(Label::getName)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    @Transactional
    public CourseResponseDTO getCourseById(Long courseId) {

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COURSE_DOES_NOT_EXIST));

        System.out.println("Saved course labels: " + course.getLabels().size()); // Log this to check

        return CourseResponseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .labels(getCourseLabels(course))
                .build();

    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(course -> CourseResponseDTO.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .labels(getCourseLabels(course))
                        .build())
                .collect(Collectors.toList());
    }

    // PRIVATE METHODS

    private Set<String> getCourseLabels(Course course) {
       Set<String> labelNames = course.getLabels().stream()
                .map(Label::getName)
                .collect(Collectors.toSet());

       return labelNames;
    }
}
