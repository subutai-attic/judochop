package org.safehaus.chop.webapp.view.chart.overview;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.json.*;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.MainView;
import org.safehaus.chop.webapp.service.calc.Params;

import java.util.List;
import java.util.Set;

public class OverviewLayout extends AbsoluteLayout {

    private CommitDao commitDao = InjectorFactory.getInstance( CommitDao.class );
    private RunDao runDao = InjectorFactory.getInstance( RunDao.class );
    private NoteDao noteDao = InjectorFactory.getInstance( NoteDao.class );

    private OverviewChart overviewChart = new OverviewChart();

    private ComboBox testNamesCombo;
    private ComboBox metricCombo;
    private ComboBox percentileCombo;
    private ComboBox failureCombo;
    private Button commitIdButton;

    private MainView mainUI;

    public OverviewLayout(MainView mainUI) {

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
            }
        });
    }



    private void addChartLayout() {

        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(700, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(400, Sizeable.UNITS_PIXELS);
        chartLayout.setId("overviewChart");

        this.addComponent(chartLayout, "left: 10px; top: 150px;");
    }

    private void addSubmitButton() {

        Button button = new Button("Submit");
        button.setWidth("100px");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
//                loadChart("1168044208");
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

    private void addJavaScriptCallback() {
        JavaScript.getCurrent().addFunction("com.example.foo.myfunc",
                new JavaScriptFunction() {
                    @Override
                    public void call(org.json.JSONArray args) throws JSONException {

                        JSONObject json = args.getJSONObject( 0 );
                        String commitId = json.getString( "commitId" );

                        commitIdButton.setCaption( commitId );

                        int runNumber = json.optInt( "runNumber" );

                        loadNote( commitId, runNumber );
                    }
                }
        );
    }

    private void loadNote( String commitId, int runNumber ) {
        Note note = noteDao.get( commitId, runNumber );

        System.out.println( note );
    }

    private void commitButtonClicked() {

        String commitId = commitIdButton.getCaption();

//        mainUI.showRunsLayout(commitId);
//        mainUI.showRunsLayout(getParams(commitId));
    }

    public void loadChart(String moduleId) {

//        String testName = (String) testNamesCombo.getValue();
//        String metricType = (String) metricCombo.getValue();
//        int percentile = Integer.parseInt( (String) percentileCombo.getValue() );
//        String failureType = (String) failureCombo.getValue();
//
//        Params params = new Params(
//                testName,
//                null,
//                metricType,
//                percentile,
//                failureType
//        );

        loadChart(moduleId, getParams(null));
    }

    private Params getParams(String commitId) {

        String testName = (String) testNamesCombo.getValue();
        String metricType = (String) metricCombo.getValue();
        int percentile = Integer.parseInt( (String) percentileCombo.getValue() );
        String failureType = (String) failureCombo.getValue();

        return new Params(
                testName,
                commitId,
                metricType,
                percentile,
                failureType
        );
    }

    private void submitButtonClicked() {
        loadChart("1168044208");
    }

    private void loadChart(String moduleId, Params params) {
//        String chart = overviewChart.get(moduleId, params);
//        JavaScript.getCurrent().execute(chart);
//        addJavaScriptCallback();
    }

}
