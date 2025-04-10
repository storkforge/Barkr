package org.storkforge.barkr.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.storkforge.barkr.domain.entity.Post;
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
}
