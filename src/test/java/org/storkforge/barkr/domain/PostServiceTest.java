package org.storkforge.barkr.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
            when(postRepository.findAll()).thenReturn(List.of());
            List<ResponsePost> posts = postService.findAll();

            assertThat(posts).isEqualTo(List.of());
        }

        @Test
        @DisplayName("Invalid id or nonexistent id throws error")
        void invalidIdOrNonexistentIdThrowsError() {
            when(postRepository.findById(1L)).thenThrow(new PostNotFound("The post with id: 1 could not be found"));

            assertThatThrownBy(() -> postService.findById(1L)).isInstanceOf(PostNotFound.class).hasMessage("The post with id: 1 could not be found");
        }

        @Test
        @DisplayName("Invalid username or nonexistent username throws error")
        void invalidUsernameOrNonexistentUsernameThrowsError() {
            when(accountRepository.findByUsernameEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.findByUsername("mockAccount")).isInstanceOf(AccountNotFound.class).hasMessage("Account with username: mockAccount not found");
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
            when(postRepository.findAll()).thenReturn(mockPosts);

            List<ResponsePost> result = postService.findAll();

            assertThat(result).isEqualTo(mockPosts
                    .stream()
                    .map(PostMapper::mapToResponse)
                    .toList()
            );
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

           when(accountRepository.findByUsernameEqualsIgnoreCase("mockAccount")).thenReturn(Optional.of(mockAccount));
           when(postRepository.findByAccount(mockAccount)).thenReturn(List.of(mockPost));

           List<ResponsePost> result = postService.findByUsername("mockAccount");

           assertAll(
                   () -> assertThat(result).isEqualTo(Stream.of(mockPost)
                                   .map(PostMapper::mapToResponse)
                                   .toList()
                           ),
                   () -> assertThat(result).isNotEqualTo(Stream.of(mockPost2)
                           .map(PostMapper::mapToResponse)
                           .toList()
                   )
           );
       }
    }

    @Nested
    class xAddTests {
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
            when(accountRepository.findById(1L)).thenThrow(new AccountNotFound("Account with id 1 not found"));

            CreatePost post = new CreatePost("mock content", 1L);

            assertThatThrownBy(() -> postService.addPost(post))
                    .isInstanceOf(AccountNotFound.class)
                    .hasMessage("Account with id 1 not found");
        }
    }
}
