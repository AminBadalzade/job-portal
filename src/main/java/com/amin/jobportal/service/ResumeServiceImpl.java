package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.ResumeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    @Override
    public ResumeResponse upload(MultipartFile multipartFile) {
        return null;
    }

    @Override
    public List<ResumeResponse> getMyResumes() {
        return List.of();
    }

    @Override
    public ResumeResponse getById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
