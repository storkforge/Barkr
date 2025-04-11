package org.storkforge.barkr.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.CreatePost;
import org.storkforge.barkr.dto.postDto.ResponsePost;

import static org.assertj.core.api.Assertions.assertThat;

class PostMapperTest {
  @Test
  @DisplayName("Can convert entity to response object")
  void canConvertEntityToResponse() {
    Post post = new Post();
    post.setId(1L);
    post.setContent("mockContent");

    assertThat(PostMapper.mapToResponse(post))
            .isInstanceOf(ResponsePost.class)
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("content", "mockContent");
  }

  @Test
  @DisplayName("Returns null for null post")
  void returnsNullForNullPost() {
    assertThat(PostMapper.mapToResponse(null)).isNull();
  }

  @Test
  @DisplayName("Can convert create dto to entity")
  void canConvertCreateDtoToEntity() {
    Account mockAccount = new Account();
    mockAccount.setId(1L);
    CreatePost dto = new CreatePost("mockContent", mockAccount.getId());

    assertThat(PostMapper.mapToEntity(dto, mockAccount))
            .isInstanceOf(Post.class)
            .hasFieldOrPropertyWithValue("content", dto.content());
  }
}
