package org.storkforge.barkr.domain.roles;

public enum BarkrRole {

    USER("ROLE_USER"),
    PREMIUM("ROLE_PREMIUM");



    private final String authorityValue;

    BarkrRole(String roleValue) {
        this.authorityValue = roleValue;
    }

    public String getAuthorityValue() {
        return authorityValue;
    }



}
