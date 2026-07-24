package com.careercrafter.controller;
import com.careercrafter.dto.request.JobSeekerDto;
import com.careercrafter.dto.response.JobSeekerRespDto;
import com.careercrafter.model.JobSeeker;
import com.careercrafter.service.JobSeekerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
        import java.util.List;
@RestController
@RequestMapping("/api/jobseeker/")
@RequiredArgsConstructor
public class JobSeekerController {
    private final JobSeekerService jobSeekerService;
    @PostMapping("/add")
    public JobSeeker add(@Valid @RequestBody JobSeekerDto jobSeekerDto){
        return jobSeekerService.add(jobSeekerDto);
    }
    @GetMapping("/get-all")
    public List<JobSeekerRespDto> getAll(@RequestParam Integer page,
                                         @RequestParam Integer size){
        return jobSeekerService.getAll(page,size);
    }
    @GetMapping("/get-one/{id}")
    public JobSeekerRespDto getById(@PathVariable long id){
        return jobSeekerService.getById(id);
    }
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable long id){
        jobSeekerService.delete(id);
    }
    @PutMapping("/update/{id}")
    public void update(@PathVariable long id,
                       @Valid @RequestBody JobSeekerDto jobSeekerDto){
        jobSeekerService.update(id, jobSeekerDto);
    }
}