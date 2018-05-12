package com.scheduler.interfaces.web.scheduleevents;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.google.common.collect.Iterables;
import com.scheduler.application.LoginService;


import com.scheduler.presentation.framework.AddEditDeleteContent;
import com.scheduler.presentation.framework.GenericGridViewWithBasicManagement;
import com.scheduler.presentation.framework.RefreshableLayout;
import com.scheduler.shared.scheduling.application.generator.ScheduleGenerator;
import com.scheduler.shared.scheduling.domain.condition.ScheduleConditionRepository;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.Schedule;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.ScheduleEventRepository;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;

@Configurable(preConstruction = true)
public class ScheduleEventsView implements RefreshableLayout, Serializable {

    @Autowired
    private ScheduleEventRepository scheduleEventRepository;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ScheduleGenerator scheduleGenerator;
    @Autowired
    private ScheduleConditionRepository scheduleConditionRepository;

    private HorizontalLayout layout;
    private Panel eventsPanel;
    private Panel valuesPanel;

    private final MainUIView mv;
    private GenericGridViewWithBasicManagement<ScheduleEvent> gridView;
    private Grid<ScheduleProperty> multipleValuesGrid;

    private Button inconsistenciesBtn;
    private Button collisionsBtn;


    public ScheduleEventsView(MainUIView mv) {
        this.mv = mv;

        setupGridView();
        setupPanel();
        mv.setMainContent(layout);
    }

    private void setupPanel() {
        layout = new HorizontalLayout();
        layout.setSizeFull();

        VerticalLayout eventsVlay =new VerticalLayout();
        eventsVlay.setSizeFull();

        eventsPanel = new Panel("Warunki");
        eventsPanel.setSizeFull();

        HorizontalLayout eventsGridViewLAyout = this.gridView.getLayout();
        VerticalLayout gridLayout = this.gridView.getGridLayout();
        AddEditDeleteContent addEditDeleteContent = this.gridView.getAddEditDeleteContent();

        eventsGridViewLAyout.setExpandRatio(gridLayout,7.0f);
        eventsGridViewLAyout.setExpandRatio(addEditDeleteContent.getContent(),3.0f);
        eventsPanel.setContent(eventsGridViewLAyout);
        eventsVlay.addComponent(eventsPanel);

        VerticalLayout valuesPanelLayout = new VerticalLayout();
        valuesPanelLayout.setSizeFull();

        Panel panel = new Panel("Wlasciwosci zdarzenia");
        panel.setSizeFull();
        multipleValuesGrid = new Grid<ScheduleProperty>();
        multipleValuesGrid.setSizeFull();
        multipleValuesGrid.addColumn(ScheduleProperty::propertyName).setCaption("Nazwa");
        multipleValuesGrid.addColumn(ScheduleProperty::entityValue).setCaption("Wartosc");
        panel.setContent(multipleValuesGrid);
        valuesPanelLayout.addComponent(panel);

        layout.addComponent(eventsVlay);
        layout.addComponent(valuesPanelLayout);
        layout.setExpandRatio(eventsVlay,6.5f);
        layout.setExpandRatio(valuesPanelLayout,3.5f);

        collisionsBtn = prepareShowCollisionsWindowButton(); collisionsBtn.setEnabled(false);
        inconsistenciesBtn = prepareShowInconsistenciesButton(); collisionsBtn.setEnabled(false);

        this.gridView.getGrid().addItemClickListener(new ItemClickListener<ScheduleEvent>() {
            @Override
            public void itemClick(final Grid.ItemClick<ScheduleEvent> itemClick) {
                Set<ScheduleProperty> scheduleEntities = new HashSet<>(itemClick.getItem().getScheduleProperties());
                for (final ScheduleProperty entity : scheduleEntities) {
                    if(entity.entityValue() == null || entity.entityValue().getValue() == null) {
                        entity.setSchedulePropertyValue(new SchedulePropertyValue(""));
                    }
                }
                multipleValuesGrid.setItems(scheduleEntities);

                collisionsBtn.setEnabled(itemClick.getItem().hasAnyCollisions());
                inconsistenciesBtn.setEnabled(itemClick.getItem().hasAnyInconsistencies());
            }
        });
    }


