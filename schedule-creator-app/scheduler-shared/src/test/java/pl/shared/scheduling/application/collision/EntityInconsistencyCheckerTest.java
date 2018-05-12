package pl.shared.scheduling.application.collision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.scheduler.shared.scheduling.application.generator.ConflictChecker;
import com.scheduler.shared.scheduling.domain.condition.services.ScheduleConditionExtractor;

import com.scheduler.shared.scheduling.domain.collision.model.ConditionalCollision;
import com.scheduler.shared.scheduling.domain.collision.model.EntityInconsistency;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposalBuilder;

import pl.shared.scheduling.SchedulingBuildingHelper;

public class EntityInconsistencyCheckerTest {

    private final ConflictChecker conflictChecker = new ConflictChecker();
    private final ScheduleConditionExtractor conditionExtractor = new ScheduleConditionExtractor();
    private final SchedulingBuildingHelper bh = new SchedulingBuildingHelper();

    private Set<ScheduleCondition> conditions;

    @Before
    public void initConditions() {
        this.conditions =  new HashSet<ScheduleCondition>() {{
            add(new ScheduleCondition(
                    new ScheduleProperty("TEACHER", "JOHN"),
                    new SchedulePropertyName("ROOM"), new String[]{"305"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("TEACHER", "MATTHEW"),
                    new SchedulePropertyName("GRADE"), new String[]{"3A"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("ROOM", "305"),
                    new SchedulePropertyName("TEACHER"), new String[]{"MATTHEW"})); // collides with teacher john

            add(new ScheduleCondition(
                    new ScheduleProperty("ROOM", "205"),
                    new SchedulePropertyName("GRADE"), new String[]{"5A"})); // collides with teacher matthew

            add(new ScheduleCondition(
                    new ScheduleProperty("SUBJECT", "PHYSICS"),
                    new SchedulePropertyName("ROOM"), new String[]{"111", "222", "333"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("GRADE", "6C"),
                    new SchedulePropertyName("ROOM"), new String[]{"444", "555", "666"}));

            // Multi collisions
            add(new ScheduleCondition(
                    new ScheduleProperty("TEACHER", "KOWALSKY"),
                    new SchedulePropertyName("GRADE"), new String[]{"2D"}));
            add(new ScheduleCondition(
                    new ScheduleProperty("ROOM", "525"),
                    new SchedulePropertyName("GRADE"), new String[]{"3D"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("TEACHER", "KOWALSKY"),
                    new SchedulePropertyName("SUBJECT"), new String[]{"MUSIC"}));
            add(new ScheduleCondition(
                    new ScheduleProperty("ROOM", "525"),
                    new SchedulePropertyName("SUBJECT"), new String[]{"MATHS"}));
        }};
    }

    @Test
    public void findEntityInconsistencyOnEvent_shouldFindCollisionIfGivenEntitiesAreForcingDifferentValues_EqualStatement() throws Exception {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("GRADE", null),
                new ScheduleProperty("ROOM", "305")
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<EntityInconsistency> collisionsOnEvent = conflictChecker.findEntityInconsistencyOnEvent(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));

        List<Set<ScheduleCondition>> inconsistenciesOnEvent = collisionsOnEvent.stream()
                .map(c -> c.determinantesWantingOtherValue)
                .collect(Collectors.toList());

        Assertions.assertTrue(inconsistenciesOnEvent.size() == 1);
        Assertions.assertEquals(
                new HashSet<ScheduleCondition>() {{
                    add(new ScheduleCondition(
                            new ScheduleProperty("ROOM", "305"),
                            new SchedulePropertyName("TEACHER"), new String[]{"MATTHEW"}));
                }},
                inconsistenciesOnEvent.get(0));

    }

    @Test
    public void findEntityInconsistencyOnEvent_shouldReturnEmptyWhenNoCollisionsFound() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "MATTHEW"),
                new ScheduleProperty("GRADE", "3A"),
                new ScheduleProperty("ROOM", null)
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<EntityInconsistency> inconsistenciesOnEvent = conflictChecker.findEntityInconsistencyOnEvent(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));

        Assertions.assertTrue(inconsistenciesOnEvent.isEmpty());
    }

