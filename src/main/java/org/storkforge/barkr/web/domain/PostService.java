package org.storkforge.barkr.web.domain;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.CreatePost;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.web.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.web.infrastructure.persistence.PostRepository;
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

  public List<ResponsePost> findAll() {
    log.info("Finding all posts");
    return postRepository
            .findAll()
            .stream()
            .map(PostMapper::mapToResponse)
            .toList();
  }

  public List<ResponsePost> findByUsername(String username) {
    log.info("Finding posts by username {}", username);
    Account account = accountRepository
            .findByUsernameEqualsIgnoreCase(username)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    return postRepository
            .findByAccount(account)
            .stream()
            .map(PostMapper::mapToResponse)
            .toList();
  }

  public void addPost(CreatePost dto) {
    String content = dto.content();
    Account account = accountRepository
            .findById(dto.accountId())
            .orElseThrow(() -> new RuntimeException("Account not found"));

    Post post = new Post();
    post.setContent(content);
    post.setAccount(account);
    postRepository.save(post);
  }
}
