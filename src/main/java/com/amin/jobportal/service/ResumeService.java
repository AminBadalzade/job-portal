package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.DownloadFileResponse;
import com.amin.jobportal.dto.response.ResumeResponse;
import com.amin.jobportal.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ResumeService {
    ResumeResponse upload(MultipartFile multipartFile, User user) throws IOException;

    List<ResumeResponse> getMyResumes(User user);

    DownloadFileResponse downloadById(Long id, User user) throws FileNotFoundException;

    void delete(Long id, User user);
}
