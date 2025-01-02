package com.example.matcher.profileservice.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3Service {
    private final S3Client s3Client;
    @Value("${aws.s3.buck_name}")
    private final String BUCK_NAME = "matcherphototest";  // Имя вашего бакета

    @Value("${aws.s3.my_domain}")
    private String MY_DOMAIN;
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }



    public String uploadFile(UUID userId, MultipartFile file) throws IOException {

        String uniqueFileName = generateFileName(userId, Objects.requireNonNull(file.getOriginalFilename()));
        // Загружаем файл в S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCK_NAME)
                .key(uniqueFileName)
                .acl(ObjectCannedACL.PUBLIC_READ) // Делаем файл публичным
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return MY_DOMAIN + "/" + uniqueFileName;
    }

    public boolean doesFileExist(String fileName) {
        try {
            // Создаем запрос на получение метаданных файла
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(BUCK_NAME)  // имя бакета
                    .key(fileName)       // имя файла
                    .build();

            // Отправляем запрос
            s3Client.headObject(headObjectRequest);
            return true; // Если запрос выполнен успешно, файл существует
        } catch (NoSuchKeyException e) {
            return false; // Если ключ не найден, файл не существует
        } catch (S3Exception e) {
            // Обрабатываем другие возможные ошибки (например, проблемы с правами доступа)
            e.printStackTrace();
            return false;
        }
    }
    public UrlResource downloadFile(String fileName) throws IOException {
        // Создаем запрос для получения файла
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCK_NAME)
                .key(fileName)
                .build();
        // Проверка наличия файла
        if (!doesFileExist(fileName)) {
            throw new IOException("File " + fileName + " does not exist in S3");
        }

//        Path tempDir = Files.createTempDirectory("photoTempS3");
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "photoTempS3");
        Files.createDirectories(tempDir);  // Создаст директорию, если она не существует
        File tempFile = new File(tempDir.toFile(), fileName);
        if (tempFile.exists()) {
            if (tempFile.delete()) {
                logger.info("Deleted existing file: " + tempFile.getAbsolutePath());
            } else {
                logger.warn("Failed to delete existing file: " + tempFile.getAbsolutePath());
            }
        }
        // Временное сохранение файла
//        Path tempFilePath = Files.createTempFile("download-", fileName);
        try {
            GetObjectResponse response = s3Client.getObject(getObjectRequest, ResponseTransformer.toFile(tempFile.toPath()));
            if (response.sdkHttpResponse().isSuccessful()) {
                logger.info("File downloaded successfully.");
            } else {
                logger.error("Failed to download the file.");
            }


//            logger.info("File downloaded to " + tempFilePath.toString());
        } catch (S3Exception e) {
            throw new IOException("Error downloading file from S3", e);
        }

        // Преобразование загруженного файла в ресурс для возврата
        return new UrlResource(tempFile.toPath().toUri());
    }

    public void deleteFile(String fileUrl) {
        String fileName = extractFileName(fileUrl);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCK_NAME)
//                .key(fileName)
                .key("2024-06-15-161634.jpg")
                .build();
        if (doesFileExist(fileName)) {
            logger.info("Photo DONT delete from s3. fileName: " + fileName);
        }
        else {
            logger.info("Photo delete from s3. Filename: " + fileName);
        }
    }

    private String extractFileName(String url) {
        return url.substring(url.indexOf(MY_DOMAIN) + MY_DOMAIN.length());
    }

    private String generateFileName(UUID userId, String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        return "users/" + userId.toString() + "/photos/" + UUID.randomUUID() + extension;
    }
}
