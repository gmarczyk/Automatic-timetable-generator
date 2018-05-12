package com.scheduler.infrastructure.users;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;

import com.scheduler.infrastructure.GenericUNAWAREHibernateRepository;
import com.scheduler.infrastructure.multitenancy.TenantRepositoryUNAWARE;
import com.scheduler.shared.users.domain.multitenancy.Tenant;
import com.scheduler.shared.users.domain.users.User;

@Repository
public class UserRepositoryUNAWARE {

    @Autowired
    GenericUNAWAREHibernateRepository<User> userHibernateRepository;
    @Autowired
    TenantRepositoryUNAWARE tenantHibernateRepository;


    public User findByName(String username) {
        Criteria criteria = userHibernateRepository.createCriteria(User.class);
        criteria.add(Restrictions.eq("username", username));
        List<User> list = criteria.list();
        userHibernateRepository.commit();
        return Iterables.getOnlyElement(list, null);
    }

    public void createByInternalPanel(final User user) {
        Tenant byId = tenantHibernateRepository.findById(user.ownerTenantId());
        if(byId == null) {
            throw new RuntimeException("Cannot createByInternalPanel user for no tenant");
        }

        userHibernateRepository.save(user);
    }
}
