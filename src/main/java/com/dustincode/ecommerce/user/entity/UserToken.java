package com.dustincode.ecommerce.user.entity;

import com.dustincode.ecommerce.core.audit.AbstractAuditingEntity;
import com.dustincode.ecommerce.user.entity.enumerations.TokenChannel;
import com.dustincode.ecommerce.user.entity.enumerations.TokenType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user_tokens")
public class UserToken extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_channel", nullable = false)
    private TokenChannel channel;

    @Column(name = "token_value", nullable = false)
    private String token;

    @Column(name = "token_expire_time", nullable = false)
    private Instant expireTime;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserToken userToken)) return false;
        return Objects.equals(id, userToken.id)
                || (type == userToken.type && channel == userToken.channel && Objects.equals(token, userToken.token));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, token);
    }
}
