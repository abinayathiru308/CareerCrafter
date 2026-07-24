package com.careercrafter.mapper;
import com.careercrafter.dto.request.EmployerReqDto;
import com.careercrafter.model.Employer;
import org.springframework.stereotype.Component;
@Component
public class EmployerMapper {
    public static Employer convertDtoToEntity(EmployerReqDto dto){
        Employer employer = new Employer();
        employer.setCompanyName(dto.companyName());
        employer.setCity(dto.city());
        return employer;
    }
}