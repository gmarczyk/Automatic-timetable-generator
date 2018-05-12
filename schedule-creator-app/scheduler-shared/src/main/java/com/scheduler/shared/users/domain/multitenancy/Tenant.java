package com.scheduler.shared.users.domain.multitenancy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

@Entity
@Table(name = "shared_tenants",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"tenantName"})})
public class Tenant {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true)
    private String tenantName;

    @Version
    private Long version;

    public Tenant() {
        // HIBERNATE
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(final String tenantName) {
        this.tenantName = tenantName;
    }




}
