package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.LessonDTO;
import com.mentors.applicationstarter.DTO.LessonDetailDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.VideoStatus;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.LessonMapper;
import com.mentors.applicationstarter.Model.Lesson;
import com.mentors.applicationstarter.Repository.LessonRepository;
import com.mentors.applicationstarter.Service.FileStorageService;
import com.mentors.applicationstarter.Service.LessonService;
import com.mentors.applicationstarter.Service.VideoConversionService;
import com.mentors.applicationstarter.Utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mentors.applicationstarter.Constant.FileConstant.LESSON_ENTITY;
import static com.mentors.applicationstarter.Constant.FileConstant.LESSON_FOLDER;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final FileStorageService fileStorageService;
    private final VideoConversionService videoConversionService;

    @Override
    public List<LessonDetailDTO> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(LessonMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDTO getLessonById(Long lessonId) {
        Lesson lesson = findLessonById(lessonId);
        return LessonMapper.toLessonDto(lesson);
    }

    @Override
    public Page<Lesson> getPagedLessons(Pageable pageable) {
        Page<Lesson> lessonPage = lessonRepository.findAll(pageable);
        return lessonPage;
    }

    @Override
    public LessonDTO createLesson(Lesson request) {

        UUID lessonUUID = UUID.randomUUID();
        UUID user = AuthUtils.getAuthenticatedUserUuid();

        Lesson lesson = Lesson.builder()
                .uuid(UUID.randomUUID())
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(null)
                .videoUrl(request.getVideoUrl())
                .duration(request.getDuration())
                .createdAt(Instant.now())
                .createdBy(user)
                .build();

        lessonRepository.save(lesson);
        return LessonMapper.toLessonDto(lesson);
    }

    @Override
    public LessonDTO updateLesson(Long lessonId, Lesson lesson, MultipartFile image, MultipartFile video) {
        Lesson existingLesson = lessonRepository.findById(lessonId).orElseThrow(
                ()-> new ResourceNotFoundException(ErrorCodes.LESSON_NOT_FOUND));

        UUID userUUID = AuthUtils.getAuthenticatedUserUuid();

        existingLesson.setUpdatedBy(userUUID);
        existingLesson.setUpdatedAt(Instant.now());
        if(lesson.getTitle() != null) {
            existingLesson.setTitle(lesson.getTitle());
        }

        if( lesson.getDuration() != null ) {
            existingLesson.setDuration(lesson.getDuration());
        }

        if(lesson.getType() != null) {
            existingLesson.setType(lesson.getType());
        }

        if(lesson.getCategory() != null) {
            existingLesson.setCategory(lesson.getCategory());
        }

        if(image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeFile(
                    LESSON_ENTITY,
                    "image",
                    existingLesson.getUuid(),
                    image
            );
            existingLesson.setImageUrl(imagePath);
        }

        if (video != null && !video.isEmpty()) {
            // Store original video
            String videoPath = fileStorageService.storeFile(
                    LESSON_ENTITY,
                    "video",
                    existingLesson.getUuid(),
                    video
            );

            existingLesson.setOriginalVideoUrl(videoPath);

            // Check if video is already MP4
            String contentType = video.getContentType();
            boolean isAlreadyMp4 = "video/mp4".equals(contentType) ||
                    videoPath.toLowerCase().endsWith(".mp4");

            if (isAlreadyMp4) {
                // Already MP4 - no conversion needed
                existingLesson.setVideoUrl(videoPath);
                existingLesson.setVideoStatus(VideoStatus.READY);
                log.info("Video is already MP4, no conversion needed: {}", videoPath);
            } else {
                // Needs conversion
                existingLesson.setVideoStatus(VideoStatus.UPLOADING);

                // Save first to get the ID
                Lesson savedLesson = lessonRepository.save(existingLesson);

                // Queue for conversion (happens in background)
                videoConversionService.queueVideoConversion(savedLesson.getId());

                log.info("Video uploaded and queued for conversion: {} ({})",
                        videoPath, contentType);

                return LessonMapper.toLessonDto(savedLesson);
            }
        }

        Lesson updatedLesson = lessonRepository.save(existingLesson);
        return LessonMapper.toLessonDto(updatedLesson);
    }


    @Override
    public LessonDTO deleteLesson(Long lessonId) {
        Lesson lesson = findLessonById(lessonId);
        lessonRepository.delete(lesson);
        return LessonMapper.toLessonDto(lesson);
    }

    private Lesson findLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.LESSON_NOT_FOUND));

    }
}
