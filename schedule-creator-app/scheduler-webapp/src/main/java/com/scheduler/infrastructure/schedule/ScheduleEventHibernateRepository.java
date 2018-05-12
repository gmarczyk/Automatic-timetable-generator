package com.scheduler.infrastructure.schedule;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Iterables;
import com.scheduler.domain.schedule.SchoolScheduleEntity;
import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.ScheduleEventRepository;

@Repository
public class ScheduleEventHibernateRepository implements ScheduleEventRepository {

    @Autowired
    private GenericTenantAwareHibernateRepository<ScheduleEvent> tenantAwareHibernateRepository;

    @Override
    public List<ScheduleEvent> allEvents() {
        List<ScheduleEvent> list = tenantAwareHibernateRepository.list(ScheduleEvent.class);

        for (final ScheduleEvent scheduleEvent : list) {
            for (final ScheduleProperty scheduleProperty : scheduleEvent.getScheduleProperties()) {
                SchedulePropertyValue schedulePropertyValue = scheduleProperty.entityValue();
                if(schedulePropertyValue == null)
                    scheduleProperty.updateNonNull();
            }
        }

        return list;
    }

    @Override
    public void delete(final ScheduleEvent onlyElement) {
        tenantAwareHibernateRepository.delete(onlyElement);
    }

    @Override
    public ScheduleEvent create(final SchedulePropertyValue teacherVal, final SchedulePropertyValue gradeVal,
            final SchedulePropertyValue subjectVal, final SchedulePropertyValue roomVal, final String hour,
            final String day) {

        Set<ScheduleProperty> entitySet = new LinkedHashSet<>();
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.SUBJECT.toString(), subjectVal.getValue(),true));
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.GRADE.toString(), gradeVal.getValue()));
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.TEACHER.toString(), teacherVal.getValue()));
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.CLASSROOM.toString(), roomVal.getValue()));

        ScheduleEvent scheduleEvent = new ScheduleEvent(new EventTimeInterval(day, hour), entitySet);
        tenantAwareHibernateRepository.save(scheduleEvent);
        return scheduleEvent;
    }

    @Override
    public void update(final ScheduleEvent ex) {
        tenantAwareHibernateRepository.update(ex);
    }

    @Override
    public ScheduleEvent findById(final long id) {
        Criteria criteria = tenantAwareHibernateRepository.createCriteria(ScheduleEvent.class);
        criteria.add(Restrictions.eq("id",id));
        List list = criteria.list();
        tenantAwareHibernateRepository.commit();
        return Iterables.getOnlyElement((List<ScheduleEvent>)list,null);
    }
}
