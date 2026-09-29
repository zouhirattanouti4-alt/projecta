package com.zouhir.neobank.transaction;

import com.zouhir.neobank.account.Account;
import com.zouhir.neobank.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Immutable
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount; // we use Account and not User, a user may have lots of accs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false, unique = true)
    private String reference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Builder
    public Transaction(UUID id, Account fromAccount, Account toAccount, BigDecimal amount, Type type, Status status, String reference, Instant createdAt){
        if(amount==null || amount.compareTo(BigDecimal.ZERO) <= 0) // Satisfying the Check (amount > 0) constraint
            throw new IllegalArgumentException("Error : The amount should be positive");
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.reference = reference;
        this.createdAt = (createdAt!=null) ? createdAt : Instant.now();
    }


    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof Transaction transaction)) return false;
        return Objects.equals(this.getReference(),transaction.getReference());
    }

    @Override
    public int hashCode(){
        return Objects.hash(getReference());
    }
}
