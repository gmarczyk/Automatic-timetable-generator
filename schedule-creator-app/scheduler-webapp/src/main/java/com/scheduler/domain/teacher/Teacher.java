package com.scheduler.domain.teacher;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.scheduler.domain.schedule.SchoolScheduleEntity;
import com.scheduler.shared.core.structure.AggregateRoot;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.ScheduleEntityRelated;
import com.scheduler.shared.users.domain.multitenancy.TenantAware;
import com.scheduler.shared.users.domain.multitenancy.TenantId;


@AggregateRoot
@Entity
@Table(name = "teachers", uniqueConstraints = @UniqueConstraint(columnNames = "shortcut"))
public class Teacher implements ScheduleEntityRelated, TenantAware {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private TenantId tenantId;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String shortcut;

    @Version
    private Long version;

    public Teacher(String firstName, String lastName, String shortcut) {
        this.shortcut = shortcut;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Teacher() {
        // HIBERNATE
    }


    @Override
    public ScheduleProperty getScheduleEntity() {
        return new ScheduleProperty(SchoolScheduleEntity.TEACHER.toString(), firstName + " " + lastName);
    }

    @Override
    public TenantId ownerTenantId() {
        return tenantId;
    }

    @Override
    public void setOwnerTenantId(TenantId id) {
        this.tenantId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getShortcut() {
        return shortcut;
    }
}
