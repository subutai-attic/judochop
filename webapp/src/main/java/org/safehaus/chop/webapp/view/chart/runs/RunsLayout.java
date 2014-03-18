package org.safehaus.chop.webapp.view.chart.runs;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.util.FileUtil;
import org.safehaus.chop.webapp.view.window.UserSubwindow;

import java.util.List;
import java.util.Set;

public class RunsLayout extends AbsoluteLayout {

    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);
    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);
    private NoteDao noteDao = InjectorFactory.getInstance(NoteDao.class);

    private RunsChart runsChart = new RunsChart();

    private ComboBox metricCombo;
    private ComboBox percentileCombo;
    private ComboBox failureCombo;

    public RunsLayout() {
        setSizeFull();

        addMetricCombo();
        addPercentileCombo();
        addFailureCombo();
        addSubmitButton();
        addChartLayout();

        addCommitIdButtons();
    }

    private void addCommitIdButtons() {

        Button button = new Button("[commitId]");
        button.setId("commitIdButton");

        button.setStyleName(Reindeer.BUTTON_LINK);
        this.addComponent(button, "left: 800px; top: 30px;");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserSubwindow sub = new UserSubwindow();
                UI.getCurrent().addWindow(sub);
            }
        });


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

    private void loadChart(String commitId, String testName, String metricType, int percentile, String failureValue) {
        String chart = runsChart.get(commitId, testName, metricType, percentile, failureValue);
        JavaScript.getCurrent().execute(chart);
    }

    public void loadScripts() {
//        JavaScript.getCurrent().execute( FileUtil.getContent("js/jquery.min.js") );
//        JavaScript.getCurrent().execute( FileUtil.getContent("js/highcharts.js") );

        try {
            String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
            String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
            String metricType = "Avg Time";
            int percentile = 100;
            String failureValue = "ALL";

            loadChart(commitId, testName, metricType, percentile, failureValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitButtonClicked() {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
        String metricType = (String) metricCombo.getValue();
        int percentile = Integer.parseInt( (String) percentileCombo.getValue() );
        String failureType = (String) failureCombo.getValue();

        loadChart(commitId, testName, metricType, percentile, failureType);
    }

}
