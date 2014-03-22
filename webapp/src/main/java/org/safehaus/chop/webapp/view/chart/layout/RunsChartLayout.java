package org.safehaus.chop.webapp.view.chart.layout;

import com.vaadin.data.Property;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;
import org.safehaus.chop.webapp.view.util.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunsChartLayout extends ChartLayout {

    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    private Map<String, Run> runners = new HashMap<String, Run>();
    private ListSelect runnersListSelect;

    public RunsChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, ChartLayout prevLayout) {
        super( new Config(
                layoutContext,
                chartBuilder,
                prevLayout,
                nextLayout,
                "runsChart",
                "runsChartCallback",
                "js/runs-chart.js",
                new PointRadius()
        ) );

        addNextChartButton();
    }

    @Override
    protected void addControls() {
        addMainControls();
        addSubControls(460);
        super.addSubControls(650);
    }

    @Override
    protected void addSubControls(int startTop) {

        String position = String.format("left: 750px; top: %spx;", startTop);
        runnersListSelect = UIUtil.addListSelect(this, "Runners:", position, "250px");

        runnersListSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (value != null) {
                    showRunner( value.toString() );
                }
            }
        });
    }

    private void showRunner(String runner) {

        Run run = runners.get(runner);

        String text = "- minTime: " + run.getMinTime()
                + "\n- maxTime: " + run.getMaxTime()
                + "\n- avgTime: " + run.getAvgTime()
                + "\n- actualTime: " + run.getActualTime()
                + "\n- iterations: " + run.getIterations()
                + "\n- failures: " + run.getFailures()
                + "\n- ignores: " + run.getIgnores()
                + "\n- threads: " + run.getThreads()
                + "\n- totalTestsRun: " + run.getTotalTestsRun();

        Notification.show(runner, text, Notification.Type.TRAY_NOTIFICATION);
    }

    @Override
    protected void pointClicked(JSONObject json) throws JSONException {
        super.pointClicked(json);
        handleRunNumber();
    }

    private void handleRunNumber() {
        nextChartButton.setCaption( "Run: " + params.getRunNumber() );
        showRunners();
    }

    private void showRunners() {

        runnersListSelect.removeAllItems();
        runners.clear();

        List<Run> runs = runDao.getList(params.getCommitId(), params.getRunNumber() );

        for (Run run : runs) {
            runnersListSelect.addItem(run.getRunner());
            runners.put(run.getRunner(), run);
        }
    }
}
