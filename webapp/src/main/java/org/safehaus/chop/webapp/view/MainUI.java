package org.safehaus.chop.webapp.view;

import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.*;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.ModuleService;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsChart;
import org.safehaus.chop.webapp.view.chart.overview.OverviewChart;
import org.safehaus.chop.webapp.view.chart.runs.RunsChart;
import org.safehaus.chop.webapp.view.util.FileUtil;
import org.safehaus.chop.webapp.view.window.UserSubwindow;

import java.util.List;
import java.util.Set;

@Title("Test UI")
public class MainUI extends UI {

    private ModuleService moduleService = InjectorFactory.getInstance(ModuleService.class);
    private NoteDao noteDao = InjectorFactory.getInstance(NoteDao.class);

    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);
    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);

    OverviewChart overviewChart = new OverviewChart();

    private ComboBox testNamesCombo;
    private ComboBox metricCombo;
    private ComboBox percentileCombo;
    private ComboBox failureCombo;

    protected void init(VaadinRequest request) {
        initLayout();
	}

    private void initLayout_() {
        // Some UI logic to open the sub-window
        final Button open = new Button("Open Sub-Window");
        open.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserSubwindow sub = new UserSubwindow();

                // Add it to the root component
                UI.getCurrent().addWindow(sub);
            }
        });

        setContent(open);
    }

	private void initLayout() {

        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        setContent(hsplit);

        hsplit.setSplitPosition(25);

        hsplit.setFirstComponent(getTreeTable());

        AbsoluteLayout rightLayout = new AbsoluteLayout();
        rightLayout.setSizeFull();
//        rightLayout.setWidth(800, Sizeable.UNITS_PIXELS);
//        rightLayout.setHeight(800, Sizeable.UNITS_PIXELS);

        addTestNamesCombo(rightLayout);
        addMetricCombo(rightLayout);
        addPercentileCombo(rightLayout);
        addFailureCombo(rightLayout);
        addSubmitButton(rightLayout);

        // ------------------------------------------------------------------------------------
        Button userManagementButton = new Button("User Management");
        userManagementButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserSubwindow sub = new UserSubwindow();

                // Add it to the root component
                UI.getCurrent().addWindow(sub);
            }
        });

        rightLayout.addComponent(userManagementButton, "left: 800px; top: 30px;");
        // ------------------------------------------------------------------------------------


        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(700, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(400, Sizeable.UNITS_PIXELS);
        chartLayout.setId("chart");

        rightLayout.addComponent(chartLayout, "left: 10px; top: 150px;");

        addNoteControls(rightLayout);


        hsplit.setSecondComponent(rightLayout);

        loadScripts();
    }

    private void addSubmitButton(AbsoluteLayout rightLayout) {

        Button button = new Button("Submit");
        button.setWidth("100px");
//        button.setWidth(100, Sizeable.UNITS_PIXELS);

//        saveButton.addListener(new Button.ClickListener() {
//            public void buttonClick(Button.ClickEvent event) {
//                saveNote();
//            }
//        });

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
//                Notification.show("Thank You!");
                submitButtonClicked();
            }
        });

        rightLayout.addComponent(button, "left: 600px; top: 80px;");
    }



    private void addFailureCombo(AbsoluteLayout rightLayout) {

        ComboBox comboBox = new ComboBox("Interation Points to Plot:");
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);

        comboBox.addItem("ALL");
        comboBox.addItem("FAILED");
        comboBox.addItem("SUCCESS");

        comboBox.select("ALL");

        rightLayout.addComponent(comboBox, "left: 400px; top: 80px;");

        failureCombo = comboBox;
    }

    private void addPercentileCombo(AbsoluteLayout rightLayout) {

        ComboBox comboBox = new ComboBox("Percentile:");
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);

        comboBox.addItem("100");
        comboBox.addItem("90");
        comboBox.addItem("80");
        comboBox.addItem("70");
        comboBox.addItem("60");
        comboBox.addItem("50");
        comboBox.addItem("40");
        comboBox.addItem("30");
        comboBox.addItem("20");
        comboBox.addItem("10");

        comboBox.select("100");

        rightLayout.addComponent(comboBox, "left: 200px; top: 80px;");

        percentileCombo = comboBox;
    }

    private void addMetricCombo(AbsoluteLayout rightLayout) {

        ComboBox comboBox = new ComboBox("Metric:");
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);

        comboBox.addItem("Avg Time");
        comboBox.addItem("Min Time");
        comboBox.addItem("Max Time");
        comboBox.addItem("Actual Time");

        comboBox.select("Avg Time");

        rightLayout.addComponent(comboBox, "left: 10px; top: 80px;");

        metricCombo = comboBox;
    }

    private void addTestNamesCombo(AbsoluteLayout rightLayout) {

        List<Commit> commits = commitDao.getByModule("1168044208");
        Set<String> testNames = runDao.getTestNames(commits);

        ComboBox comboBox = new ComboBox("Test Name:");
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);

        for (String testName : testNames) {
            comboBox.addItem(testName);
        }

        comboBox.select(testNames.iterator().next());

        rightLayout.addComponent(comboBox, "left: 10px; top: 30px;");

        testNamesCombo = comboBox;
    }


    private void addNoteControls(AbsoluteLayout layout) {

        Button saveButton = new Button("Save");
        saveButton.setWidth(100, Sizeable.UNITS_PIXELS);

        saveButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                saveNote();
            }
        });

        layout.addComponent(saveButton, "left: 10px; top: 700px;");



        Button readButton = new Button("Read");
        readButton.setWidth(100, Sizeable.UNITS_PIXELS);

        readButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                readNote();
            }
        });

        layout.addComponent(readButton, "left: 200px; top: 700px;");



        TextArea textArea = new TextArea("Log:");
        textArea.setWidth(100, Sizeable.UNITS_PIXELS);
        textArea.setHeight(100, Sizeable.UNITS_PIXELS);
        textArea.setWordwrap(false);

        layout.addComponent(textArea, "left: 10px; top: 750px;");
    }

    private void saveNote() {
        Note note = new Note("noteCommitId", 1, ""+System.currentTimeMillis());

        try {
            noteDao.save(note);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readNote() {
        try {
            System.out.println( noteDao.get("noteCommitId", 1) );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TreeTable getTreeTable() {

        final TreeTable ttable = new TreeTable("Modules");
        ttable.addContainerProperty("Module", String.class, "");
        ttable.addContainerProperty("Property", String.class, "");
        ttable.setSizeFull();

        ttable.addItem(new Object[]{"Module1", "v1"}, "id1");
        ttable.addItem(new Object[]{"Module2", "v2"}, 2);
        ttable.addItem(new Object[]{"version1", "v3"}, 3);
        ttable.addItem(new Object[]{"version2", "v4"}, 4);
        ttable.addItem(new Object[]{"version3", "v5"}, 5);
        ttable.addItem(new Object[]{"version4", "v6"}, 6);

        ttable.setParent(3, "id1");
        ttable.setParent(4, "id1");
        ttable.setParent(5, 2);
        ttable.setParent(6, 2);

        ttable.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
//                System.out.println("clicked: " + event.getItemId() + ", " + event.getPropertyId());
                itemClicked(event);
            }
        });

        return ttable;
    }

    private void itemClicked(ItemClickEvent event) {
//        System.out.println( FileUtil.getContent("js/chart.js") );
    }

    private void loadScripts() {
        JavaScript.getCurrent().execute( FileUtil.getContent("js/jquery.min.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("js/highcharts.js") );

        try {
            String moduleId = "1168044208";
            String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
            String metricType = "Min Time";
            int percentile = 100;
            String failureValue = "ALL";

            loadChart(moduleId, testName, metricType, percentile, failureValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChart(String moduleId, String testName, String metricType, int percentile, String failureValue) {
        String chart = overviewChart.get(moduleId, testName, metricType, percentile, failureValue);
        JavaScript.getCurrent().execute(chart);
    }

    private void submitButtonClicked() {

        String moduleId = "1168044208";
        String testName = (String) testNamesCombo.getValue();
        String metricType = (String) metricCombo.getValue();
        int percentile = Integer.parseInt( (String) percentileCombo.getValue() );
        String failureType = (String) failureCombo.getValue();

        loadChart(moduleId, testName, metricType, percentile, failureType);
    }

}
