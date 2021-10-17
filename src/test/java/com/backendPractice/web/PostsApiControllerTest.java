package com.backendPractice.web;

import com.backendPractice.domain.posts.Posts;
import com.backendPractice.domain.posts.PostsRepository;
import com.backendPractice.web.dto.PostsResponseDto;
import com.backendPractice.web.dto.PostsSaveRequestDto;
import com.backendPractice.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() {
        postsRepository.deleteAll();
    }

    @Test
    public void saveTest() {
        // given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto dto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();
        String url = "http://localhost:"+port+"/api/v1/posts";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, dto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> list = postsRepository.findAll();
        Posts target = list.get(0);
        assertThat(target.getTitle()).isEqualTo(title);
        assertThat(target.getContent()).isEqualTo(content);
        assertThat(target.getId()).isEqualTo(responseEntity.getBody());
    }

    @Test
    public void updateTest() {
        // given
        String title = "title";
        String content = "content";
        Posts savedPost = postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("author")
                .build());
        Long updatedId = savedPost.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto dto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:"+port+"/api/v1/posts/"+updatedId;
        HttpEntity<PostsUpdateRequestDto> httpEntity = new HttpEntity<>(dto);

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        Posts target = all.get(0);
        assertThat(target.getTitle()).isEqualTo(expectedTitle);
        assertThat(target.getContent()).isEqualTo(expectedContent);

        // is modifiedDate changed but not createdDate
        assertThat(target.getModifiedDate()).isNotEqualTo(target.getCreatedDate());

    }

    @Test
    public void retrieveTest() {
        // given
        String title = "title";
        String content = "content";
        Posts savedPost = postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("author")
                .build());
        Long id = savedPost.getId();

        String url = "http://localhost:"+port+"/api/v1/posts/"+id;

        // when
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.getForEntity(url, PostsResponseDto.class, id);

        // then
        PostsResponseDto responseDto = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert responseDto != null;
        assertThat(responseDto.getId()).isEqualTo(id);
        assertThat(responseDto.getTitle()).isEqualTo(title);
        assertThat(responseDto.getContent()).isEqualTo(content);
        assertThat(responseDto.getAuthor()).isEqualTo("author");

        List<Posts> all = postsRepository.findAll();
        Posts target = all.get(0);
        assertThat(responseDto.getId()).isEqualTo(target.getId());
        assertThat(responseDto.getTitle()).isEqualTo(target.getTitle());
        assertThat(responseDto.getContent()).isEqualTo(target.getContent());
        assertThat(responseDto.getAuthor()).isEqualTo(target.getAuthor());
    }

    @Test
    public void deleteTest() {
        // given
        String title = "title";
        String content = "content";
        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("author")
                .build());

        Posts post = postsRepository.findAll().get(0);
        Long id = post.getId();
        String url = "http://localhost:"+port+"/api/v1/posts/"+id;

        // when
        restTemplate.delete(url);

        // then
        List<Posts> list = postsRepository.findAll();
        assertThat(list.size()).isEqualTo(0);

    }
}
