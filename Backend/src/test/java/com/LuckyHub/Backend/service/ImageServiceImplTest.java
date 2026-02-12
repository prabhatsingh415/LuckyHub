package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.exception.ImageUploadFailedException;
import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private com.cloudinary.Uploader uploader;

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private MultipartFile multipartFile;

    private final String folder = "profile_pics";

    @BeforeEach
    void setUp() {
        lenient().when(cloudinary.uploader()).thenReturn(uploader);
    }

    // Verifies image upload and URL return
    @Test
    void uploadImage_ShouldReturnUrl_WhenUploadIsSuccessful() throws IOException {
        byte[] fileBytes = "test image data".getBytes();
        String mockUrl = "http://cloudinary.com/luckyhub/image123.jpg";
        Map<String, Object> uploadResult = Map.of("url", mockUrl);

        when(multipartFile.getBytes()).thenReturn(fileBytes);

        when(uploader.upload(eq(fileBytes), anyMap())).thenReturn(uploadResult);

        String result = imageService.uploadImage(multipartFile, folder);

        assertEquals(mockUrl, result);
        verify(uploader).upload(eq(fileBytes), argThat(map ->
                map.get("folder").equals(folder) && (boolean)map.get("overwrite")
        ));
    }

    // Throws custom exception when IOException occurs
    @Test
    void uploadImage_ShouldThrowException_WhenIOExceptionOccurs() throws IOException {
        when(multipartFile.getBytes()).thenThrow(new IOException("File read error"));

        assertThrows(ImageUploadFailedException.class, () ->
                imageService.uploadImage(multipartFile, folder)
        );
    }
}