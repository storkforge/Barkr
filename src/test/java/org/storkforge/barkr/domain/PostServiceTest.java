package org.storkforge.barkr.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.CreatePost;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.exceptions.AccountNotFound;
import org.storkforge.barkr.exceptions.PostNotFound;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.storkforge.barkr.mapper.PostMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    PostService postService;

    @Nested
    class NoPostsTest {
        @Test
        @DisplayName("No posts records in database returns empty array")
        void noPostsRecordsInDatabaseReturnsEmptyArray() {
            Pageable pageable = PageRequest.of(0, 10);
            when(postRepository.findAll(pageable)).thenReturn(Page.empty());
            Page<ResponsePost> posts = postService.findAll(pageable);

            assertThat(posts).isEqualTo(Page.empty());
        }

        @Test
        @DisplayName("Invalid id or nonexistent id throws error")
        void invalidIdOrNonexistentIdThrowsError() {
            when(postRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.findById(1L)).isInstanceOf(PostNotFound.class).hasMessage("Post with id: 1 not found");
        }

        @Test
        @DisplayName("Invalid username or nonexistent username throws error")
        void invalidUsernameOrNonexistentUsernameThrowsError() {
            when(accountRepository.findByUsernameEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());

            Pageable pageable = PageRequest.of(0, 10);

            assertThatThrownBy(() -> postService.findByUsername("mockAccount", pageable)).isInstanceOf(AccountNotFound.class).hasMessage("Account with username: mockAccount not found");
        }
    }

    @Nested
    class PostsTest {
        @Test
        @DisplayName("findAll return all posts")
        void findAllReturnAllPosts() {
            Account mockAccount = new Account();
            mockAccount.setId(1L);

            Account mockAccount2 = new Account();
            mockAccount2.setId(2L);

            Post mockPost = new Post();
            mockPost.setId(1L);
            mockPost.setAccount(mockAccount);
            mockPost.setContent("voff");

            Post mockPost2 = new Post();
            mockPost2.setId(2L);
            mockPost2.setAccount(mockAccount2);
            mockPost2.setContent("woof");

            List<Post> mockPosts = List.of(mockPost, mockPost2);
            Page<Post> postPage = new PageImpl<>(mockPosts);

            Pageable pageable = PageRequest.of(0, 10);
            when(postRepository.findAll(pageable)).thenReturn(postPage);

            Page<ResponsePost> result = postService.findAll(pageable);

            assertAll(
                    () -> assertThat(result.getContent()).hasSize(2),
                    () -> assertThat(result.getContent())
                        .extracting("id", "content")
                        .containsExactlyInAnyOrder(
                            tuple(1L, "voff"),
                            tuple(2L, "woof")
            ));
        }

        @Test
        @DisplayName("findById returns post if valid id is passed")
        void findByIdReturnsPostIfValidIdIsPassed() {
            Post mockPost = new Post();
            mockPost.setId(1L);
            mockPost.setContent("voff");

            when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

            ResponsePost result = postService.findById(1L);

            assertAll(
                    () -> assertThat(result).extracting("id").isEqualTo(mockPost.getId()),
                    () -> assertThat(result).extracting("content").isEqualTo(mockPost.getContent())
            );
        }

       @Test
       @DisplayName("FindByUsername returns only an accounts posts")
       void findByUsernameReturnsOnlyAnAccountsPosts() {
           Account mockAccount = new Account();
           mockAccount.setId(1L);
           mockAccount.setUsername("mockAccount");

           Account mockAccount2 = new Account();
           mockAccount2.setId(2L);
           mockAccount2.setUsername("mockAccount2");

           Post mockPost = new Post();
           mockPost.setId(1L);
           mockPost.setAccount(mockAccount);
           mockPost.setContent("voff");

           Post mockPost2 = new Post();
           mockPost2.setId(2L);
           mockPost2.setAccount(mockAccount2);
           mockPost2.setContent("woof");

           Pageable pageable = PageRequest.of(0, 10);
           Page<Post> postPage = new PageImpl<>(List.of(mockPost), pageable, 1);

           when(accountRepository.findByUsernameEqualsIgnoreCase("mockAccount")).thenReturn(Optional.of(mockAccount));
           when(postRepository.findByAccount(mockAccount, pageable)).thenReturn(postPage);

           Page<ResponsePost> result = postService.findByUsername("mockAccount", pageable);


           assertAll(
                   () -> assertThat(result.getContent()).hasSize(1),
                   () -> assertThat(result.getContent()).contains(PostMapper.mapToResponse(mockPost)),
                   () -> assertThat(result.getContent()).doesNotContain(PostMapper.mapToResponse(mockPost2))
           );
       }
    }

    @Nested
    class AddTests {
        @Test
        @DisplayName("Can create a post")
        void canCreatePost() {
            when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));

            CreatePost post = new CreatePost("mock content", 1L);

            postService.addPost(post);

            verify(postRepository, times(1)).save(any(Post.class));
        }

        @Test
        @DisplayName("AddPost throws error for invalid account")
        void addPostThrowsErrorForInvalidAccount() {
            when(accountRepository.findById(1L)).thenReturn(Optional.empty());

            CreatePost post = new CreatePost("mock content", 1L);

            assertThatThrownBy(() -> postService.addPost(post))
                    .isInstanceOf(AccountNotFound.class)
                    .hasMessage("Account with id: 1 not found");
        }
    }
}
