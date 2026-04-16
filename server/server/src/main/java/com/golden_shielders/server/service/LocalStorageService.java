package com.golden_shielders.server.service;


import com.golden_shielders.server.entity.UploadFile;
import com.golden_shielders.server.repository.LocalStorageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService {

    private final LocalStorageRepository localStorageRepository;

    @Value("${upload.local.dir}")
    private String uploadDir;

    @Transactional
    public void storeFiles(MultipartFile[] files, Integer postId) {
        if (files == null) return;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                storeFile(file, postId);
            }
        }
    }

    public UploadFile storeFile(MultipartFile file, Integer postId) {
        if (file == null || file.isEmpty()) return null;

        String originalName = file.getOriginalFilename();
        String storedName = UUID.randomUUID().toString() + "_" + originalName;
        String filePath = uploadDir + File.separator + storedName;

        try {
            // 1. 물리 파일 저장
            File dest = new File(filePath).getAbsoluteFile();
            if (dest.getParentFile() != null && !dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            // 2. 엔티티 생성
            UploadFile uploadFile = UploadFile.builder()
                    .postId(postId)
                    .originalName(originalName)
                    .storedName(storedName)
                    .filePath(filePath)
                    .build();

            // 3. DB 기록
            localStorageRepository.save(uploadFile);
            return uploadFile;

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}