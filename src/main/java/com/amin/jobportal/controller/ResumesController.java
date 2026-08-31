package com.amin.jobportal.controller;

import com.amin.jobportal.dto.response.DownloadFileResponse;
import com.amin.jobportal.dto.response.ResumeResponse;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.service.ResumeService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/resumes")
public class ResumesController {

    private final ResumeService resumeService;

    public ResumesController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal User user) throws IOException {

        ResumeResponse response = resumeService.upload(multipartFile, user);
        return ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getAllResumes(@AuthenticationPrincipal User user) {
     return ok(resumeService.getMyResumes(user));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) throws IOException {

        DownloadFileResponse downloadFile =
                resumeService.downloadById(id, user);

        Resource resource =
                new FileSystemResource(downloadFile.getFile());

        String contentType = Files.probeContentType(
                downloadFile.getFile().toPath()
        );

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                downloadFile.getOriginalFileName() +
                                "\""
                )
                .contentLength(downloadFile.getFile().length())
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResumeById(@PathVariable Long id,@AuthenticationPrincipal User user){
        resumeService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

}
