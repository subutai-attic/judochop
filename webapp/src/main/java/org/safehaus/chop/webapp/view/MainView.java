package org.safehaus.chop.webapp.view;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartViewContext;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsLayout;
import org.safehaus.chop.webapp.view.chart.overview.OverviewLayout;
import org.safehaus.chop.webapp.view.chart.runs.RunsLayout;
import org.safehaus.chop.webapp.view.chart.view.ChartView;
import org.safehaus.chop.webapp.view.chart.view.OverviewChartView;
import org.safehaus.chop.webapp.view.chart.view.RunsChartView;
import org.safehaus.chop.webapp.view.tree.ModuleSelectListener;
import org.safehaus.chop.webapp.view.tree.ModuleTreeBuilder;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;

@Title("Judo Chop")
public class MainView extends UI implements ChartViewContext, ModuleSelectListener {

    private HorizontalSplitPanel splitPanel;
    private OverviewChartView overviewChartView;

    @Override
    protected void init(VaadinRequest request) {
        overviewChartView = initChartViews(this);
        initLayout();
        loadScripts();
    }

    private static OverviewChartView initChartViews(ChartViewContext viewContext) {

        ChartView runsChartView = new RunsChartView(viewContext, null, null);

        OverviewChartView overviewChartView = new OverviewChartView(viewContext, null, runsChartView);

        return overviewChartView;
    }

    private void initLayout() {

        splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(25);
        splitPanel.setFirstComponent( ModuleTreeBuilder.getTree(this) );

        setContent(splitPanel);
    }

    private void loadScripts() {
        JavaScriptUtil.loadFile("js/jquery.min.js");
        JavaScriptUtil.loadFile("js/highcharts.js");
    }

    public void onModuleSelect(String moduleId) {
        overviewChartView.show(moduleId);
        splitPanel.setSecondComponent(overviewChartView);
    }

    @Override
    public void show(ChartView chartView, Params params) {
        splitPanel.setSecondComponent(chartView);
        chartView.show(params);
    }
}
