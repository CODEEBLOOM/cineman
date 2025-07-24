package com.codebloom.cineman.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface GoogleDriveService {
    Map<String, String> uploadFile(MultipartFile file) throws IOException;
}
