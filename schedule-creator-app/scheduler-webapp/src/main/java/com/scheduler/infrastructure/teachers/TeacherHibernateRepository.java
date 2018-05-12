package com.scheduler.infrastructure.teachers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Iterables;
import com.scheduler.domain.teacher.Teacher;
import com.scheduler.domain.teacher.TeacherRepository;
import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;

@Repository
public class TeacherHibernateRepository implements TeacherRepository {

    @Autowired
    private GenericTenantAwareHibernateRepository<Teacher> tenantAwareRepository;

    @Override
    public void create(Teacher teacher) {
        tenantAwareRepository.save(teacher);
    }

    @Override
    public Teacher findByShortcut(final String shortcut) {
        Criteria criteria = tenantAwareRepository.createCriteria(Teacher.class);
        criteria.add(Restrictions.eq("shortcut",shortcut));
        List list = criteria.list();
        tenantAwareRepository.commit();
        return (Teacher) Iterables.getOnlyElement(list, null);
    }

    @Override
    public List<Teacher> list() {
        return (List<Teacher>) tenantAwareRepository.list(Teacher.class);
    }

    @Override
    public void delete(final Teacher teacher) {
        tenantAwareRepository.delete(teacher);
    }

}
