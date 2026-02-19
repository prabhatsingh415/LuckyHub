package com.LuckyHub.Backend.model;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String videoId;
    private String thumbnail;
    private String title;
}