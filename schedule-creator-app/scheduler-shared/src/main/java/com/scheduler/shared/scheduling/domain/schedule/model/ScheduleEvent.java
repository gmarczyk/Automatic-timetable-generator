package com.scheduler.shared.scheduling.domain.schedule.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Iterables;
import com.scheduler.shared.core.structure.AggregateRoot;
import com.scheduler.shared.scheduling.domain.entity.model.PropertyGenerationFailure;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.scheduler.shared.users.domain.multitenancy.TenantAware;
import com.scheduler.shared.users.domain.multitenancy.TenantId;

@AggregateRoot
@Entity
@Table(name = "schedule_events")
public class ScheduleEvent implements TenantAware {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private EventTimeInterval timeInterval;

    @ElementCollection
    @CollectionTable(name = "schedule_events2schedule_entities")
    private Set<ScheduleProperty> scheduleProperties;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationStatus generationStatus;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "proposalId")
    private Set<EventProposal> proposals;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "failureId")
    private Set<PropertyGenerationFailure> failures;

    @Embedded
    private TenantId tenantId;

    @Version
    private Long version;


    public ScheduleEvent() {
        // HIBERNATE
    }

    public ScheduleEvent(final EventTimeInterval timeInterval, final Set<ScheduleProperty> scheduleProperties) {
        this.timeInterval = timeInterval;
        this.scheduleProperties = scheduleProperties;
        this.generationStatus = GenerationStatus.NEW;
    }

    public ScheduleEvent(EventProposal proposal) {
        this.timeInterval = proposal.getTimeInterval();
        this.scheduleProperties = proposal.getProperties();
        this.generationStatus = GenerationStatus.ASSIGNED;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if(generationStatus != null) s.append(generationStatus.name());
        s.append("\n");
        if(timeInterval != null) s.append(timeInterval.day + ", " + timeInterval.hours + "\n");
        scheduleProperties.forEach(en -> {
            s.append(en.toString());
            s.append("\n");
        });

        return s.toString();
    }

    public String getDayStr() {
        if(timeInterval == null || timeInterval.day == null)
            return "";

        return this.timeInterval.day;
    }
    public String getHourStr() {
        if(timeInterval == null || timeInterval.hours == null)
            return "";
        return this.timeInterval.hours;
    }
    public String getStatusStr() {
        if(getGenerationStatus() == null || getGenerationStatus().toString() == null)
            return "";

        return this.getGenerationStatus().toString();
    }

    public String getSubjStr() {
        if(scheduleProperties == null || scheduleProperties.isEmpty())
            return "-";

        ScheduleProperty subj = Iterables.getOnlyElement(scheduleProperties.stream()
                .filter(c -> c.propertyName().getName().equals("Przedmiot"))
                .collect(Collectors.toList()), null);

        if(subj != null && subj.entityValue() != null && subj.entityValue().getValue() != null
                && StringUtils.isNotBlank(subj.entityValue().getValue())) {
            return subj.entityValue().getValue();
        }

        return "-";
    }

    public EventTimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(final EventTimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    public Set<ScheduleProperty> getScheduleProperties() {
        return scheduleProperties;
    }

    public void setScheduleProperties(final Set<ScheduleProperty> scheduleProperties) {
        this.scheduleProperties = scheduleProperties;
    }

    public Set<EventProposal> getProposals() {
        return proposals;
    }

    public void setProposals(final Set<EventProposal> proposals) {
        this.proposals = proposals;
    }

    public GenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(final GenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public Set<PropertyGenerationFailure> getFailures() {
        return failures;
    }

    public void setFailures(final Set<PropertyGenerationFailure> failures) {
        this.failures = new HashSet<>(failures);
    }

    @Override
    public TenantId ownerTenantId() {
        return this.tenantId;
    }

    @Override
    public void setOwnerTenantId(final TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public boolean hasAnyCollisions() {
        if(!failures.isEmpty()) {
            for (final PropertyGenerationFailure fail : failures) {
                if(!fail.getCollisions().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyInconsistencies() {
        if(!failures.isEmpty()) {
            for (final PropertyGenerationFailure fail : failures) {
                if(!fail.getInconsistencies().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public enum GenerationStatus {
        NEW("NOWY"),
        COLLISION("KOLIZJA"),
        NOT_DETERMINABLE("NIEDETERMINOWALNY"),
        ASSIGNED("ZAGNIEZDZONY"),
        UNASSIGNABLE("NIEZAGNIEZDZALNY");

        public String val;

        GenerationStatus(final String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(final TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ScheduleEvent that = (ScheduleEvent) o;

        boolean rest =  new EqualsBuilder().append(timeInterval, that.timeInterval)
                .append(generationStatus, that.generationStatus)
                .append(proposals, that.proposals)
                .append(failures, that.failures)
                .append(tenantId, that.tenantId)
                .append(version, that.version)
                .isEquals();

        HashSet<ScheduleProperty> a = new HashSet<ScheduleProperty>();
        for (final ScheduleProperty refEn : this.scheduleProperties) {
            a.add(new ScheduleProperty(refEn.getSchedulePropertyName().getName(),refEn.getSchedulePropertyValue().getValue()));
        }


        HashSet<ScheduleProperty> b = new HashSet<ScheduleProperty>();
        for (final ScheduleProperty assi : that.scheduleProperties) {
            b.add(new ScheduleProperty(assi.getSchedulePropertyName().getName(),assi.getSchedulePropertyValue().getValue()));
        }

        boolean entitiesBool = a.equals(b);

        return rest & entitiesBool;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(timeInterval)
                .append(generationStatus)
                .append(proposals)
                .append(failures)
                .append(tenantId)
                .append(version)
                .toHashCode();
    }
}
