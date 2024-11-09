package com.example.matcher.profileservice.service;


import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
@AllArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName = "matcherphototest";  // Имя вашего бакета
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);


    public String uploadFile(MultipartFile file) throws IOException {
        // Временное сохранение файла
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);
        Path filePath = tempFile.toPath();

        // Загружаем файл в S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                .acl(ObjectCannedACL.PUBLIC_READ) // Делаем файл публичным
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));

        // Удаление временного файла
        Files.deleteIfExists(filePath);

        return "https://aeb07b27-c9f2-4c95-a16b-72df1b5f62f9.selstorage.ru" + "/" + file.getOriginalFilename();
    }

    public boolean doesFileExist(String fileName) {
        try {
            // Создаем запрос на получение метаданных файла
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)  // имя бакета
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
                .bucket(bucketName)
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

    public void deleteFile(String fileName) {
        // Удаление файла из S3
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}
