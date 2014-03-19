package org.safehaus.chop.webapp.view.chart.view;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartViewContext;
import org.safehaus.chop.webapp.view.chart.overview.OverviewChart;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;
import org.safehaus.chop.webapp.view.util.UIUtil;

public class OverviewChartView extends ChartView {

    private String moduleId;
    private String commitId;

    private OverviewChart overviewChart = new OverviewChart();

    public OverviewChartView(ChartViewContext viewContext, ChartView prevView, ChartView nextView) {
        super(viewContext, prevView, nextView);
        addCommitIdButton();
    }

    public void show(String moduleId) {

        this.moduleId = moduleId;

        populateTestNames(moduleId);

        Params params = getParams();

        String chart = overviewChart.get(params);
        JavaScriptUtil.execute(chart);
        JavaScriptUtil.addCallback("overviewChartCallback", this);
    }

    private Params getParams() {
        return new Params(
                moduleId,
                (String) testNamesCombo.getValue(),
                commitId,
                0,
                (String) metricCombo.getValue(),
                Integer.parseInt( (String) percentileCombo.getValue() ),
                (String) failureCombo.getValue()
        );
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        commitId = json.getString("commitId");
    }

    private void addCommitIdButton() {

        Button button = UIUtil.getButton("open commit", "150px");
        button.setStyleName(Reindeer.BUTTON_LINK);

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                commitButtonClicked();
            }
        });

        addComponent(button, "left: 800px; top: 30px;");
    }

    private void commitButtonClicked() {
        System.out.println(commitId);
        chartViewContext.show( nextChartView, getParams() );
    }

}
