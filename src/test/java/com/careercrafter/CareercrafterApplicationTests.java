package com.careercrafter;

import com.careercrafter.dto.request.JobListingReqDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CareercrafterApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void testJobListingReqDtoConstructors() {
        var dto1 = new JobListingReqDto("Developer", "Job Description");
        var dto2 = new JobListingReqDto("Developer", "Job Description", 1L);

        org.junit.jupiter.api.Assertions.assertNotNull(dto1);
        org.junit.jupiter.api.Assertions.assertNotNull(dto2);
    }

}
