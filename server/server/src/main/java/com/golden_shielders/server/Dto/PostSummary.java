package com.golden_shielders.server.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostSummary {
    Integer id;
    String title;
    String authorName;
}