    private void setupGridView() {

        this.gridView = new GenericGridViewWithBasicManagement<ScheduleEvent>(scheduleEventRepository.allEvents(), mv) {
            @Override
            public void configureGrid() {
                this.grid.addColumn(ScheduleEvent::getDayStr).setCaption("Dzien");
                this.grid.addColumn(ScheduleEvent::getHourStr).setCaption("Godzina");
                this.grid.addColumn(ScheduleEvent::getSubjStr).setCaption("Przedmiot");
                this.grid.addColumn(ScheduleEvent::getStatusStr).setCaption("Status generacji");

                if (!loginService.isAnyOfRoles(UserRole.TENANT_ADMIN, UserRole.MANAGEMENT)) {
                    this.addEditDeleteContent.setEnabled(false);
                }

            }

            @Override
            public void configureManagementButtons() {
                this.addEditDeleteContent.getLayout().addComponent(prepareDeleteAllButton());
                this.addEditDeleteContent.getLayout().addComponent(prepareRefreshButton());
                Button button = prepareCreateScheduleButton();
                if(scheduleEventRepository.allEvents().stream().map(c->c.getGenerationStatus()).collect(Collectors.toList()).contains(
                        ScheduleEvent.GenerationStatus.ASSIGNED)) {
                    button.setEnabled(false);
                }

                this.addEditDeleteContent.getLayout().addComponent(button);
                this.addEditDeleteContent.getLayout().addComponent(prepareShowCollisionsWindowButton());
                this.addEditDeleteContent.getLayout().addComponent(prepareShowInconsistenciesButton());
                this.addEditDeleteContent.getLayout().setSizeFull();

                this.addEditDeleteContent.getAddButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        AddScheduleEventWindow addScheduleEventWindow = new AddScheduleEventWindow(mv);
                        addScheduleEventWindow.show();
                    }
                });

                this.addEditDeleteContent.getLayout().removeComponent(this.addEditDeleteContent.getEditButton());

                this.addEditDeleteContent.getDeleteButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        Set<ScheduleEvent> selectedItems = grid.getSelectedItems();
                        if(selectedItems.size() != 1) {
                            return;
                        }

                        scheduleEventRepository.delete(Iterables.getOnlyElement(selectedItems));
                    }
                });
            }
        };

        this.gridView.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    private Component prepareDeleteAllButton() {
        final Button delall =new Button("Usun wszystkie zdarzenia");
        if (!loginService.isAnyOfRoles(UserRole.TENANT_ADMIN, UserRole.MANAGEMENT)) {
            delall.setEnabled(false);
        }
        delall.setSizeFull();
        delall.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                scheduleEventRepository.allEvents().forEach(ev -> scheduleEventRepository.delete(ev));
            }
        });

        return delall;
    }

    private Button prepareShowInconsistenciesButton() {
        final Button incon = new Button("Pokaz niespojnosci");
        incon.setSizeFull();
        incon.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                if(!gridView.getGrid().getSelectedItems().isEmpty()) {
                    ScheduleEvent onlyElement = Iterables.getOnlyElement(gridView.getGrid().getSelectedItems());
                    if(onlyElement.hasAnyInconsistencies()) {
                        new ShowInconsistenciesView(mv, onlyElement).show();
                    }
                }
            }
        });

        return incon;
    }

    private Button prepareShowCollisionsWindowButton() {
        final Button conf = new Button("Pokaz kolizje");
        conf.setSizeFull();
        conf.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                if(!gridView.getGrid().getSelectedItems().isEmpty()) {
                    ScheduleEvent onlyElement = Iterables.getOnlyElement(gridView.getGrid().getSelectedItems());
                    if(onlyElement.hasAnyCollisions()) {
                        new ShowCollisionsWindow(mv, onlyElement).show();
                    }
                }
            }
        });

        return conf;
    }

    private Button prepareRefreshButton() {
        final Button refreshButton = new Button("Odswiez");
        refreshButton.setSizeFull();
        refreshButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                ScheduleEventsView.this.refreshLay();
            }
        });

        return refreshButton;
    }

    private Button prepareCreateScheduleButton() {
        final Button create = new Button("Generuj harmonogram");
        create.setSizeFull();
        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                Set<EventTimeInterval> intervals= new LinkedHashSet<>();
                for (final String day : AddScheduleEventWindow.PRE_DAYS) {
                    for (final String hour : AddScheduleEventWindow.PRE_HOURS) {
                        intervals.add(new EventTimeInterval(day,hour));
                    }
                }

                List<ScheduleEvent> scheduleEvents = scheduleEventRepository.allEvents();

                Schedule generate = scheduleGenerator.generate(intervals, scheduleEvents,
                        new HashSet<>(scheduleConditionRepository.allConditions()));

                scheduleEvents.forEach(ex -> {
                    scheduleEventRepository.update(ex);
                    System.out.println(ex.toString());
                });
                scheduleEventRepository.allEvents().size();
            }
        });

        return create;
    }

    @Override
    public void refreshLay() {
        setupGridView();
        setupPanel();
        mv.setMainContent(layout);
    }

}
