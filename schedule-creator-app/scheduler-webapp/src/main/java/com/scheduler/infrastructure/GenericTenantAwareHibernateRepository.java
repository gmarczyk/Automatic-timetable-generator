package com.scheduler.infrastructure;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.scheduler.application.LoginService;
import com.scheduler.shared.users.domain.multitenancy.TenantAware;
import com.scheduler.shared.users.domain.multitenancy.TenantId;

@Repository
public class GenericTenantAwareHibernateRepository<T extends TenantAware> implements GenericHibernateRepository<T>{

    @Autowired
    LoginService loginService;

    private SessionFactory sessionFactory;

    public GenericTenantAwareHibernateRepository() {
        sessionFactory = new Configuration().configure()
                .buildSessionFactory();
    }

    public void save(T object) {
        if(object.ownerTenantId() != null) {
            throw new RuntimeException("Object already has its tenant id");
        }

        object.setOwnerTenantId(loginService.getCurrentlyLoggedTenantId());

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(object);
        session.getTransaction().commit();
        session.close();
    }

    public void update(T object) {
        if (!object.ownerTenantId().equals(loginService.getCurrentlyLoggedTenantId())) {
            throw new RuntimeException("Tenant id differs from current owner id");
        }

        if(object.ownerTenantId() == null ) {
            object.setOwnerTenantId(loginService.getCurrentlyLoggedTenantId());
        }

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(object);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(T object) {
        if (!object.ownerTenantId().equals(loginService.getCurrentlyLoggedTenantId())) {
            throw new RuntimeException("Tenant id differs from current owner id");
        }

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(object);
        session.getTransaction().commit();
        session.close();
    }

    public List<T> list (final Class<T> clazz) {
        Criteria criteria = createCriteria(clazz);
        List list = criteria.list();
        commit();
        return (List<T>) list;
    }

    public Criteria createCriteria(final Class<T> clazz) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.beginTransaction();
        final Criteria result = currentSession.createCriteria(clazz);
        TenantId currentlyLoggedTenantId = loginService.getCurrentlyLoggedTenantId();
        if(currentlyLoggedTenantId == null) {
            throw new RuntimeException("No tenant logged in");
        }
        result.add(Restrictions.eq("tenantId", currentlyLoggedTenantId));
        return result;
    }

    public void commit() {
        sessionFactory.getCurrentSession().getTransaction().commit();
    }
}
