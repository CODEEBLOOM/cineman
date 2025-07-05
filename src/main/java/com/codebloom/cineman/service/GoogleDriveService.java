package com.codebloom.cineman.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import com.google.api.services.drive.model.Permission;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
@Slf4j(topic = "GOOGLE-DRIVE-SERVICE")
@RequiredArgsConstructor
public class GoogleDriveService {

    private final Drive googleDrive;

    public Map<String, String> uploadFile(MultipartFile file) throws IOException {
        // đặt tên file
        String originalName = file.getOriginalFilename();
        if (originalName == null) originalName = "unknown";

        String name = System.currentTimeMillis() + originalName;
        String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String fileName = Integer.toHexString(name.hashCode()) + extension;

        // Tạo file tạm
        File tempFile = java.io.File.createTempFile("upload-", extension);
        file.transferTo(tempFile);

        // Xác định MIME type
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = Files.probeContentType(tempFile.toPath()); // fallback
        }

        // Xác định folderId theo loại file
        String imageFolderId = "14rzD0AbUSmnu9Ybnp9kCY1DXWtlXMUVR";
        String textFolderId = "1CGCq6iMRfqVsLcIotXpTdkEqGcYq5ek-";
        String otherFolderId = "1oHQAw205il5vwuLniXqpouWJFWNLoAIm";

        String folderId;
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                folderId = imageFolderId;
            } else if (contentType.equals("text/plain") || contentType.startsWith("text/")) {
                folderId = textFolderId;
            } else {
                folderId = otherFolderId;
            }
        } else {
            folderId = otherFolderId;
        }

        // Metadata
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));

        // Nội dung
        FileContent mediaContent = new FileContent(contentType, tempFile);

        // Upload lên Drive
        com.google.api.services.drive.model.File uploadedFile = googleDrive.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        // Cấp quyền công khai
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        googleDrive.permissions().create(uploadedFile.getId(), permission).execute();

        // Trả kết quả
        String publicUrl = "https://drive.google.com/uc?id=" + uploadedFile.getId();
        return Map.of(
                "id", uploadedFile.getId(),
                "url", publicUrl,
                "name", fileName
        );
    }


    // xóa file theo fileId
    public void deleteFile(String fileId) throws IOException {
        googleDrive.files().delete(fileId).execute();
    }

    // download file theo id
    public ResponseEntity<byte[]> downloadFile(String fileId) {
        try {
            // Lấy metadata để biết tên file và loại MIME
            com.google.api.services.drive.model.File fileMetadata = googleDrive.files().get(fileId)
                    .setFields("name, mimeType")
                    .execute();

            String fileName = fileMetadata.getName();
            String contentType = fileMetadata.getMimeType();

            // Tải nội dung file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            googleDrive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            byte[] fileBytes = outputStream.toByteArray();

            // Chuẩn bị header cho phản hồi
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
            headers.setContentLength(fileBytes.length);

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            // Lỗi khi tải file
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Lỗi khi tải file: " + e.getMessage()).getBytes());
        }
    }
    // truy vấn toàn bộ file từ drive
    public List<FileInfo> listAllFiles() throws IOException {
        List<FileInfo> files = new ArrayList<>();

        String pageToken = null;
        String folderId = "14rzD0AbUSmnu9Ybnp9kCY1DXWtlXMUVR";
        String query = "'" + folderId + "' in parents and trashed = false";

        do {
            FileList result = googleDrive.files().list()
                    .setQ(query) // <-- lọc theo folder
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();

            for (com.google.api.services.drive.model.File file : result.getFiles()) {
                files.add(new FileInfo(file.getId(), file.getName()));
            }

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return files;
    }

    public record FileInfo(String id, String name) {}
}
