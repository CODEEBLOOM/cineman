package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.FileManagerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}")
@Validated
@Tag(name = "File Manager")
public class FileManagerController {

    private final FileManagerService fileManagerService;


    @GetMapping( "/files/{folder}/{file}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("file") String file, @PathVariable("folder") String folder) {
        byte[] downloadFile = fileManagerService.read(folder, file);
        return ResponseEntity.status(OK)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(downloadFile);
    }

    @PostMapping("/files/{folder}/upload")
    public ResponseEntity<ApiResponse> uploadFile(@PathVariable("folder") String folder,@PathParam("file") MultipartFile file) {
        String fileName = fileManagerService.upload(folder, file);
        return ResponseEntity.ok().body(
                ApiResponse.builder()
                        .message("File uploaded successfully")
                        .status(HttpStatus.OK.value())
                        .data(fileName)
                        .build()
        );
    }

    @PostMapping("/files/{folder}")
    public ResponseEntity<ApiResponse> uploadFile(@PathVariable("folder") String folder,@PathParam("files") MultipartFile[] files) {
        List<String> fileNames = fileManagerService.save(folder,files);
        return ResponseEntity.ok().body(
                ApiResponse.builder()
                        .message("Files uploaded successfully")
                        .status(HttpStatus.OK.value())
                        .data(fileNames)
                        .build()
        );
    }

    @DeleteMapping("/files/{folder}/{file}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable("folder") String folder, @PathVariable("file") String file) {
        fileManagerService.delete(folder, file);
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .message("File deleted successfully")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/files/{folder}")
    public ResponseEntity<ApiResponse> getFiles(@PathVariable("folder") String folder) {
        List<String> fileNames =  fileManagerService.list(folder);
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .message("Files retrieved successfully")
                        .status(HttpStatus.OK.value())
                        .data(fileNames)
                        .build()
        );
    }
}
