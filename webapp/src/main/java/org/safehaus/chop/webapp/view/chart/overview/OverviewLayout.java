package org.safehaus.chop.webapp.view.chart.overview;

import com.google.gwt.json.client.JSONArray;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.json.*;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.MainUI;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;
import java.util.Set;

public class OverviewLayout extends AbsoluteLayout {

    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);
    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);
    private NoteDao noteDao = InjectorFactory.getInstance(NoteDao.class);

    private OverviewChart overviewChart = new OverviewChart();

    private ComboBox testNamesCombo;
    private ComboBox metricCombo;
    private ComboBox percentileCombo;
    private ComboBox failureCombo;

    private Button commitIdButton;

    private MainUI mainUI;

    public OverviewLayout(MainUI mainUI) {

        this.mainUI = mainUI;

        setSizeFull();

        addTestNamesCombo();
        addMetricCombo();
        addPercentileCombo();
        addFailureCombo();
        addSubmitButton();
        addChartLayout();

        addCommitIdButton();
    }

    private void addCommitIdButton() {

        commitIdButton = new Button("...");
        commitIdButton.setId("commitIdButton");

        commitIdButton.setStyleName(Reindeer.BUTTON_LINK);
        this.addComponent(commitIdButton, "left: 800px; top: 30px;");

        commitIdButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                commitButtonClicked();
//                System.out.println( event.getButton().getCaption() );

//                UserSubwindow sub = new UserSubwindow();
//                UI.getCurrent().addWindow(sub);
                }
        });
    }

    private void commitButtonClicked() {
        String commitId = commitIdButton.getCaption();
        mainUI.showRunsLayout(commitId);
    }

    private void addChartLayout() {

        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(700, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(400, Sizeable.UNITS_PIXELS);
        chartLayout.setId("chart");

        this.addComponent(chartLayout, "left: 10px; top: 150px;");
    }

    private void addSubmitButton() {

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

        this.addComponent(button, "left: 600px; top: 80px;");
    }



    private void addFailureCombo() {

        ComboBox comboBox = new ComboBox("Interation Points to Plot:");
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);

        comboBox.addItem("ALL");
        comboBox.addItem("FAILED");
        comboBox.addItem("SUCCESS");

        comboBox.select("ALL");

        this.addComponent(comboBox, "left: 400px; top: 80px;");

        failureCombo = comboBox;
    }

    private void addPercentileCombo() {

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

        this.addComponent(comboBox, "left: 200px; top: 80px;");

        percentileCombo = comboBox;
    }

    private void addMetricCombo() {

        ComboBox comboBox = new ComboBox("Metric:");
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);

        comboBox.addItem("Avg Time");
        comboBox.addItem("Min Time");
        comboBox.addItem("Max Time");
        comboBox.addItem("Actual Time");

        comboBox.select("Avg Time");

        this.addComponent(comboBox, "left: 10px; top: 80px;");

        metricCombo = comboBox;
    }

    private void addTestNamesCombo() {

        List<Commit> commits = commitDao.getByModule("1168044208");
        Set<String> testNames = runDao.getTestNames(commits);

        ComboBox comboBox = new ComboBox("Test Name:");
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);

        for (String testName : testNames) {
            comboBox.addItem(testName);
        }

        comboBox.select(testNames.iterator().next());

        this.addComponent(comboBox, "left: 10px; top: 30px;");

        testNamesCombo = comboBox;
    }


    private void addNoteControls() {

        Button saveButton = new Button("Save");
        saveButton.setWidth(100, Sizeable.UNITS_PIXELS);

//        saveButton.addListener(new Button.ClickListener() {
//            public void buttonClick(Button.ClickEvent event) {
//                saveNote();
//            }
//        });

        this.addComponent(saveButton, "left: 10px; top: 700px;");



        Button readButton = new Button("Read");
        readButton.setWidth(100, Sizeable.UNITS_PIXELS);

//        readButton.addListener(new Button.ClickListener() {
//            public void buttonClick(Button.ClickEvent event) {
//                readNote();
//            }
//        });

        this.addComponent(readButton, "left: 200px; top: 700px;");


        TextArea textArea = new TextArea("Log:");
        textArea.setWidth(100, Sizeable.UNITS_PIXELS);
        textArea.setHeight(100, Sizeable.UNITS_PIXELS);
        textArea.setWordwrap(false);

        this.addComponent(textArea, "left: 10px; top: 750px;");
    }


    private void loadChart(String moduleId, String testName, String metricType, int percentile, String failureValue) {
        String chart = overviewChart.get(moduleId, testName, metricType, percentile, failureValue);
        JavaScript.getCurrent().execute(chart);
    }

    public void loadScripts() {

        JavaScript.getCurrent().execute( FileUtil.getContent("js/jquery.min.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("js/highcharts.js") );

        try {
            String moduleId = "1168044208";
            String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
            String metricType = "Avg Time";
            int percentile = 100;
            String failureValue = "ALL";

            loadChart(moduleId, testName, metricType, percentile, failureValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JavaScript.getCurrent().addFunction("com.example.foo.myfunc",
            new JavaScriptFunction() {
                @Override
                public void call(org.json.JSONArray args) throws JSONException {
                    String commitId = args.getString(0);
                    commitIdButton.setCaption(commitId);
                }
            }
        );

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
