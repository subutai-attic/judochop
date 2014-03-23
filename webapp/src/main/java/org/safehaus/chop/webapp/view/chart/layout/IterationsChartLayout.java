package org.safehaus.chop.webapp.view.chart.layout;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.service.util.JsonUtil;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;
import org.safehaus.chop.webapp.view.main.Breadcrumb;
import org.safehaus.chop.webapp.view.util.UIUtil;

public class IterationsChartLayout extends ChartLayout {

    private RunResultDao runResultDao = InjectorFactory.getInstance(RunResultDao.class);

    protected Button failuresButton;
    private String runResultId;

    public IterationsChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, Breadcrumb breadcrumb) {
        super( new Config(
                layoutContext,
                chartBuilder,
                nextLayout,
                "iterationsChart",
                "iterationsChartCallback",
                "js/iterations-chart.js",
                new PointRadius(20, 15, 10, 10, 5, 7),
                breadcrumb
        ) );
    }

    @Override
    protected void addControls() {
        addMainControls();
        addSubControls(410);
        super.addSubControls(430);
    }

    @Override
    protected void addSubControls(int startTop) {

        String position = String.format("left: 750px; top: %spx;", startTop);

        failuresButton = UIUtil.addButton(this, "Show failures", position, "250px");
        failuresButton.setStyleName(Reindeer.BUTTON_LINK);

        failuresButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                showFailures();
            }
        });
    }

    private void setControlsReadOnly(boolean readOnly) {
        testNameCombo.setReadOnly(readOnly);
        metricCombo.setReadOnly(readOnly);
    }

    private void doShow(Params params) {
        setControlsReadOnly(false);
        super.show(params);
        setControlsReadOnly(true);
    }

    public void show(Params params) {
        doShow(params);
        noteLayout.load( params.getCommitId(), params.getRunNumber() );
        failuresButton.setVisible(false);
    }

    @Override
    protected void pointClicked(JSONObject json) throws JSONException {
        detailsTable.setContent(json);
        handlePointClick(json);
    }

    private void handlePointClick(JSONObject json) {

        runResultId = json.optString("id");
        int failures = json.optInt("failures");

        boolean buttonVisible = !StringUtils.isEmpty(runResultId) && failures > 0;
        failuresButton.setVisible(buttonVisible);
    }

    private void showFailures() {

        String failures = runResultDao.getFailures(runResultId);
        JSONArray arr = JsonUtil.parseArray(failures);
        String messages = firstMessages(arr);

        Notification.show("Failures", messages, Notification.Type.TRAY_NOTIFICATION);
    }

    private String firstMessages(JSONArray arr) {

        String s = "";
        int len = Math.min(5, arr.length());

        for (int i = 0; i < len; i++) {
            JSONObject json = JsonUtil.get(arr, i);

            s += "* " + StringUtils.abbreviate(json.optString("message"),  200) + "\n"
                    + StringUtils.abbreviate(json.optString("trace"),  500) + "\n\n";
        }

        return s;
    }

    @Override
    protected void handleBreadcrumb() {
        String caption = "Run: " + params.getRunNumber();
        config.getBreadcrumb().setItem(this, caption, 2);
    }
}