    @Test
    public void findEntityInconsistencyOnEvent_shouldNotTakeIntoAccountNullValuesWhenCheckingForCollisions() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("GRADE", null),
                new ScheduleProperty("ROOM", null) // should be 305 because John wants to
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<EntityInconsistency> inconsistenciesOnEvent = conflictChecker.findEntityInconsistencyOnEvent(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));

        Assertions.assertTrue(inconsistenciesOnEvent.isEmpty());
    }

    // TODO in sets tests

    @Test
    public void findEntityConditionalCollisionsOnEntities_noCollisions_shouldReturnEmpty() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "MATTHEW"), // does Grade 3A
                new ScheduleProperty("GRADE", null),
                new ScheduleProperty("ROOM", null) //  does Grade 5A
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(
                event, conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));
        Assertions.assertTrue(collisions.isEmpty());
    }

    @Test
    public void findEntityConditionalCollisionsOnEntities_oneCollision() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "MATTHEW"), // does Grade 3A
                new ScheduleProperty("GRADE", null),
                new ScheduleProperty("ROOM", "205") //  does Grade 5A
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(
                event, conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));

        Assertions.assertTrue(collisions.size() == 1);
        Assertions.assertEquals(new ConditionalCollision(new SchedulePropertyName("GRADE"),
                new HashSet<ScheduleCondition>() {{
                    add(new ScheduleCondition(
                            new ScheduleProperty("TEACHER", "MATTHEW"),
                            new SchedulePropertyName("GRADE"), new String[]{"3A"}));
                    add(new ScheduleCondition(
                            new ScheduleProperty("ROOM", "205"),
                            new SchedulePropertyName("GRADE"), new String[]{"5A"}));
                }}),
                collisions.get(0));
    }

    @Test
    public void findEntityConditionalCollisionsOnEntities_multipleValuesColliding() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("SUBJECT", "PHYSICS"),
                new ScheduleProperty("GRADE", "6C"),
                new ScheduleProperty("ROOM", null)
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(
                event, conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));

        Assertions.assertTrue(collisions.size() == 1);
        Assertions.assertEquals(new ConditionalCollision(new SchedulePropertyName("ROOM"),
                        new HashSet<ScheduleCondition>() {{
                            add(new ScheduleCondition(
                                    new ScheduleProperty("SUBJECT", "PHYSICS"),
                                    new SchedulePropertyName("ROOM"), new String[]{"111", "222", "333"}));
                            add(new ScheduleCondition(
                                    new ScheduleProperty("GRADE", "6C"),
                                    new SchedulePropertyName("ROOM"), new String[]{"444", "555", "666"}));
                        }}),
                collisions.get(0));
    }

    @Test
    public void findEntityConditionalCollisionsOnEntities_multipleCollisions() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("SUBJECT", null),
                new ScheduleProperty("GRADE", null),
                new ScheduleProperty("ROOM", "525"),
                new ScheduleProperty("TEACHER", "KOWALSKY")
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(
                event, conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));

        Assertions.assertTrue(collisions.size() == 2);
        Assertions.assertTrue(collisions.contains(new ConditionalCollision(new SchedulePropertyName("SUBJECT"),
                new HashSet<ScheduleCondition>() {{
                    add(new ScheduleCondition(
                            new ScheduleProperty("TEACHER", "KOWALSKY"),
                            new SchedulePropertyName("SUBJECT"), new String[]{"MUSIC"}));
                    add(new ScheduleCondition(
                            new ScheduleProperty("ROOM", "525"),
                            new SchedulePropertyName("SUBJECT"), new String[]{"MATHS"}));
                }})));

        Assertions.assertTrue(collisions.contains(new ConditionalCollision(new SchedulePropertyName("GRADE"),
                new HashSet<ScheduleCondition>() {{
                    add(new ScheduleCondition(
                            new ScheduleProperty("TEACHER", "KOWALSKY"),
                            new SchedulePropertyName("GRADE"), new String[]{"2D"}));
                    add(new ScheduleCondition(
                            new ScheduleProperty("ROOM", "525"),
                            new SchedulePropertyName("GRADE"), new String[]{"3D"}));
                }})));
    }

    @Test
    public void findEntityConditionalCollisionsOnEntities_nullValuesGiven() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("SUBJECT", null),
                new ScheduleProperty("GRADE", null),

        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(
                event, conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions));

        Assertions.assertTrue(collisions.isEmpty());
    }

    @Test
    public void findEntityConditionalCollisionsOnEntities_emptyDeterminantesGiven() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("SUBJECT", null),
                new ScheduleProperty("GRADE", null),

        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes = new HashMap<>();
        List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(
                event, determinantes);

        Assertions.assertTrue(collisions.isEmpty());
    }


}