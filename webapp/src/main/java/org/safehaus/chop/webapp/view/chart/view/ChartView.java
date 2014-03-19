package org.safehaus.chop.webapp.view.chart.view;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.RunService;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartViewContext;
import org.safehaus.chop.webapp.view.util.UIUtil;

import java.util.Set;

public abstract class ChartView extends AbsoluteLayout implements JavaScriptFunction {

    private RunService runService = InjectorFactory.getInstance(RunService.class);

    protected ChartViewContext chartViewContext;
    private ChartView prevView;
    protected ChartView nextChartView;
    //private ChartBuilder chartBuilder;

    protected ComboBox testNamesCombo;
    protected ComboBox metricCombo;
    protected ComboBox percentileCombo;
    protected ComboBox failureCombo;

    protected ChartView(ChartViewContext viewContext, ChartView prevView, ChartView nextChartView) {

        this.chartViewContext = viewContext;
        this.prevView = prevView;
        this.nextChartView = nextChartView;

        setSizeFull();

        addTestNamesCombo();
        addMetricCombo();
        addPercentileCombo();
        addFailureCombo();
        addSubmitButton();
    }

    protected void addChartLayout(String id) {
//        AbsoluteLayout chartLayout = UIUtil.getLayout("overviewChart", "700px", "400px");
        AbsoluteLayout chartLayout = UIUtil.getLayout(id, "700px", "400px");
        addComponent(chartLayout, "left: 10px; top: 150px;");
    }

    private void addSubmitButton() {

        Button button = UIUtil.getButton("Submit", "100px");
        addComponent(button, "left: 600px; top: 80px;");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                submitButtonClicked();
            }
        });
    }

    private void submitButtonClicked() {
        System.out.println("submit");
    }

    protected void addNextChartButton() {

        Button button = UIUtil.getButton("next chart", "150px");
        button.setStyleName(Reindeer.BUTTON_LINK);
        addComponent(button, "left: 800px; top: 30px;");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                nextChartButtonClicked();
            }
        });
    }

    protected void nextChartButtonClicked() {
        System.out.println("next");
    }

    private void addTestNamesCombo() {
        testNamesCombo = UIUtil.getCombo("Test Names:");
        addComponent(testNamesCombo, "left: 10px; top: 30px;");
    }

    private void addMetricCombo() {
        String metrics[] = {"Avg Time", "Min Time", "Max Time", "Actual Time"};

        metricCombo = UIUtil.getCombo("Metric:", metrics);
        addComponent(metricCombo, "left: 10px; top: 80px;");
    }

    private void addPercentileCombo() {
        String values[] = {"100", "90", "80", "70", "60", "50", "40", "30", "20", "10"};

        percentileCombo = UIUtil.getCombo("Percentile:", values);
        addComponent(percentileCombo, "left: 200px; top: 80px;");
    }

    private void addFailureCombo() {
        String values[] = {"ALL", "FAILED", "SUCCESS"};

        failureCombo = UIUtil.getCombo("Percentile:", values);
        addComponent(failureCombo, "left: 400px; top: 80px;");
    }

    protected void populateTestNames(String moduleId) {
        Set<String> testNames = runService.getTestNames(moduleId);
        UIUtil.populateCombo(testNamesCombo, testNames.toArray(new String[0]));
    }

    public void show(Params params) { }

//    public void showPrev() {}
//    public void showNext() {}
//    public void show() {}


}
