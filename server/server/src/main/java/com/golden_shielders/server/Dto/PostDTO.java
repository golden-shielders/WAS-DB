package com.golden_shielders.server.Dto;

import com.golden_shielders.server.entity.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    Integer id;
    String title;
    String content;
    String authorName;
    List<FileDTO> attachments;
}
