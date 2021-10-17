package com.backendPractice.web;

import com.backendPractice.service.posts.PostsService;
import com.backendPractice.web.dto.PostsResponseDto;
import com.backendPractice.web.dto.PostsSaveRequestDto;
import com.backendPractice.web.dto.PostsUpdateRequestDto;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostsApiController {

    private final PostsService postsService;

    public PostsApiController(PostsService postsService) {
        this.postsService = postsService;
    }

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto postsSaveRequestDto) {
        return postsService.save(postsSaveRequestDto);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto postsUpdateRequestDto) {
        return postsService.update(id, postsUpdateRequestDto);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto retrieve(@PathVariable Long id) {
        return postsService.findById(id);
    }
}
