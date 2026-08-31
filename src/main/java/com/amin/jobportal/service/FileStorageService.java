package com.amin.jobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path storageLocation;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public FileStorageService(@Value("${file.storage.location}") String storageDirectory) {
        this.storageLocation = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFileName =  StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        String extension = "";

        int dotIndex = originalFileName.lastIndexOf('.');

        if(dotIndex != -1){
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID() + extension;

        Path target = storageLocation
                .resolve(storedFileName)
                .normalize();


        if (!target.startsWith(storageLocation)) {
            throw new SecurityException("Invalid file path");
        }

        Files.copy(
                file.getInputStream(),
                target,
                StandardCopyOption.REPLACE_EXISTING
        );

        return storedFileName;

    }

    public File getDownloadFile(String fileName) throws FileNotFoundException {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is required");
        }

        Path filePath = storageLocation
                .resolve(fileName)
                .normalize();

        if (!filePath.startsWith(storageLocation)) {
            throw new SecurityException("Unsupported filename!");
        }

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("No file named: " + fileName);
        }

        return filePath.toFile();

    }

    public void deleteFile(String storageKey) throws FileNotFoundException {

        Path filePath = storageLocation
                .resolve(storageKey)
                .normalize();

        if (!filePath.startsWith(storageLocation)) {
            throw new SecurityException("Invalid file path");
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
    }
}
