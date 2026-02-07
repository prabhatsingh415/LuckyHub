package com.LuckyHub.Backend.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetail {
    private String videoId;
    private String thumbnail;
    private String title;
}