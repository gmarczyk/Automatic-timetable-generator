package com.scheduler.shared.users.domain.users;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;

import org.hibernate.annotations.*;


import com.scheduler.shared.users.domain.multitenancy.Tenant;
import com.scheduler.shared.users.domain.multitenancy.TenantAware;
import com.scheduler.shared.users.domain.multitenancy.TenantId;

@Entity
@Table(name = "shared_users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "username"})})
public class User implements Serializable, TenantAware {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private UserId userId;

    @Embedded
    private TenantId tenantId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Version
    private Long version;

    public User(final UserId userId, final UserRole role, final String username, final String password) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    public User() {
        // HIBERNATE
    }


    public UserId getUserId() {
        return this.userId;
    }

    public void setUserId(final UserId userId) {
        this.userId = userId;
    }

    public void setTenantId(final TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(final UserRole role) {
        this.role = role;
    }

    @Override
    public TenantId ownerTenantId(){
        return this.tenantId;
    }

    @Override
    public void setOwnerTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }
}
