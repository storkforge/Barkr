package org.storkforge.barkr.dto.postDto;

public record CreatePost(
        String content,
        Long accountId
) {}
