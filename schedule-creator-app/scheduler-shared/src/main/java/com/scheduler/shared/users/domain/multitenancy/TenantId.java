package com.scheduler.shared.users.domain.multitenancy;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
@Embeddable
public class TenantId {

    @Column(name = "ownerTenantId")
    private long value;

    public TenantId(final long tenantId) {
        this.value = tenantId;
    }

    public TenantId() {
        // HIBERNATE
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TenantId tenantId1 = (TenantId) o;

        return new EqualsBuilder().append(value, tenantId1.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).toHashCode();
    }
}
