package com.scheduler.interfaces.web.scheduleevents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.configuration.MainUIView;
import com.scheduler.shared.scheduling.domain.entity.model.PropertyGenerationFailure;
import com.scheduler.shared.scheduling.domain.collision.model.ConditionalCollision;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ShowCollisionsWindow {

    private MainUIView mv;
    private ScheduleEvent onlyElement;
    private Window window = new Window();

    public ShowCollisionsWindow(final MainUIView mv, final ScheduleEvent onlyElement) {
        this.mv = mv;
        this.onlyElement = onlyElement;

        this.window = new Window("Kolizje");
        this.window.setWidth(60, Sizeable.Unit.PERCENTAGE);

        window.center();
        window.setContent(singleCollisionSet());
    }

    public ShowCollisionsWindow(final MainUIView mv, final List<ConditionalCollision> conditionalCollisionList) {
        this.mv = mv;
        this.onlyElement = onlyElement;

        this.window = new Window("Kolizje");
        this.window.setWidth(60, Sizeable.Unit.PERCENTAGE);

        window.center();

        VerticalLayout layout = new VerticalLayout();
        singleCollisionSet(new HashSet<>(conditionalCollisionList),layout);
        window.setContent(layout);
    }


    public void show() {
        this.mv.addWindow(window);
    }

    private Component singleCollisionSet() {
        VerticalLayout collisionLayout = new VerticalLayout();

        Set<PropertyGenerationFailure> failures = onlyElement.getFailures();

        for (final PropertyGenerationFailure failure : failures) {
            singleCollisionSet(failure.getCollisions(),collisionLayout);
        }

        return collisionLayout;
    }

    private void singleCollisionSet(final Set<ConditionalCollision> conditionalCollisionList, VerticalLayout collisionLayout) {
        for (final ConditionalCollision collision : conditionalCollisionList) {
            VerticalLayout singleCollisionLayout = new VerticalLayout();

            Label affected = new Label("Wlasciwosc kolizyjna: " + collision.affectedEntityName.getName());
            singleCollisionLayout.addComponent(affected);

            for (final ScheduleCondition conditionCause : collision.conditionsDemandingDifferentValues) {
                String demanding = "";

                Iterator<SchedulePropertyValue> iterator = conditionCause.getThen_entity_values().iterator();
                while(iterator.hasNext()) {
                    SchedulePropertyValue next = iterator.next();
                    demanding+=  next.getValue();
                    if(iterator.hasNext()) {
                        demanding += ", ";
                    }
                }

                Label ifCon = new Label("Jezeli " + conditionCause.getIf_entity().propertyName().getName() + ": "
                        + conditionCause.getIf_entity().entityValue().getValue() +
                        " - to " + conditionCause.getThen_entity_name().getName() + " musi byc ktoras z wartosci " +
                        demanding);

                singleCollisionLayout.addComponent(ifCon);
            }
            collisionLayout.addComponent(singleCollisionLayout);
        }
    }
}
