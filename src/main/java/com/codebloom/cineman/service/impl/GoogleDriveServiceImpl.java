package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.service.GoogleDriveService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "GOOGLE-DRIVE-SERVICE")
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final Drive googleDrive;

    public Map<String, String> uploadFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        if (originalName == null) originalName = "unknown";

        String name = System.currentTimeMillis() + originalName;
        String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String fileName = Integer.toHexString(name.hashCode()) + extension;

        // Tạo file tạm
        java.io.File tempFile = java.io.File.createTempFile("upload-", extension);
        file.transferTo(tempFile);

        // Xác định MIME type
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = Files.probeContentType(tempFile.toPath());
        }

        // Chọn folderId theo loại file
        String folderId;
        if (contentType != null && contentType.startsWith("image/")) {
            folderId = "14rzD0AbUSmnu9Ybnp9kCY1DXWtlXMUVR";
        } else if (contentType != null && (contentType.equals("text/plain") || contentType.startsWith("text/"))) {
            folderId = "1CGCq6iMRfqVsLcIotXpTdkEqGcYq5ek-";
        } else {
            folderId = "1oHQAw205il5vwuLniXqpouWJFWNLoAIm";
        }

        // Metadata
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));

        // Nội dung file
        FileContent mediaContent = new FileContent(contentType, tempFile);

        // Upload lên Google Drive
        File uploadedFile;
        try {
            uploadedFile = googleDrive.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } catch (Exception e) {
            log.error("Error uploading file to Google Drive", e);
            throw e;
        }

        // Cấp quyền công khai
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        googleDrive.permissions().create(uploadedFile.getId(), permission).execute();

        String publicUrl = "https://drive.google.com/uc?id=" + uploadedFile.getId();

        return Map.of(
                "id", uploadedFile.getId(),
                "url", publicUrl,
                "name", fileName
        );
    }
}
