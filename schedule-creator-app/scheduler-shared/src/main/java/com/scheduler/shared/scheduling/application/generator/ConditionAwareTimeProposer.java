package com.scheduler.shared.scheduling.application.generator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.ApplicationService;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.Schedule;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@ApplicationService
@Component
public class ConditionAwareTimeProposer {

    ConflictChecker conflictChecker = new ConflictChecker();

    public Set<EventTimeInterval> tryFindingValidIntervals(final Schedule schedule,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes, final EventProposal minInProposals) {

        EventTimeInterval rememberTime = new EventTimeInterval(minInProposals.getTimeInterval());
        EventProposal copy = new EventProposal(minInProposals);

        Set<EventTimeInterval> validIntervals = new HashSet<>();
        for (final EventTimeInterval timeInterval : schedule.getTimeIntervals()) {
            if(!isProposedTimeValidWithCurrentOne(timeInterval, rememberTime)) {
                continue;
            }

            copy.setTimeInterval(timeInterval);
            if(!conflictChecker.hasAnyConflicts(copy,determinantes)) {
                validIntervals.add(timeInterval);
            }
        }

        return validIntervals;
    }

    private boolean isProposedTimeValidWithCurrentOne(final EventTimeInterval proposed, final EventTimeInterval actual) {
        if(actual == null || (StringUtils.isBlank(actual.hours) && StringUtils.isBlank(actual.day))) {
            return true;
        }

        return !((actual.hours != null && !actual.hours.equals(proposed.hours))
                || (actual.day != null && !actual.day.equals(proposed.day)));

    }
}
