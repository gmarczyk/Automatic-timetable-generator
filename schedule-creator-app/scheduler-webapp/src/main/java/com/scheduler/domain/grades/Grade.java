package com.scheduler.domain.grades;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.scheduler.domain.schedule.SchoolScheduleEntity;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.ScheduleEntityRelated;
import com.scheduler.shared.users.domain.multitenancy.TenantAware;
import com.scheduler.shared.users.domain.multitenancy.TenantId;

@Entity
@Table(name = "grades", uniqueConstraints = @UniqueConstraint(columnNames = "gradeSymbol"))
public class Grade implements ScheduleEntityRelated, TenantAware {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true)
    private String gradeSymbol;

    @Embedded
    private TenantId tenantId;

    @Version
    private Long version;

    public Grade() {
        // HIBERNATE
    }

    public Grade(final String gradeSymbol) {
        this.gradeSymbol = gradeSymbol;
    }

    public String getGradeSymbol() {
        return gradeSymbol;
    }

    @Override
    public ScheduleProperty getScheduleEntity() {
        return new ScheduleProperty(SchoolScheduleEntity.GRADE.toString(),this.gradeSymbol);
    }

    @Override
    public TenantId ownerTenantId() {
        return this.tenantId;
    }

    @Override
    public void setOwnerTenantId(final TenantId tenantId) {
        this.tenantId =tenantId;
    }
}
