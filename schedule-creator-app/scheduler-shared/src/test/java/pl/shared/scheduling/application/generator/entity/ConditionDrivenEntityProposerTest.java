package pl.shared.scheduling.application.generator.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.scheduler.shared.scheduling.domain.condition.services.ScheduleConditionExtractor;

import com.scheduler.shared.scheduling.application.generator.ConditionDrivenEntityProposer;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposalBuilder;

import pl.shared.scheduling.SchedulingBuildingHelper;

// TODO update for "in" operator and THENENTITY sets of valuese
public class ConditionDrivenEntityProposerTest {


    private final ConditionDrivenEntityProposer entityProposer = new ConditionDrivenEntityProposer();
    private final ScheduleConditionExtractor conditionExtractor = new ScheduleConditionExtractor();
    private final SchedulingBuildingHelper buildingHelper = new SchedulingBuildingHelper();

    private Set<ScheduleCondition> conditions;

    @Before
    public void initConditions() {
        this.conditions =  new HashSet<ScheduleCondition>() {{
            add(new ScheduleCondition(
                    new ScheduleProperty("TEACHER", "JOHN"),
                    new SchedulePropertyName("ROOM"), new String[] {"305"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("ROOM", "305"),
                    new SchedulePropertyName("GRADE"), new String[]{"3A"}));
        }};
    }


    @Test
    public void findAllConditionValidEventVariations_shouldLaveEmptySetIfNothingWasFound_EqualsExpression() {
        conditions.add(new ScheduleCondition(
                new ScheduleProperty("GRADE", "3A"),
                new SchedulePropertyName("TEACHER"), new String[]{"MATTHEW"}));

        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "JOHN"), // 3. <-- MATTEW by 3A
                new ScheduleProperty("ROOM", null), // 1. <-- 305 by John
                new ScheduleProperty("GRADE", null) // 2. <-- 3A by 305
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        Set<EventProposal> results = new HashSet<>();
        entityProposer.findAllConditionValidEventVariations(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions), results,
                new HashSet<>());

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    public void findAllConditionValidEventVariations_shouldLaveEmptyIfAllValuesGivenAreNull() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", null),
                new ScheduleProperty("ROOM", null),
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        Set<EventProposal> results = new HashSet<>();
        entityProposer.findAllConditionValidEventVariations(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions), results,
                new HashSet<>());

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    public void findAllConditionValidEventVariations_shouldFindOnlyOnePossibleValueIfOnlyEqualsExpressionConditionsAreGiven() {
        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("ROOM", null), // 1. <-- 305 by John  3. 306 by 3A collision
                new ScheduleProperty("GRADE", null) // 2. <-- 3A by 305
        };
        EventProposal event = new EventProposalBuilder().setEntities(scheduleEntities).build();

        Set<EventProposal> results = new HashSet<>();
        entityProposer.findAllConditionValidEventVariations(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions), results,
                new HashSet<>());

        ArrayList<ScheduleProperty> expected = new ArrayList<>(new HashSet<ScheduleProperty>() {{
            add(new ScheduleProperty("TEACHER", "JOHN"));
            add(new ScheduleProperty("ROOM", "305"));
            add(new ScheduleProperty("GRADE", "3A"));
        }});
        Assertions.assertTrue(results.size() == 1);
        Assertions.assertArrayEquals(expected.toArray(), new ArrayList<>(results).get(0).getProperties().toArray());
    }


}