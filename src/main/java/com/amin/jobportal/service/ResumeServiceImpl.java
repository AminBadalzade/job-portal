package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.DownloadFileResponse;
import com.amin.jobportal.dto.response.ResumeResponse;
import com.amin.jobportal.entity.Resume;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.exception.ConflictException;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.ResumeMapper;
import com.amin.jobportal.repository.JobApplicationRepository;
import com.amin.jobportal.repository.ResumeRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileStorageService fileStorageService;
    private final ResumeMapper resumeMapper;
    private final JobApplicationRepository jobApplicationRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository, FileStorageService fileStorageService, ResumeMapper resumeMapper, JobApplicationRepository jobApplicationRepository) {
        this.resumeRepository = resumeRepository;
        this.fileStorageService = fileStorageService;
        this.resumeMapper = resumeMapper;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Override
    public ResumeResponse upload(MultipartFile multipartFile, User user) throws IOException {
        String storageKey = null;

        try {
            storageKey = fileStorageService.saveFile(multipartFile);

            Resume resume = new Resume();
            resume.setOriginalFileName(multipartFile.getOriginalFilename());
            resume.setStorageKey(storageKey);
            resume.setUser(user);

            Resume savedResume = resumeRepository.save(resume);

            return resumeMapper.toResponse(savedResume);

        } catch (Exception e) {
            if (storageKey != null) {
                fileStorageService.deleteFile(storageKey);
            }

            throw e;
        }
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Override
    public List<ResumeResponse> getMyResumes(User user) {
        List<Resume> resumes =
                resumeRepository.findAllByUserId(user.getId());

        return resumes.stream()
                .map(resumeMapper::toResponse)
                .toList();
    }

    @Override
    public DownloadFileResponse downloadById(Long id, User user) throws FileNotFoundException {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + id));

        boolean isOwner = resume.getUser().getId().equals(user.getId());
        boolean isReviewingStaff = user.getCompany() != null
                && jobApplicationRepository.existsByResumeIdAndJob_Company_Id(id, user.getCompany().getId());


        if (!isOwner && !isReviewingStaff) {
            throw new ForbiddenException("You cannot access this resume");
        }

        File file = fileStorageService.getDownloadFile(resume.getStorageKey());

        return new DownloadFileResponse(file, resume.getOriginalFileName());
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Transactional
    @Override
    public void delete(Long id, User user) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found with id: " + id));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You cannot access other user's resumes");
        }

        try {
            fileStorageService.deleteFile(resume.getStorageKey());
        } catch (IOException e) {
            throw new RuntimeException("Could not delete resume file", e);
        }

        resumeRepository.delete(resume);
    }
}
