package com.amin.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor

public class DownloadFileResponse {
    private final File file;
    private final String originalFileName;
}
