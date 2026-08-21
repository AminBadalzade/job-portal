package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {
    ResumeResponse upload(MultipartFile multipartFile);

    List<ResumeResponse> getMyResumes();

    ResumeResponse getById(Long id);

    void delete(Long id);
}
