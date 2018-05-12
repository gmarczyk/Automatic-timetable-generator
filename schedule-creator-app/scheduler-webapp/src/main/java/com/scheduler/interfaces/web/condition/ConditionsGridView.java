package com.scheduler.interfaces.web.condition;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.google.common.collect.Iterables;
import com.scheduler.application.LoginService;

import com.scheduler.presentation.framework.AddEditDeleteContent;
import com.scheduler.presentation.framework.GenericGridViewWithBasicManagement;
import com.scheduler.presentation.framework.RefreshableLayout;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.condition.ScheduleConditionRepository;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;

@Configurable(preConstruction = true)
public class ConditionsGridView implements RefreshableLayout {

    @Autowired
    private ScheduleConditionRepository scheduleConditionRepository;
    @Autowired
    private LoginService loginService;

    private HorizontalLayout layout;
    private Panel conditionsPanel;
    private Panel valuesPanel;

    private final MainUIView mv;
    private GenericGridViewWithBasicManagement<ScheduleCondition> gridView;
    private Grid<SchedulePropertyValue> multipleValuesGrid;


    public ConditionsGridView (MainUIView mv) {
        this.mv = mv;

        setupGridView();
        setupPanel();
        mv.setMainContent(layout);
    }

    private void setupPanel() {
        layout = new HorizontalLayout();
        layout.setSizeFull();

        VerticalLayout conditionsVlay =new VerticalLayout();
        conditionsVlay.setSizeFull();

        conditionsPanel = new Panel("Warunki");
        conditionsPanel.setSizeFull();

        HorizontalLayout conditionsGridViewLAyout = this.gridView.getLayout();
        VerticalLayout gridLayout = this.gridView.getGridLayout();
        AddEditDeleteContent addEditDeleteContent = this.gridView.getAddEditDeleteContent();

        conditionsGridViewLAyout.setExpandRatio(gridLayout,7.0f);
        conditionsGridViewLAyout.setExpandRatio(addEditDeleteContent.getContent(),3.0f);
        conditionsPanel.setContent(conditionsGridViewLAyout);
        conditionsVlay.addComponent(conditionsPanel);

        VerticalLayout valuesPanelLayout = new VerticalLayout();
        valuesPanelLayout.setSizeFull();

        Panel panel = new Panel("Wartosci wymuszane");
        panel.setSizeFull();
        multipleValuesGrid = new Grid<SchedulePropertyValue>();
        multipleValuesGrid.setSizeFull();
        multipleValuesGrid.addColumn(SchedulePropertyValue::getValue).setCaption("Wartosci");
        panel.setContent(multipleValuesGrid);
        valuesPanelLayout.addComponent(panel);

        layout.addComponent(conditionsVlay);
        layout.addComponent(valuesPanelLayout);
        layout.setExpandRatio(conditionsVlay,6.5f);
        layout.setExpandRatio(valuesPanelLayout,3.5f);

        this.gridView.getGrid().addItemClickListener(new ItemClickListener<ScheduleCondition>() {
            @Override
            public void itemClick(final Grid.ItemClick<ScheduleCondition> itemClick) {
                multipleValuesGrid.setItems(itemClick.getItem().getThen_entity_values());
            }
        });
    }

    private Set<SchedulePropertyValue> determinePanelValues() {
        ScheduleCondition onlyElement = Iterables.getOnlyElement(this.gridView.getGrid().getSelectedItems(),null);
        if(onlyElement == null) {
            return new HashSet<>();
        }

        return onlyElement.getThen_entity_values();
    }

    private void setupGridView() {

            this.gridView = new GenericGridViewWithBasicManagement<ScheduleCondition>(scheduleConditionRepository.allConditions(), mv) {
                @Override
                public void configureGrid() {
                    this.grid.addColumn(ScheduleCondition::getIf_entity).setCaption("Wlasciwosc warunkowa");
                    this.grid.addColumn(ScheduleCondition::getThen_entity_name).setCaption("Wlasciwosc wymuszana");

                    if (!loginService.isAnyOfRoles(UserRole.TENANT_ADMIN, UserRole.MANAGEMENT)) {
                        this.addEditDeleteContent.setEnabled(false);
                    }
                }

                @Override
                public void configureManagementButtons() {
                    this.addEditDeleteContent.getLayout().addComponent(prepareRefreshButton());

                    this.addEditDeleteContent.getAddButton().addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(final Button.ClickEvent clickEvent) {
                            AddConditionWindow addConditionWindow = new AddConditionWindow(mv);
                            addConditionWindow.show();
                        }
                    });

                    this.addEditDeleteContent.getLayout().removeComponent(this.addEditDeleteContent.getEditButton());

                    this.addEditDeleteContent.getDeleteButton().addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(final Button.ClickEvent clickEvent) {
                            Set<ScheduleCondition> selectedItems = grid.getSelectedItems();
                            if(selectedItems.size() != 1) {
                                return;
                            }

                            scheduleConditionRepository.delete(Iterables.getOnlyElement(selectedItems));
                        }
                    });
                }
            };

            this.gridView.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);
        }

        private Button prepareRefreshButton() {
            final Button refreshButton = new Button("Odswiez");
            refreshButton.setSizeFull();
            refreshButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final Button.ClickEvent clickEvent) {
               ConditionsGridView.this.refreshLay();
                }
            });

            return refreshButton;
        }

        @Override
        public void refreshLay() {
            setupGridView();
            setupPanel();
            mv.setMainContent(layout);
        }

}
