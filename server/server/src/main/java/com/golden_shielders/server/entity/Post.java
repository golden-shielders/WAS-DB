package com.golden_shielders.server.entity;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class Post {
    Integer id;
    String title;
    String content;
    String authorName;
}
