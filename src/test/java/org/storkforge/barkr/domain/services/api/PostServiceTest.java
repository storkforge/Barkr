package org.storkforge.barkr.domain.services.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.exceptions.PostNotFound;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;

    @Mock
    Post post;

    @Mock
    Account account;

    @InjectMocks
    PostService postService;


    @Nested
    class NoPostsTest{

        @Test
        @DisplayName("No posts records in database throws error")
        void noPostsRecordsInDatabaseThrowsError() {

            when(postRepository.findAll()).thenReturn(List.of()).thenThrow(PostNotFound.class);
            assertThatThrownBy( () -> postService.findAll()).isInstanceOf(PostNotFound.class).hasMessage("No post record(s) found in database");
        }

        @Test
        @DisplayName("Null post throws exception")
        void nullPostThrowsException() {
            when(postRepository.findAll()).thenReturn(null).thenThrow(NullPointerException.class);
            assertThatThrownBy(postService::findAll).isInstanceOf(PostNotFound.class).hasMessage("No post record(s) found in database");

        }

        @Test
        @DisplayName("Invalid id or nonexistent id throws error")
        void invalidIdOrNonexistentIdThrowsError() {
            when(postRepository.findById(eq(1L))).thenThrow(new PostNotFound("The post with id: 1 could not be found"));
            assertThatThrownBy(() -> postService.findOne(1L)).isInstanceOf(PostNotFound.class).hasMessage("The post with id: 1 could not be found");



        }

    }

    @Nested
    class PostsTests{

        @Test
        @DisplayName("findAll return all posts")
        void findAllReturnAllPosts() {

            Account mockAccount = mock(Account.class);
            Account mockAccount2 = mock(Account.class);
            when(mockAccount.getId()).thenReturn(1L);
            when(mockAccount2.getId()).thenReturn(2L);

            Post mockPost =  mock(Post.class);
            Post mockPost2 = mock(Post.class);
            when(mockPost.getId()).thenReturn(1L);
            when(mockPost2.getId()).thenReturn(2L);
            when(mockPost.getAccount()).thenReturn(mockAccount);
            when(mockPost2.getAccount()).thenReturn(mockAccount2);
            when(mockPost.getContent()).thenReturn("voff");
            when(mockPost2.getContent()).thenReturn("woof");

            List<Post> mockPosts = List.of(mockPost, mockPost2);
            when(postRepository.findAll()).thenReturn(mockPosts);

            var result = postService.findAll();
            assertThat(result).hasSize(2);
            assertThat(result).extracting("id").containsExactly(mockPost.getId(), mockPost2.getId());
            assertThat(result).extracting("content").containsExactly(mockPost.getContent(), mockPost2.getContent());

            verify(postRepository, times(1)).findAll();

        }

        @Test
        @DisplayName("findOne returns post if valid id is passed")
        void findOneReturnsPostIfValidIdIsPassed() {

            Account mockAccount = mock(Account.class);

            Post mockPost =  mock(Post.class);
            when(mockPost.getId()).thenReturn(1L);
            when(mockPost.getContent()).thenReturn("voff");

            when(postRepository.findById(eq(1L))).thenReturn(Optional.of(mockPost));

            var result = postService.findOne(1L);

            assertThat(result).extracting("id").isEqualTo(mockPost.getId());
            assertThat(result).extracting("content").isEqualTo(mockPost.getContent());
            verify(postRepository, times(1)).findById(eq(1L));



        }


    }




}
