package com.dustincode.ecommerce.user.entity;

import com.dustincode.ecommerce.core.audit.AbstractAuditingEntity;
import com.dustincode.ecommerce.core.utils.DateUtils;
import com.dustincode.ecommerce.user.entity.enumerations.MFAType;
import com.dustincode.ecommerce.user.entity.enumerations.Role;
import com.dustincode.ecommerce.core.security.SecurityUtils;
import com.dustincode.ecommerce.user.entity.enumerations.TokenChannel;
import com.dustincode.ecommerce.user.entity.enumerations.TokenType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

import static com.dustincode.ecommerce.core.security.SecurityUtils.encodePassword;
import static com.dustincode.ecommerce.core.security.SecurityUtils.matchPassword;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_users")
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_type", nullable = false)
    private MFAType mfaType;

    @JsonIgnore
    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserDetail userDetail;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSession> userSessions;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserToken> userTokens;


    @JsonIgnore
    public boolean isEnableMfa() {
        return !MFAType.NONE.equals(mfaType);
    }

    @JsonIgnore
    public boolean validatePassword(String password) {
        return matchPassword(password, getPassword());
    }

    @JsonIgnore
    public boolean validateMFA(String mfaCode) {
        return isEnableMfa() && SecurityUtils.authorizeMFA(getMfaSecret(), mfaCode);
    }

    public User(
            @Nonnull Role role,
            @Nonnull String email,
            @Nonnull String phone,
            @Nonnull String password,
            @Nonnull String name,
            @Nonnull String address
    ) {
        this.email = email;
        this.phone = phone;
        this.password = encodePassword(password);
        this.role = role;
        this.mfaType = MFAType.NONE;
        this.userDetail = UserDetail.builder()
                .user(this)
                .name(name)
                .address(address)
                .build();
    }

    public void updateContactInfo(
            @Nonnull String phone,
            @Nonnull String name,
            @Nonnull String address
    ) {
        setPhone(phone);
        getUserDetail().setName(name);
        getUserDetail().setAddress(address);
    }

    public void addUserSession(
            @Nonnull String accessTokenId,
            @Nonnull String refreshTokenId,
            @Nonnull Instant expireTime
    ) {
        getUserSessions().add(UserSession.builder()
                .user(this)
                .accessTokenId(accessTokenId)
                .refreshTokenId(refreshTokenId)
                .expireTime(expireTime)
                .build());
    }

    public void removeUserSession(@Nonnull String accessTokenId) {
        getUserSessions().remove(UserSession.builder()
                .accessTokenId(accessTokenId)
                .build());
    }

    public boolean updatePassword(String oldPassword, String newPassword) {
        if (!matchPassword(oldPassword, getPassword())) {
            return false;
        }
        setPassword(encodePassword(newPassword));
        removeAllSessions();
        return true;
    }

    public void removeAllSessions() {
        getUserSessions().forEach(session -> session.setUser(null));
        getUserSessions().clear();
    }

    public UserToken createResetPasswordToken() {
        removeOldToken(TokenType.RESET_PASSWORD);
        UserToken newToken = UserToken.builder()
                .user(this)
                .type(TokenType.RESET_PASSWORD)
                .token(SecurityUtils.generateRandomCode())
                .channel(TokenChannel.EMAIL)
                .expireTime(DateUtils.currentInstant().plusSeconds(1800))
                .build();
        getUserTokens().add(newToken);
        return newToken;
    }

    public void removeOldToken(@Nonnull TokenType type) {
        List<UserToken> tokens = getUserTokens()
                .stream()
                .filter(token -> type.equals(token.getType()))
                .toList();
        getUserTokens().removeAll(tokens);
    }

    public void resetPassword(String newPassword) {
        setPassword(encodePassword(newPassword));
        removeAllSessions();
        removeOldToken(TokenType.RESET_PASSWORD);
    }
}
