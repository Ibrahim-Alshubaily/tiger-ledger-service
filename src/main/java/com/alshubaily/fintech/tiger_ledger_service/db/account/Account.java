package com.alshubaily.fintech.tiger_ledger_service.db.account;

import com.alshubaily.fintech.tiger_ledger_service.db.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.Instant;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @Column(nullable = false, unique = true, precision = 39)
    private BigInteger accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
