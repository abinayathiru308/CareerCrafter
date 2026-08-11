package com.careercrafter.utility;

import com.careercrafter.exception.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class UploadUtility {

    public void validateResume(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new FileUploadException("Resume file not given");

        String fileName = file.getOriginalFilename();
        List<String> allowedList = List.of("pdf");
        String[] arr = fileName.split("\\.");

        if (arr.length != 2)
            throw new FileUploadException("Invalid file name");

        String ext = arr[1];

        if (!allowedList.contains(ext))
            throw new FileUploadException("Extension not allowed");
    }
}