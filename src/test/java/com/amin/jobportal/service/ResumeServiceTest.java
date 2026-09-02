package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.DownloadFileResponse;
import com.amin.jobportal.dto.response.ResumeResponse;
import com.amin.jobportal.entity.Resume;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.exception.ConflictException;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.ResumeMapper;
import com.amin.jobportal.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ResumeServiceTest {

    @Mock private ResumeRepository resumeRepository;

    @Mock private FileStorageService fileStorageService;

    @Mock
    private ResumeMapper resumeMapper;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private User user;
    private User anotherUser;
    private Resume resume;
    private ResumeResponse resumeResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        anotherUser = new User();
        anotherUser.setId(2L);

        resume = new Resume();
        resume.setId(1L);
        resume.setOriginalFileName("CV.pdf");
        resume.setStorageKey("abc-123.pdf");
        resume.setUser(user); resumeResponse = new ResumeResponse();

    }

    @Test
    void uploadSuccessfully() throws IOException {
        MockMultipartFile file = new MockMultipartFile( "file", "CV.pdf", "application/pdf", "test content".getBytes() );

        ResumeResponse expected = resumeResponse;

        when(fileStorageService.saveFile(file)) .thenReturn("abc-123.pdf");
        when(resumeRepository.save(any(Resume.class))) .thenReturn(resume);
        when(resumeMapper.toResponse(resume)) .thenReturn(expected);

        ResumeResponse result = resumeService.upload(file, user);

        assertThat(result).isSameAs(expected);


        verify(fileStorageService).saveFile(file);
        verify(resumeRepository).save(any(Resume.class));
        verify(resumeMapper).toResponse(resume);
        verify(fileStorageService, never()).deleteFile(anyString());
    }


    @Test
    void uploadDeletesFileWhenSomethingFails() throws IOException {
        MockMultipartFile file = new MockMultipartFile( "file", "CV.pdf", "application/pdf", "test content".getBytes() );

        when(fileStorageService.saveFile(file)).thenReturn("abc-123.pdf");

        when(resumeRepository.save(any(Resume.class))).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> resumeService.upload(file, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(fileStorageService).saveFile(file);
        verify(fileStorageService).deleteFile("abc-123.pdf");
    }

    @Test
    void GetMyResumesSuccessfully(){
        Resume secondResume = new Resume();
        secondResume.setId(2L);
        secondResume.setUser(user);

        ResumeResponse secondResponse = new ResumeResponse();

        when(resumeRepository.findAllByUserId(user.getId()))
                .thenReturn(List.of(resume, secondResume));

        when(resumeMapper.toResponse(resume))
                .thenReturn(resumeResponse);

        when(resumeMapper.toResponse(secondResume))
                .thenReturn(secondResponse);

        List<ResumeResponse> result = resumeService.getMyResumes(user);

        assertThat(result).containsExactly(resumeResponse, secondResponse);

        verify(resumeRepository).findAllByUserId(1L);
        verify(resumeMapper).toResponse(resume);
        verify(resumeMapper).toResponse(secondResume);

    }

    @Test
    void getMyResumesReturnsEmptyListWhenNoResumes() {
        when(resumeRepository.findAllByUserId(user.getId())).thenReturn(List.of());

        List<ResumeResponse> result = resumeService.getMyResumes(user);

        assertThat(result).isEmpty();

        verify(resumeRepository).findAllByUserId(1L);
    }

    @Test
    void downloadSuccessfully() throws FileNotFoundException {
        File file = mock(File.class);

        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));

        when(fileStorageService.getDownloadFile("abc-123.pdf")) .thenReturn(file);

        DownloadFileResponse result = resumeService.downloadById(1L, user);

        assertThat(result).isNotNull(); assertThat(result.getFile()).isSameAs(file);
        assertThat(result.getOriginalFileName()).isEqualTo("CV.pdf");

        verify(resumeRepository).findById(1L);
        verify(fileStorageService).getDownloadFile("abc-123.pdf");
    }

    @Test
    void downloadThrowsWhenResumeDoesNotExist() throws FileNotFoundException {
        when(resumeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.downloadById(99L, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resume not found with id: 99");

        verify(fileStorageService, never()).getDownloadFile(anyString());
    }

    @Test
    void downloadThrowsWhenResumeBelongsToAnotherUser() throws FileNotFoundException {
        resume.setUser(anotherUser);

        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));

        assertThatThrownBy(() -> resumeService.downloadById(1L, user)).isInstanceOf(ForbiddenException.class).hasMessage("You cannot access other user's resumes");

        verify(fileStorageService, never()).getDownloadFile(anyString());
    }

    @Test
    void deleteSuccessfully() throws IOException {
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));

        resumeService.delete(1L, user);

        verify(fileStorageService).deleteFile("abc-123.pdf");
        verify(resumeRepository).delete(resume);
    }

    @Test
    void deleteThrowsWhenResumeDoesNotExist() throws FileNotFoundException {
        when(resumeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.delete(99L, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resume not found with id: 99");

        verify(fileStorageService, never()).deleteFile(anyString());
        verify(resumeRepository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenResumeBelongsToAnotherUser() throws FileNotFoundException {
        resume.setUser(anotherUser);

        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));

        assertThatThrownBy(() -> resumeService.delete(1L, user))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You cannot access other user's resumes");

        verify(fileStorageService, never()).deleteFile(anyString());
        verify(resumeRepository, never()).delete(any()); }
}
