package com.codebloom.cineman.service.util;

import com.codebloom.cineman.service.FileManagerService;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileManagerServiceImpl implements FileManagerService {


    private final ServletContext app;

    @Value("${cinema.upload_file.base_path}")
    private String basePath;

    private Path getPath(String folder, String fileName)  {
        try {
            URI uri = new URI(basePath.concat("/").concat(folder));
            File dir = Paths.get(uri).toFile();
            if (!dir.exists()) {
                boolean mkdirs = dir.mkdirs();
            }
            return Paths.get(dir.getAbsolutePath(), fileName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(String folder, String filename) {
        Path path = getPath(folder, filename);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> save(String folder, MultipartFile[] files) {
        List<String> fileNames = new ArrayList<String>();
        for (MultipartFile file : files) {
            String name = System.currentTimeMillis() + file.getOriginalFilename();
            String fileName = Integer.toHexString(name.hashCode()) + name.substring(name.lastIndexOf("."));
            Path path = getPath(folder, fileName);
            try {
                file.transferTo(path);
                fileNames.add(fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileNames;
    }

    @Override
    public String upload(String folder, MultipartFile file) {
        String name = System.currentTimeMillis() + file.getOriginalFilename();
        String fileName = Integer.toHexString(name.hashCode()) + name.substring(name.lastIndexOf("."));
        Path path = getPath(folder, fileName);
        try {
            file.transferTo(path);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String folder, String filename) {
        Path path = getPath(folder, filename);
        boolean delete = path.toFile().delete();
    }

    @Override
    public List<String> list(String folder) {
        List<String> list = new ArrayList<String>();

        File dir = Paths.get(app.getRealPath("/files/"), folder).toFile();
        if (dir.exists()) {
            File[] files = dir.listFiles();
            assert files != null;
            for (File file : files) {
                list.add(file.getName());
            }
        }
        return list;
    }
}
