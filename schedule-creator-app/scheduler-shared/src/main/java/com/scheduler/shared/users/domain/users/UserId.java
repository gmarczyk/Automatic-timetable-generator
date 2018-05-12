package com.scheduler.shared.users.domain.users;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
@Embeddable
public class UserId  {

    @Column(nullable = false, unique = true)
    private String userId;

    public UserId(final String userId) {
        this.userId = userId;
    }

    public UserId() {
        // HIBERNATE
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UserId userId1 = (UserId) o;

        return new EqualsBuilder().append(userId, userId1.userId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(userId).toHashCode();
    }
}
