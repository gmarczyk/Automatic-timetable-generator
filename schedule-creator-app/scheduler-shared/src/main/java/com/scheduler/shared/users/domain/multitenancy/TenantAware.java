package com.scheduler.shared.users.domain.multitenancy;


public interface TenantAware {

    TenantId ownerTenantId();

    /**
     *  SHOULD BE USED ONLY BY HIBERNATE REPOSITORY
     */
    void setOwnerTenantId(TenantId tenantId);

}


