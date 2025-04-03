package org.storkforge.barkr.mapper;

import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.CreatePost;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.dto.postDto.UpdatePost;

public class PostMapper {
    public static ResponsePost mapToResponse(Post post) {
        if (post == null) {
            return null;
        }
        return new ResponsePost(
            post.getId(),
            post.getContent(),
            post.getAccount() != null ? post.getAccount().getId() : null,
            post.getCreatedAt()
            );
}
public static Post mapToEntity(CreatePost dto, Account account) {
        if (dto == null || account == null) {
            return null;
        }
        Post post = new Post();
        post.setContent(dto.content());
        post.setAccount(account);
        return post;
}
public static void mapToUpdateEntity(Post post, UpdatePost dto) {
        if(post == null || dto == null) {
        return ;
        }
        post.setContent(dto.content());
}
}