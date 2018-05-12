package com.scheduler.infrastructure.multitenancy;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;
import com.scheduler.infrastructure.GenericUNAWAREHibernateRepository;
import com.scheduler.shared.users.domain.multitenancy.Tenant;
import com.scheduler.shared.users.domain.multitenancy.TenantId;

@Repository
public class TenantRepositoryUNAWARE {

    @Autowired
    GenericUNAWAREHibernateRepository<Tenant> tenantHibernateRepository;


    public void create(final Tenant tenant) {
       tenantHibernateRepository.save(tenant);
    }

    public Tenant findById(TenantId id) {
        Criteria criteria = tenantHibernateRepository.createCriteria(Tenant.class);
        criteria.add(Restrictions.eq("id", id.getValue()));
        List list = criteria.list();
        tenantHibernateRepository.commit();
        return (Tenant) Iterables.getOnlyElement(list, null);
    }

    public Tenant findByName(String name) {
        Criteria criteria = tenantHibernateRepository.createCriteria(Tenant.class);
        criteria.add(Restrictions.eq("tenantName", name));
        List list = criteria.list();
        tenantHibernateRepository.commit();
        return (Tenant) Iterables.getOnlyElement(list, null);
    }

}
