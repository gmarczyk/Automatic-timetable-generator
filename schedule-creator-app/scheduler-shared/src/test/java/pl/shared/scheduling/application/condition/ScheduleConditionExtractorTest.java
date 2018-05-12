package pl.shared.scheduling.application.condition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.scheduler.shared.scheduling.domain.condition.services.ScheduleConditionExtractor;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;

import pl.shared.scheduling.SchedulingBuildingHelper;

public class ScheduleConditionExtractorTest {

    private final ScheduleConditionExtractor conditionExtractor = new ScheduleConditionExtractor();
    private final SchedulingBuildingHelper buildingHelper = new SchedulingBuildingHelper();

    private Set<ScheduleCondition> conditions;
    private ScheduleCondition teacherJohnRoom305;
    private ScheduleCondition room305Grade3A;
    private ScheduleCondition room305TeacherMatthew;
    private ScheduleCondition grade3ATeacherJohn;

    @Before
    public void initConditions() {
        this.conditions = new HashSet<ScheduleCondition>() {{
            add(teacherJohnRoom305 = new ScheduleCondition(new ScheduleProperty("TEACHER", "JOHN"),
                    new SchedulePropertyName("ROOM"), new String[]{"305"}));

            add(room305Grade3A = new ScheduleCondition(new ScheduleProperty("ROOM", "305"),
                    new SchedulePropertyName("GRADE"), new String[]{"3A"}));

            add(room305TeacherMatthew = new ScheduleCondition(new ScheduleProperty("ROOM", "305"),
                    new SchedulePropertyName("TEACHER"), new String[]{"MATTHEW"}));

            add(grade3ATeacherJohn = new ScheduleCondition(new ScheduleProperty("GRADE", "3A"),
                    new SchedulePropertyName("TEACHER"), new String[]{"JOHN"}));
        }};
    }


    @Test
    public void mapConditionsPerEachInfluencedEntityName_shouldReturnMappedConditionsByInfluencedEntityNamesIfAnyValidArePresent() {
        Map<SchedulePropertyName, Set<ScheduleCondition>> mappedConditions = conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(
                conditions);

        Assertions.assertEquals(buildingHelper.namesSet(new String[] { "TEACHER", "ROOM", "GRADE" }),
                mappedConditions.keySet());

        Assertions.assertEquals(new HashSet<ScheduleCondition>(Arrays.asList(room305TeacherMatthew,grade3ATeacherJohn))
                ,mappedConditions.get(new SchedulePropertyName("TEACHER")));

        Assertions.assertEquals(new HashSet<ScheduleCondition>(Arrays.asList(room305Grade3A))
                ,mappedConditions.get(new SchedulePropertyName("GRADE")));

        Assertions.assertEquals(new HashSet<ScheduleCondition>(Arrays.asList(teacherJohnRoom305))
                ,mappedConditions.get(new SchedulePropertyName("ROOM")));
    }

    @Test
    public void mapConditionsPerGivenDeterminantNames_shouldReturnMappedConditionsByDeterminantNamesIfAnyArePresent() {
        Map<SchedulePropertyName, Set<ScheduleCondition>> mappedConditions = conditionExtractor.mapConditionsPerGivenINFLUENCINGEntities(
                conditions, buildingHelper.entitiesSet(new HashMap<String,String>(){{
                    put("ROOM", "305");
                    put("GRADE", "3A");
                }}));

        Assertions.assertEquals(buildingHelper.namesSet(new String[] { "ROOM", "GRADE" }),
                mappedConditions.keySet());

        Assertions.assertEquals(new HashSet<ScheduleCondition>(Arrays.asList(room305Grade3A,room305TeacherMatthew))
                ,mappedConditions.get(new SchedulePropertyName("ROOM")));
        Assertions.assertEquals(new HashSet<ScheduleCondition>(Arrays.asList(grade3ATeacherJohn))
                ,mappedConditions.get(new SchedulePropertyName("GRADE")));
    }

    @Test
    public void mapConditionsPerGivenDeterminantNames_shouldNotReturnConditionForDeterminantesWithOtherValuesThanGiven() {
        ScheduleCondition withOtherValue = new ScheduleCondition(new ScheduleProperty("ROOM", "12345678"),
                new SchedulePropertyName("X"), new String[]{"Y"});
        conditions.add(withOtherValue);

        Map<SchedulePropertyName, Set<ScheduleCondition>> mappedConditions = conditionExtractor.mapConditionsPerGivenINFLUENCINGEntities(
                conditions, buildingHelper.entitiesSet(new HashMap<String,String>(){{
                    put("ROOM", "305");
                    put("GRADE", "3A");
                }}));

        Assertions.assertFalse(mappedConditions.get(new SchedulePropertyName("ROOM")).contains(withOtherValue));
    }

}