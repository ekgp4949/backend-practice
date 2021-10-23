package com.backendPractice.web;

import com.backendPractice.domain.posts.Posts;
import com.backendPractice.domain.posts.PostsRepository;
import com.backendPractice.web.dto.PostsResponseDto;
import com.backendPractice.web.dto.PostsSaveRequestDto;
import com.backendPractice.web.dto.PostsUpdateRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jdk.jfr.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void saveTest() throws Exception {
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
        mvc.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        // then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> list = postsRepository.findAll();
        Posts target = list.get(0);
        assertThat(target.getTitle()).isEqualTo(title);
        assertThat(target.getContent()).isEqualTo(content);
        //assertThat(target.getId()).isEqualTo(responseEntity.getBody());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateTest() throws Exception {
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
        //ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Long.class);
        mvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        // then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        Posts target = all.get(0);
        assertThat(target.getTitle()).isEqualTo(expectedTitle);
        assertThat(target.getContent()).isEqualTo(expectedContent);

        // is modifiedDate changed but not createdDate
        assertThat(target.getModifiedDate()).isNotEqualTo(target.getCreatedDate());

    }

    @Test
    @WithMockUser(roles = "USER")
    public void retrieveTest() throws Exception {
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
        //ResponseEntity<PostsResponseDto> responseEntity = restTemplate.getForEntity(url, PostsResponseDto.class, id);
        mvc.perform(
                get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(savedPost)))
                .andExpect(status().isOk());

        // then
//        PostsResponseDto responseDto = responseEntity.getBody();
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assert responseDto != null;
//        assertThat(responseDto.getId()).isEqualTo(id);
//        assertThat(responseDto.getTitle()).isEqualTo(title);
//        assertThat(responseDto.getContent()).isEqualTo(content);
//        assertThat(responseDto.getAuthor()).isEqualTo("author");

        List<Posts> all = postsRepository.findAll();
        Posts target = all.get(0);
        assertThat(savedPost.getId()).isEqualTo(target.getId());
        assertThat(savedPost.getTitle()).isEqualTo(target.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(target.getContent());
        assertThat(savedPost.getAuthor()).isEqualTo(target.getAuthor());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteTest() throws Exception {
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
//        restTemplate.delete(url);
        mvc.perform(delete(url))
                .andExpect(status().isOk());

        // then
        List<Posts> list = postsRepository.findAll();
        assertThat(list.size()).isEqualTo(0);

    }
}
