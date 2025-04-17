package org.storkforge.barkr.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "google_account_api_key_link")
public class GoogleAccountApiKeyLink implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "google_oidc2_id", referencedColumnName = "googleOidc2Id", nullable = false, unique = true, updatable = false)
    private Account account;

    @OneToMany(mappedBy = "googleAccountApiKeyLink", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssuedApiKey> issuedApiKeys = new LinkedHashSet<>();

    public Set<IssuedApiKey> getIssuedApiKeys() {
        return issuedApiKeys;
    }

    public void setIssuedApiKeys(Set<IssuedApiKey> issuedApiKeys) {
        this.issuedApiKeys = issuedApiKeys;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        GoogleAccountApiKeyLink that = (GoogleAccountApiKeyLink) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ")";
    }


}
