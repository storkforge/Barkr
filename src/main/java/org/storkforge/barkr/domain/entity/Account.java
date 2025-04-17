package org.storkforge.barkr.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @PastOrPresent
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "image")
    @Size(max = 5242880)
    private byte[] image;

    @NotBlank
    @Size(min = 2, max = 100, message = "Breed name must be between 2 and 100 characters")
    private String breed;

    @NotBlank
    @Column(name = "google_oidc2id", unique = true, nullable = false, updatable = false)
    private String googleOidc2Id;


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL)
    private GoogleAccountApiKeyLink googleAccountApiKeyLink;


    public GoogleAccountApiKeyLink getGoogleAccountApiKeyLink() {
        return googleAccountApiKeyLink;
    }

    public void setGoogleAccountApiKeyLink(GoogleAccountApiKeyLink googleAccountApiKeyLink) {
        this.googleAccountApiKeyLink = googleAccountApiKeyLink;
    }

    public String getGoogleOidc2Id() {
        return googleOidc2Id;
    }

    public void setGoogleOidc2Id(String googleOidc2Id) {
        this.googleOidc2Id = googleOidc2Id;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getImage() {
      return image == null ? null : image.clone();
    }

    public void setImage(byte[] image) {
      this.image = image == null ? null : image.clone();
    }

    public String getBreed() {
      return breed;
    }

    public void setBreed(String breed) {
      this.breed = breed;
    }

    public void addPost(Post post) {
      posts.add(post);
      post.setAccount(this);
    }

    public void removePost(Post post) {
    posts.remove(post);
  }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Account account = (Account) o;
        return getId() != null && Objects.equals(getId(), account.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }




    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "username = " + username + ", " +
                "createdAt = " + createdAt + ", " +
                "image = " + image + ", " +
                "breed = " + breed + ", " +
                "googleOidc2Id = " + googleOidc2Id + ")";
    }
}
