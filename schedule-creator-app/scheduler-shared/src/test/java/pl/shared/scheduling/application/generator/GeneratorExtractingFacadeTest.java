package pl.shared.scheduling.application.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.scheduler.shared.scheduling.domain.condition.services.ScheduleConditionExtractor;
import com.scheduler.shared.scheduling.domain.GeneratorExtractingFacade;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposalBuilder;

//TODO here when switching to THEN_ENTITY set
public class GeneratorExtractingFacadeTest {

    private final GeneratorExtractingFacade extractingFacade = new GeneratorExtractingFacade();
    private final ScheduleConditionExtractor conditionExtractor = new ScheduleConditionExtractor();

    private Set<ScheduleCondition> conditions;
    private EventProposal event;

    @Before
    public void initConditions() {
        this.conditions =  new HashSet<ScheduleCondition>() {{
            add(new ScheduleCondition(
                    new ScheduleProperty("TEACHER", "JOHN"),
                    new SchedulePropertyName("ROOM"), new String[]{"305"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("ROOM", "305"),
                    new SchedulePropertyName("GRADE"), new String[]{"3A"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("ROOM", "305"),
                    new SchedulePropertyName("TEACHER"), new String[]{"MATTHEW"}));

            add(new ScheduleCondition(
                    new ScheduleProperty("GRADE", "3A"),
                    new SchedulePropertyName("TEACHER"), new String[]{"JOHN"}));
        }};

        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("SUBJECT", null),
                new ScheduleProperty("GRADE", "3A"),
                new ScheduleProperty("ROOM", null)
        };
        this.event = new EventProposalBuilder().setEntities(scheduleEntities).build();
    }

    @Test
    public void prepareInCommonPropositionsForEntity_shouldReturnEmptySetIfNothingDeterminesGivenEntity() {
        // Empty set
        Assert.assertEquals(new HashSet<>() ,extractingFacade.prepareInCommonPropositionsForPropertyInEvent(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions),
                new ScheduleProperty("SUBJECT", null)));
    }

    @Test
    public void prepareInCommonPropositionsForEntity_shouldReturnInCommonSetOfPropositionsBasingOnInfluencingConditionsAndAlreadySetEntities() {
        conditions.add(new ScheduleCondition(
                        new ScheduleProperty("GRADE", "3A"),
                new SchedulePropertyName("ROOM"), new String[]{"305"}));

        Assert.assertEquals(new HashSet<>(Arrays.asList(new SchedulePropertyValue("305")))
                , extractingFacade.prepareInCommonPropositionsForPropertyInEvent(event,
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions),
                new ScheduleProperty("ROOM", null)));
    }

    @Test
    public void prepareInCommonPropositionsForEntity_shouldReturnNullIfThereAreInfluencingConditionsButNoCommonValueIsPossible() {
        conditions.add(new ScheduleCondition(
                        new ScheduleProperty("GRADE", "3A"),
                new SchedulePropertyName("ROOM"), new String[]{"306"}));

        Assert.assertEquals(null, extractingFacade.prepareInCommonPropositionsForPropertyInEvent(event,
                        conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions),
                        new ScheduleProperty("ROOM", null)));
    }


}