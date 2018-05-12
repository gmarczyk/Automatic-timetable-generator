package com.scheduler.infrastructure;

import java.util.List;

import org.hibernate.Criteria;

public interface GenericHibernateRepository<T> {

    void save(T object);
    void update(T object);
    void delete(T object);
    List<T> list(final Class<T> clazz);
    Criteria createCriteria(final Class<T> clazz);
    void commit();
}
