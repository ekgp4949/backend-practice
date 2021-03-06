package com.backendPractice.service.posts;

import com.backendPractice.domain.posts.Posts;
import com.backendPractice.domain.posts.PostsRepository;
import com.backendPractice.web.dto.PostsResponseDto;
import com.backendPractice.web.dto.PostsSaveRequestDto;
import com.backendPractice.web.dto.PostsUpdateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostsService {

    @Autowired
    private final PostsRepository postsRepository;

    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Transactional
    public Long save(PostsSaveRequestDto postsSaveRequestDto) {
        return postsRepository.save(postsSaveRequestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto postsUpdateRequestDto) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id: "+id));
        post.update(postsUpdateRequestDto.getTitle(), postsUpdateRequestDto.getContent());
        return post.getId();
    }

    @Transactional(readOnly = true)
    public PostsResponseDto findById(Long id) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id: "+id));
        return new PostsResponseDto(post);
    }

    public void delete(Long id) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id: "+id));
        postsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PostsResponseDto> findAllDesc() {
        return postsRepository.findAllByOrderByIdDesc()
                .stream().map(PostsResponseDto::new).collect(Collectors.toList());
    }
}
