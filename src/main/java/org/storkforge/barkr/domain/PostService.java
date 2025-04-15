package org.storkforge.barkr.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.dto.postDto.CreatePost;
import org.storkforge.barkr.exceptions.AccountNotFound;
import org.storkforge.barkr.exceptions.PostNotFound;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.storkforge.barkr.mapper.PostMapper;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final AccountRepository accountRepository;

    public PostService(PostRepository postRepository, AccountRepository accountRepository) {
        this.postRepository = postRepository;
        this.accountRepository = accountRepository;
    }

    @Cacheable("allPosts")
    public Page<ResponsePost> findAll(Pageable pageable) {
        log.info("Finding all posts");
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(PostMapper::mapToResponse);
    }

    @Cacheable("postById")
    public ResponsePost findById(Long id) {
        log.info("Finding post by id: {}", id);

        Post entity = postRepository.findById(id).orElseThrow(() -> new PostNotFound("Post with id: " + id + " not found"));
        return PostMapper.mapToResponse(entity);
    }

    @Cacheable("postByUsername")
    public Page<ResponsePost> findByUsername(String username, Pageable pageable) {
        log.info("Finding posts by username {}", username);

        Account account = accountRepository
                .findByUsernameEqualsIgnoreCase(username)
                .orElseThrow(() -> new AccountNotFound("Account with username: " + username + " not found"));

        log.info("Fetching user's posts");
        Page<Post> posts = postRepository.findByAccount(account, pageable);

        return posts.map(PostMapper::mapToResponse);
    }

    @CacheEvict(value = {"allPosts", "postById", "postByUsername"}, allEntries = true)
    public void addPost(CreatePost dto) {
        log.info("Adding post {}", dto);

        Account account = accountRepository
                .findById(dto.accountId())
                .orElseThrow(() -> new AccountNotFound("Account with id: " + dto.accountId() + " not found"));

        Post entity = PostMapper.mapToEntity(dto, account);
        postRepository.save(entity);
    }
}
