package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.exception.ImageUploadFailedException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService{
    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        try {
            Map upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "overwrite", true,
                    "resource_type", "image"
            ));
            return (String) upload.get("url");
        } catch (IOException e) {
            throw new ImageUploadFailedException("Failed to upload image to Cloudinary");
        }
    }
}
