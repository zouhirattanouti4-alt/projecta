package com.zouhir.neobank.account;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;
    @Column(name = "user_id")
    private String accountNo;
    @Column(precision = 19, scale = 4)
    private BigDecimal balance;
    private String currency;
    @Version
    private Integer version;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof Account)) return false;
        Account account = (Account) other;
        return Objects.equals(this.id, ((Account) other).getId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
