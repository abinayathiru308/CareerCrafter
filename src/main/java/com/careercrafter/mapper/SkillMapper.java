package com.careercrafter.mapper;

import com.careercrafter.dto.request.SkillReqDto;
import com.careercrafter.dto.response.SkillRespDto;
import com.careercrafter.model.Skill;

public class SkillMapper {

    public static Skill convertDtoToEntity(SkillReqDto dto) {

        Skill skill = new Skill();
        skill.setName(dto.name());

        return skill;
    }

    public static SkillRespDto convertEntityToDto(Skill skill) {

        return new SkillRespDto(

                skill.getId(),
                skill.getName()

        );
    }

}