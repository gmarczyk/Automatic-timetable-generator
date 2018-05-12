package com.scheduler.interfaces.web.scheduleevents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.configuration.MainUIView;
import com.scheduler.shared.scheduling.domain.entity.model.PropertyGenerationFailure;
import com.scheduler.shared.scheduling.domain.collision.model.EntityInconsistency;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ShowInconsistenciesView {

    private MainUIView mv;
    private ScheduleEvent onlyElement;
    private Window window = new Window();

    public ShowInconsistenciesView(final MainUIView mv, final ScheduleEvent onlyElement) {
        this.mv = mv;
        this.onlyElement = onlyElement;

        this.window = new Window("Kolizje");
        this.window.setWidth(60, Sizeable.Unit.PERCENTAGE);

        window.center();
        window.setContent(collisionsContent());
    }

    public ShowInconsistenciesView(final MainUIView mv, final List<EntityInconsistency> inconsistencies) {
        this.mv = mv;
        this.onlyElement = onlyElement;

        this.window = new Window("Niespojnosci");
        this.window.setWidth(60, Sizeable.Unit.PERCENTAGE);

        window.center();
        VerticalLayout layout = new VerticalLayout();
        singleSetPreparation(layout,new HashSet<>(inconsistencies));
        window.setContent(layout);
    }

    public void show() {
        this.mv.addWindow(window);
    }

    private Component collisionsContent() {
        VerticalLayout collisionLayout = new VerticalLayout();

        Set<PropertyGenerationFailure> failures = onlyElement.getFailures();
        for (final PropertyGenerationFailure failure : failures) {
            singleSetPreparation(collisionLayout, failure.getInconsistencies() );
        }

        return collisionLayout;
    }

    private void singleSetPreparation(final VerticalLayout collisionLayout,Set<EntityInconsistency> inconsistencies) {
        for (final EntityInconsistency inconsistency : inconsistencies) {
            VerticalLayout singleCollisionLayout = new VerticalLayout();

            Label affected = new Label("Wlasciwosc generujaca niespojnosc: "
                    + inconsistency.affectedEntityName.getName() + ": " + inconsistency.actualValue.getValue());
            singleCollisionLayout.addComponent(affected);

            for (final ScheduleCondition conditionCause : inconsistency.getDeterminantesWantingOtherValue()) {
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