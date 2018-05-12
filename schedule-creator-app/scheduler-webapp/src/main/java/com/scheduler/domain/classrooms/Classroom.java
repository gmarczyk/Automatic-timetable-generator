package com.scheduler.domain.classrooms;

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
@Table(name = "classrooms", uniqueConstraints = @UniqueConstraint(columnNames = "classroomCode"))
public class Classroom implements ScheduleEntityRelated, TenantAware {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private TenantId tenantId;

    @Column(nullable = false, unique = true)
    private String classroomCode;

    @Version
    private Long version;

    public Classroom() {
        // HIBERNATE
    }

    public Classroom(String classroomCode) {
        this.classroomCode = classroomCode;
    }

    public String getClassroomCode() {
        return classroomCode;
    }

    @Override
    public ScheduleProperty getScheduleEntity() {
        return new ScheduleProperty(SchoolScheduleEntity.CLASSROOM.toString(), classroomCode);
    }

    @Override
    public TenantId ownerTenantId() {
        return this.tenantId;
    }

    @Override
    public void setOwnerTenantId(final TenantId tenantId) {
        this.tenantId=tenantId;
    }
}
