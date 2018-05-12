package com.scheduler.infrastructure.conditions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.condition.ScheduleConditionRepository;


@Repository
public class ScheduleConditionHibernateRepository implements ScheduleConditionRepository {

    @Autowired
    private GenericTenantAwareHibernateRepository<ScheduleCondition> conditionGenericTenantAwareHibernateRepository;

    @Override
    public List<ScheduleCondition> allConditions() {
        return conditionGenericTenantAwareHibernateRepository.list(ScheduleCondition.class);
    }

    @Override
    public void delete(final ScheduleCondition onlyElement) {
        conditionGenericTenantAwareHibernateRepository.delete(onlyElement);
    }

    @Override
    public void create(final String value, final String value1, final String value2, final String[] split) {
        conditionGenericTenantAwareHibernateRepository.save(new ScheduleCondition(value,value1,value2,split));
    }
}
