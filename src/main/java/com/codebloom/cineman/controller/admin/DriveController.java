package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.GoogleDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Upload file to gg drive")
@Slf4j(topic = "DRIVE-CONTROLLER")
@RequestMapping("${api.path}/drive")
public class DriveController {

    private final GoogleDriveService googleDriveService;

    @Operation(summary = "upload file to drive", description = "API dùng để upload file ảnh của người dùng lên gg drive.")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> upload(@RequestParam("file") MultipartFile[] files) throws IOException{
        List<Map<String, String>> uploaded = new ArrayList<>();

        for (MultipartFile file : files) {
            uploaded.add(googleDriveService.uploadFile(file));
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("upload file successfully")
                        .data(uploaded)
                        .build()
        );
    }
    @Operation(summary = "delete file from drive", description = "API dùng để xóa file ảnh của người dùng trên gg drive.")
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable String fileId) throws IOException{
        googleDriveService.deleteFile(fileId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("delete file successfully")
                        .data(null)
                        .build()
        );
    }

    @Operation(summary = "download file from drive", description = "API dùng để tải file của người dùng trên gg drive.")
    @GetMapping("/download/{fileId}")
    public ResponseEntity<ApiResponse> downloadFile(@PathVariable String fileId) throws IOException{
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("delete file successfully")
                        .data(googleDriveService.downloadFile(fileId))
                        .build()
        );
    }

    @Operation(summary = "download file from drive", description = "API dùng để tải file của người dùng trên gg drive.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllFileIMG(@Re) throws IOException{
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("delete file successfully")
                        .data(googleDriveService.listAllFiles())
                        .build()
        );
    }
}
