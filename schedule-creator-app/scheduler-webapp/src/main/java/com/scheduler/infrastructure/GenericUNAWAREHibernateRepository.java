package com.scheduler.infrastructure;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;


@Repository
public class GenericUNAWAREHibernateRepository<T> implements GenericHibernateRepository<T>{

    private SessionFactory sessionFactory;

    public GenericUNAWAREHibernateRepository() {
        sessionFactory = new Configuration().configure()
                .buildSessionFactory();
    }

    public void save(T object) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(object);
        session.getTransaction().commit();
        session.close();
    }

    public void update(T object) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(object);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(T object) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(object);
        session.getTransaction().commit();
        session.close();
    }

    public List<T> list(final Class<T> clazz) {
        return this.sessionFactory.getCurrentSession().createQuery("from " + clazz.getSimpleName()).list();
    }


    public Criteria createCriteria(final Class<T> clazz) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.beginTransaction();
        final Criteria result = currentSession.createCriteria(clazz);
        return result;
    }

    public void commit() {
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

}
