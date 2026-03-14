package com.bawal.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {
    private Long id;
    private String imageUrl;
    private String imageType;
}
