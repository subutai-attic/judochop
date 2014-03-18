package org.safehaus.chop.webapp.view.chart.iterations;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.json.JSONException;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.MainUI;
import org.safehaus.chop.webapp.view.chart.runs.RunsChart;

public class IterationsLayout extends AbsoluteLayout {

    private IterationsChart iterationsChart = new IterationsChart();

    private ComboBox percentileCombo;
    private ComboBox failureCombo;

    private MainUI mainUI;

    public IterationsLayout(MainUI mainUI) {

        this.mainUI = mainUI;

        setSizeFull();

        addPercentileCombo();
        addFailureCombo();
        addSubmitButton();
        addChartLayout();

        addBackButton();
    }

    private void addBackButton() {

        Button button = new Button("Back");

        button.setStyleName(Reindeer.BUTTON_LINK);
        this.addComponent(button, "left: 700px; top: 30px;");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                mainUI.showRunsLayout("");
            }
        });
    }

    private void addChartLayout() {

        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(700, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(400, Sizeable.UNITS_PIXELS);
        chartLayout.setId("iterationsChart");

        this.addComponent(chartLayout, "left: 10px; top: 150px;");
    }

    private void addSubmitButton() {

        Button button = new Button("Submit");
        button.setWidth("100px");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
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

    private void loadChart(String testName, String commitId, int runNumber, int percentile, String failureValue) {
        String chart = iterationsChart.get(testName, commitId, runNumber, percentile, failureValue);
        JavaScript.getCurrent().execute(chart);
    }

    public void loadChart(int runNumber) {
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        int percentile = 100;
        String failureValue = "ALL";

        loadChart(testName, commitId, runNumber, percentile, failureValue);
    }

    public void loadScripts() {
        try {
            String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
            String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
            int runNumber = 2;
            int percentile = 100;
            String failureValue = "ALL";

            loadChart(testName, commitId, runNumber, percentile, failureValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitButtonClicked() {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
        int percentile = Integer.parseInt( (String) percentileCombo.getValue() );
        String failureType = (String) failureCombo.getValue();

        loadChart(testName, commitId, 2, percentile, failureType);
    }

}
