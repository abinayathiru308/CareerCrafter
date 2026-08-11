package com.careercrafter.controller;

import com.careercrafter.dto.request.SkillReqDto;
import com.careercrafter.dto.response.SkillRespDto;
import com.careercrafter.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/skill")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/add")
    public void insert(@RequestBody SkillReqDto dto) {
        skillService.insert(dto);
    }

    @GetMapping("/get-all")
    public List<SkillRespDto> getAll() {
        return skillService.getAll();
    }

    @PostMapping("/{jobSeekerId}/add-skill/{skillId}")
    public void addSkillToJobSeeker(

            @PathVariable long jobSeekerId,

            @PathVariable long skillId

    ){
        skillService.addSkillToJobSeeker(jobSeekerId, skillId);
    }

}