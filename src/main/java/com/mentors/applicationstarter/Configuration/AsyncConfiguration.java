package com.mentors.applicationstarter.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * Thread pool for video conversion tasks
     *
     * Settings explained:
     * - corePoolSize: Always keep 2 threads running
     * - maxPoolSize: Can grow to 5 threads under load
     * - queueCapacity: Can queue up to 100 conversion tasks
     */
    @Bean(name = "videoConversionExecutor")
    public Executor videoConversionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);        // Minimum threads
        executor.setMaxPoolSize(5);         // Maximum threads
        executor.setQueueCapacity(100);     // Task queue size
        executor.setThreadNamePrefix("video-conversion-");
        executor.initialize();
        return executor;
    }
}
