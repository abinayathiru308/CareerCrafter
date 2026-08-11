package com.careercrafter.controller;

import com.careercrafter.dto.response.SavedJobRespDto;
import com.careercrafter.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/savedjob")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/save/{jobSeekerId}/{jobListingId}")
    public void save(

            @PathVariable long jobSeekerId,

            @PathVariable long jobListingId

    ){
        savedJobService.save(jobSeekerId, jobListingId);
    }

    @GetMapping("/by-jobseeker/{jobSeekerId}")
    public List<SavedJobRespDto> getByJobSeekerId(@PathVariable long jobSeekerId) {
        return savedJobService.getByJobSeekerId(jobSeekerId);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable long id) {
        savedJobService.delete(id);
    }

}