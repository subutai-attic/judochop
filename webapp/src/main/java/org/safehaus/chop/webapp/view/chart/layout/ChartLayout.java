package org.safehaus.chop.webapp.view.chart.layout;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.safehaus.chop.webapp.service.DataService;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;
import org.safehaus.chop.webapp.view.util.UIUtil;

import java.util.Set;

public abstract class ChartLayout extends AbsoluteLayout implements JavaScriptFunction {

    private DataService dataService = InjectorFactory.getInstance(DataService.class);

    protected ChartLayoutContext chartLayoutContext;
//    private ChartView prevView;
    protected ChartLayout nextLayout;
    private ChartBuilder chartBuilder;
    private String jsCallbackName;

    protected ComboBox testNamesCombo;
    protected ComboBox metricCombo;
    protected ComboBox percentileCombo;
    protected ComboBox failureCombo;

    protected Params params;

    protected ChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout prevLayout, ChartLayout nextLayout, String chartId, String jsCallbackName) {

        this.chartLayoutContext = layoutContext;
        this.chartBuilder = chartBuilder;
//        this.prevView = prevView;
        this.nextLayout = nextLayout;
        this.jsCallbackName = jsCallbackName;

        setSizeFull();
        addControls(chartId);
    }

    protected void addControls(String chartId) {
        addTestNamesCombo();
        addMetricCombo();
        addPercentileCombo();
        addFailureCombo();
        addSubmitButton();
        addChartLayout(chartId);
    }

    private void addChartLayout(String id) {
        AbsoluteLayout chartLayout = UIUtil.getLayout(id, "700px", "400px");
        addComponent(chartLayout, "left: 10px; top: 150px;");
    }

    protected void addSubmitButton() {

        Button button = UIUtil.getButton("Submit", "100px");
        addComponent(button, "left: 600px; top: 80px;");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                submitButtonClicked();
            }
        });
    }

    protected void submitButtonClicked() {
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

    private void nextChartButtonClicked() {
        chartLayoutContext.show(nextLayout, getParams());
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

    protected void addPercentileCombo() {
        String values[] = {"100", "90", "80", "70", "60", "50", "40", "30", "20", "10"};

        percentileCombo = UIUtil.getCombo("Percentile:", values);
        addComponent(percentileCombo, "left: 200px; top: 80px;");
    }

    protected void addFailureCombo() {
        String values[] = {"ALL", "FAILED", "SUCCESS"};

        failureCombo = UIUtil.getCombo("Interation Points to Plot:", values);
        addComponent(failureCombo, "left: 400px; top: 80px;");
    }

    protected void populateTestNames() {
        Set<String> testNames = dataService.getTestNames( params.getModuleId() );
        UIUtil.populateCombo(testNamesCombo, testNames.toArray(new String[0]));
    }

    protected Params getParams() {
        return new Params(
                params.getModuleId(),
                (String) testNamesCombo.getValue(),
                params.getCommitId(),
                params.getRunNumber(),
                (String) metricCombo.getValue(),
                Integer.parseInt( (String) percentileCombo.getValue() ),
                (String) failureCombo.getValue()
        );
    }

    public void show(Params params_) {

        this.params = params_;

        populateTestNames();
        String chart = chartBuilder.getChart( getParams() );
        JavaScriptUtil.loadChart(chart, jsCallbackName, this);
    }

//    public void showPrev() {}
//    public void show() {}

}
