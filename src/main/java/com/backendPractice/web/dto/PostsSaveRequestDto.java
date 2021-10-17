package com.backendPractice.web.dto;

import com.backendPractice.domain.posts.Posts;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PostsSaveRequestDto {
    private String title;
    private String content;
    private String author;

    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
