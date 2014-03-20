package org.safehaus.chop.webapp.view.chart.runs;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.json.JSONException;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.MainView;
import org.safehaus.chop.webapp.service.calc.Params;

import java.util.List;

public class RunsLayout extends AbsoluteLayout {

    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);
    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);
    private NoteDao noteDao = InjectorFactory.getInstance(NoteDao.class);

    private RunsChart runsChart = new RunsChart();

    private ComboBox metricCombo;
    private ComboBox percentileCombo;
    private ComboBox failureCombo;

    private MainView mainUI;

    private Button runNumberButton;

    public RunsLayout(MainView mainUI) {

        this.mainUI = mainUI;

        setSizeFull();

        addMetricCombo();
        addPercentileCombo();
        addFailureCombo();
        addSubmitButton();
        addChartLayout();

        addBackButton();
        addRunNumberButton();

        addNoteControls();
    }

    private void addRunNumberButton() {

        runNumberButton = new Button("...");
        runNumberButton.setId("commitIdButton");

        runNumberButton.setStyleName(Reindeer.BUTTON_LINK);
        this.addComponent(runNumberButton, "left: 800px; top: 30px;");

        runNumberButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                runNumberButtonClicked();
            }
        });
    }

    private void runNumberButtonClicked() {
//        mainUI.showIterationsLayout(selectedRunNumber);
    }

    private void addBackButton() {

        Button button = new Button("Back");

        button.setStyleName(Reindeer.BUTTON_LINK);
        this.addComponent(button, "left: 700px; top: 30px;");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
//                mainUI.showOverviewLayout();
            }
        });
    }

    private void addChartLayout() {

        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(700, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(400, Sizeable.UNITS_PIXELS);
        chartLayout.setId("runsChart");

        this.addComponent(chartLayout, "left: 10px; top: 150px;");
    }

    private void addSubmitButton() {

        Button button = new Button("Submit");
        button.setWidth("100px");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
//                loadChart(commitId);
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

//    private void loadChart(Params params) {
//        String chart = runsChart.get(params);
//        JavaScript.getCurrent().execute(chart);
//    }

    String commitId = "";

    public void loadChart(Params params) {

        this.commitId = params.getCommitId();

        metricCombo.select( params.getMetricType() );

        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
        String metricType = (String) metricCombo.getValue();
        int percentile = Integer.parseInt( (String) percentileCombo.getValue() );
        String failureType = (String) failureCombo.getValue();

        String chart = runsChart.get(params);
        JavaScript.getCurrent().execute(chart);

        addJavaScriptCallback();
    }




    private int selectedRunNumber = 1;

    private void addJavaScriptCallback() {
        JavaScript.getCurrent().addFunction("handleRunNumber",
                new JavaScriptFunction() {
                    @Override
                    public void call(org.json.JSONArray args) throws JSONException {
                        selectedRunNumber = args.getInt(0);
                        runNumberButton.setCaption(""+selectedRunNumber);

                        showRunners();
                        loadNote();
                    }
                }
        );
    }

    private void showRunners() {

        System.out.println( "---" );

        List<Run> runs = runDao.getList( commitId, selectedRunNumber );

        for ( Run run : runs ) {
            System.out.println( run.getId() + ": " + run.getRunner() );
        }

        Run run = runDao.get( "1706276721" );
        System.out.println(">> " + run);
    }

    private void loadNote() {
        Note note = noteDao.get( commitId, selectedRunNumber );
        String text = note != null ? note.getText() : "";
        noteTextArea.setValue( text );
    }

    private TextArea noteTextArea;

    private void addNoteControls() {

        noteTextArea = new TextArea("Note:");
        noteTextArea.setWidth(250, Sizeable.UNITS_PIXELS);
        noteTextArea.setHeight(100, Sizeable.UNITS_PIXELS);
        noteTextArea.setWordwrap(false);

        this.addComponent(noteTextArea, "left: 10px; top: 600px;");

        // ====================================================================

        Button saveButton = new Button("Save");
        saveButton.setWidth(100, Sizeable.UNITS_PIXELS);

        this.addComponent(saveButton, "left: 300px; top: 600px;");

        saveButton.addClickListener( new Button.ClickListener() {
            public void buttonClick( Button.ClickEvent event ) {
                saveNote();
            }
        });
    }

    private void saveNote() {
        Note note = new Note( commitId, selectedRunNumber, noteTextArea.getValue() );

        try {
            noteDao.save( note );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
