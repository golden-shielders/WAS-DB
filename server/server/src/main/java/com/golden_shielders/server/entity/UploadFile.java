package com.golden_shielders.server.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFile {
    private Integer id;
    private Integer postId;
    private String originalName;
    private String storedName;
    private String filePath;
}