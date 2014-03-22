package org.safehaus.chop.webapp.view.main;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.builder.IterationsChartBuilder;
import org.safehaus.chop.webapp.service.chart.builder.OverviewChartBuilder;
import org.safehaus.chop.webapp.service.chart.builder.RunsChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.layout.ChartLayout;
import org.safehaus.chop.webapp.view.chart.layout.IterationsChartLayout;
import org.safehaus.chop.webapp.view.chart.layout.OverviewChartLayout;
import org.safehaus.chop.webapp.view.chart.layout.RunsChartLayout;
import org.safehaus.chop.webapp.view.tree.ModuleSelectListener;
import org.safehaus.chop.webapp.view.tree.ModuleTreeBuilder;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;
import org.safehaus.chop.webapp.view.window.UserSubwindow;

@Title("Judo Chop")
public class MainView extends UI implements ChartLayoutContext, ModuleSelectListener {

    private HorizontalSplitPanel splitPanel;
    private ChartLayout overviewLayout;

    @Override
    protected void init(VaadinRequest request) {
        overviewLayout = initChartViews(this);
        initLayout();
        loadScripts();

        // Some UI logic to open the sub-window
//        final Button open = new Button("Open Sub-Window");
//        open.addClickListener(new Button.ClickListener() {
//            public void buttonClick(Button.ClickEvent event) {
//                UserSubwindow sub = new UserSubwindow();
//                UI.getCurrent().addWindow(sub);
//            }
//        });
//
//        setContent(open);
    }

    private static ChartLayout initChartViews(ChartLayoutContext layoutContext) {

        IterationsChartBuilder iterationsChartBuilder = InjectorFactory.getInstance(IterationsChartBuilder.class);
        RunsChartBuilder runsChartBuilder = InjectorFactory.getInstance(RunsChartBuilder.class);
        OverviewChartBuilder overviewChartBuilder = InjectorFactory.getInstance(OverviewChartBuilder.class);

        ChartLayout iterationsLayout = new IterationsChartLayout(layoutContext, iterationsChartBuilder, null, null);
        ChartLayout runsLayout = new RunsChartLayout(layoutContext, runsChartBuilder, iterationsLayout, null);
        ChartLayout overviewLayout = new OverviewChartLayout(layoutContext, overviewChartBuilder, null, runsLayout);

        return overviewLayout;
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

    @Override
    public void onModuleSelect(String moduleId) {
        show(overviewLayout, new Params(moduleId) );
    }

    @Override
    public void show(ChartLayout chartView, Params params) {
        splitPanel.setSecondComponent(chartView);
        chartView.show(params);
    }
}
