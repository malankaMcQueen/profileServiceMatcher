package com.example.matcher.profileservice.service;

import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;

public class ImageProcessor {

    private static final int TARGET_SIZE = 1080;  // Размер, к которому будет приводиться изображение (Y x Y)

    public static File processImageWithThumbnailator(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("processed-", ".jpg");

        Thumbnails.of(file.getInputStream())
                .size(ImageProcessor.TARGET_SIZE, ImageProcessor.TARGET_SIZE)  // Масштабируем изображение
                .crop(Positions.CENTER)
                .outputFormat("jpg")  // Формат JPEG
                .outputQuality(0.8f)  // Устанавливаем качество
                .toFile(tempFile);  // Записываем в файл

        return tempFile;
    }
}

