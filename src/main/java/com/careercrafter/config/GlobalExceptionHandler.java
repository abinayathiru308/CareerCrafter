package com.careercrafter.config;

import com.careercrafter.dto.response.ErrorMessageDto;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ){
        BindingResult result =  e.getBindingResult();
        List<FieldError> list = result.getFieldErrors();
        Map<String,String> map = new HashMap<>();
        list.forEach(err-> map.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity
                .badRequest()
                .body(map);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handleResourceNotFoundException(
            ResourceNotFoundException e
    ){
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessageDto(e.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorMessageDto> handleInvalidCredentialsException(
            InvalidCredentialsException e
    ){
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessageDto(e.getMessage()));
    }
}