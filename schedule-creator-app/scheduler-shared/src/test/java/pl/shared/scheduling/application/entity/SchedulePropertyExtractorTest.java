package pl.shared.scheduling.application.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.entity.services.ScheduleEntityExtractor;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposalBuilder;

import pl.shared.scheduling.SchedulingBuildingHelper;

public class SchedulePropertyExtractorTest {

    private final ScheduleEntityExtractor extractor = new ScheduleEntityExtractor();
    private final SchedulingBuildingHelper valueBuilder = new SchedulingBuildingHelper();

    @Test
    public void UNSETEntitiesSizeIsCorrectAndReturnedEntitiesAreMatching() {
        ScheduleProperty[] entities = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("SUBJECT", null),
                new ScheduleProperty("GRADE", "3A"),
                new ScheduleProperty("ROOM", null),
        };
        EventProposal event = new EventProposalBuilder().setEntities(entities).build();

        Set<ScheduleProperty> unsetEntities = extractor.getUnsetEntitiesOf(event);
        Assertions.assertEquals(2, unsetEntities.size());
        Assertions.assertEquals(true, unsetEntities.contains(new ScheduleProperty("SUBJECT", null)));
        Assertions.assertEquals(true, unsetEntities.contains(new ScheduleProperty("ROOM", null)));
    }

    @Test
    public void setEntitiesSizeIsCorrectAndReturnedEntitiesAreMatching() {
        ScheduleProperty[] entities = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("SUBJECT", null),
                new ScheduleProperty("GRADE", "3A"),
                new ScheduleProperty("ROOM", null),
        };
        EventProposal event = new EventProposalBuilder().setEntities(entities).build();

        Set<ScheduleProperty> setEntities = extractor.getSetEntitiesForEvent(event.getProperties());
        Assertions.assertEquals(2, setEntities.size());
        Assertions.assertEquals(true, setEntities.contains(new ScheduleProperty("TEACHER", "JOHN")));
        Assertions.assertEquals(true, setEntities.contains(new ScheduleProperty("GRADE", "3A")));
    }

    @Test
    public void nullEntitiesPassedToSetAndUnsetExtractingMethod_shouldNotThrowException() {
        EventProposal event = new EventProposalBuilder().build();
        extractor.getSetEntitiesForEvent(event.getProperties());
        extractor.getUnsetEntitiesOf(event);
    }

    @Test
    public void getInCommonElementsFrom_shouldReturnAllInCommonElementsForAllSetsIfSuchArePresent() {
        Set<Set<SchedulePropertyValue>> allSets = new HashSet<Set<SchedulePropertyValue>>() {{
            add(valueBuilder.valuesSet(new String[] { "1", "2", "3" }));
            add(valueBuilder.valuesSet(new String[] { "4", "1" }));
            add(valueBuilder.valuesSet(new String[] { "5", "6", "1", "4" }));
        }};

        Set<SchedulePropertyValue> inCommonValues = extractor.getInCommonElementsFrom(allSets);
        Set<SchedulePropertyValue> expected = new HashSet<SchedulePropertyValue>(Arrays.asList(new SchedulePropertyValue("1")));
        Assertions.assertEquals(expected,inCommonValues);
    }

    @Test
    public void getInCommonElementsFrom_shouldReturnNullIfNoCommonElementsForAllSets() {
        Set<Set<SchedulePropertyValue>> allSets = new HashSet<Set<SchedulePropertyValue>>() {{
            add(valueBuilder.valuesSet(new String[] { "1", "2", "3" }));
            add(valueBuilder.valuesSet(new String[] { "4", "5" }));
            add(valueBuilder.valuesSet(new String[] { "6", "7", "8", "1" }));
        }};

        Set<SchedulePropertyValue> inCommonValues = extractor.getInCommonElementsFrom(allSets);
        Assertions.assertEquals(null,inCommonValues);
    }

    @Test
    public void getInCommonElementsFrom_shouldReturnEmptySetIfNoValuesPassed() {
        Set<Set<SchedulePropertyValue>> allSets = new HashSet<Set<SchedulePropertyValue>>() {{
            // empty
        }};

        Set<SchedulePropertyValue> inCommonValues = extractor.getInCommonElementsFrom(allSets);
        Assertions.assertEquals(new HashSet<>(),inCommonValues);
    }

    @Test
    public void getInCommonElementsFrom_shouldReturnGivenSetIfOnlyOneSetWasGiven() {
        Set<SchedulePropertyValue> singleSet = valueBuilder.valuesSet(new String[] { "6", "7", "8", "1" });
        Set<Set<SchedulePropertyValue>> allSets = new HashSet<Set<SchedulePropertyValue>>() {{
            add(singleSet);
        }};

        Set<SchedulePropertyValue> inCommonValues = extractor.getInCommonElementsFrom(allSets);
        Assertions.assertEquals(singleSet,inCommonValues);
    }

}