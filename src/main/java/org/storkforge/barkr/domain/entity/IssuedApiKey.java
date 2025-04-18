package org.storkforge.barkr.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "issued_api_key")
public class IssuedApiKey implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @NotBlank
    @Column(nullable = false, updatable = false, unique = true)
    private String hashedApiKey;

    @Column(nullable = false, updatable = false)
    @NotNull
    @PastOrPresent
    private LocalDateTime issuedAt;

    @Column(nullable = false, updatable = false)
    @NotNull
    @FutureOrPresent
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false)
    @NotNull
    @PastOrPresent
    private LocalDateTime lastUsedAt;

    @Column(nullable = false)
    @NotBlank
    private String apiKeyName;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "google_account_api_key_link_id")
    private GoogleAccountApiKeyLink googleAccountApiKeyLink;


    public GoogleAccountApiKeyLink getGoogleAccountApiKeyLink() {
        return googleAccountApiKeyLink;
    }

    public void setGoogleAccountApiKeyLink(GoogleAccountApiKeyLink googleAccountApiKeyLink) {
        this.googleAccountApiKeyLink = googleAccountApiKeyLink;
    }

    @PrePersist
    protected void onCreate() {
        if(issuedAt == null) issuedAt = LocalDateTime.now();
        if(expiresAt == null) expiresAt = issuedAt.plusMinutes(5);
        if(lastUsedAt == null) lastUsedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHashedApiKey() {
        return hashedApiKey;
    }

    public void setHashedApiKey(String hashedApiKey) {
        this.hashedApiKey = hashedApiKey;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }

    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        IssuedApiKey that = (IssuedApiKey) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "hashedApiKey = " + hashedApiKey + ", " +
                "issuedAt = " + issuedAt + ", " +
                "expiresAt = " + expiresAt + ", " +
                "revoked = " + revoked + ", " +
                "lastUsedAt = " + lastUsedAt + ", " +
                "apiKeyName = " + apiKeyName + ", " +
                "googleAccountApiKeyLink = " + googleAccountApiKeyLink + ")";
    }
}
