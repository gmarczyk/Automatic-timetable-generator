package com.scheduler.shared.scheduling.domain.entity.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.scheduler.shared.core.structure.AggregateRoot;
import com.scheduler.shared.scheduling.domain.collision.model.ConditionalCollision;
import com.scheduler.shared.scheduling.domain.collision.model.EntityInconsistency;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@AggregateRoot
@Entity
@Table(name = "generationFailures")
public class PropertyGenerationFailure {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="failedOnId")
    public EventProposal failedOn;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "inconsistenciesIdInFailures")
    public Set<EntityInconsistency> inconsistencies = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collisionIdInFailures")
    public Set<ConditionalCollision> collisions = new HashSet<>();

    @Version
    private Long version;

    public PropertyGenerationFailure() {
        // HIBERNATE
    }


    public PropertyGenerationFailure(final EventProposal failedOn) {
        this.failedOn = failedOn;
        this.inconsistencies = new HashSet<>();
        this.collisions = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public EventProposal getFailedOn() {
        return failedOn;
    }

    public void setFailedOn(final EventProposal failedOn) {
        this.failedOn = failedOn;
    }

    public Set<EntityInconsistency> getInconsistencies() {
        return inconsistencies;
    }

    public void setInconsistencies(final Set<EntityInconsistency> inconsistencies) {
        this.inconsistencies = inconsistencies;
    }



    public Set<ConditionalCollision> getCollisions() {
        return collisions;
    }

    public void setCollisions(final Set<ConditionalCollision> collisions) {
        this.collisions = collisions;
    }




}
