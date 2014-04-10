package org.apache.usergrid.chop.webapp.view.main;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.apache.usergrid.chop.webapp.service.InjectorFactory;
import org.apache.usergrid.chop.webapp.service.chart.builder.RunsChartBuilder;
import org.apache.usergrid.chop.webapp.view.chart.layout.ChartLayout;
import org.apache.usergrid.chop.webapp.view.tree.ModuleSelectListener;
import org.apache.usergrid.chop.webapp.view.util.JavaScriptUtil;
import org.apache.usergrid.chop.webapp.service.chart.Params;
import org.apache.usergrid.chop.webapp.service.chart.builder.IterationsChartBuilder;
import org.apache.usergrid.chop.webapp.service.chart.builder.OverviewChartBuilder;
import org.apache.usergrid.chop.webapp.view.chart.layout.ChartLayoutContext;
import org.apache.usergrid.chop.webapp.view.chart.layout.IterationsChartLayout;
import org.apache.usergrid.chop.webapp.view.chart.layout.OverviewChartLayout;
import org.apache.usergrid.chop.webapp.view.chart.layout.RunsChartLayout;
import org.apache.usergrid.chop.webapp.view.tree.ModuleTreeBuilder;
import org.apache.usergrid.chop.webapp.view.util.UIUtil;

@Title("Judo Chop")
public class MainView extends UI implements ChartLayoutContext, ModuleSelectListener {

    private HorizontalSplitPanel splitPanel;
    private ChartLayout overviewLayout;

    private Breadcrumb breadcrumb = new Breadcrumb(this);
    private Header header = new Header();
    private AbsoluteLayout mainLayout;

    @Override
    protected void init(VaadinRequest request) {
        overviewLayout = initChartLayouts(this, breadcrumb);
        initLayout();
        loadScripts();
    }

    private static ChartLayout initChartLayouts(ChartLayoutContext layoutContext, Breadcrumb breadcrumb) {

        IterationsChartBuilder iterationsChartBuilder = InjectorFactory.getInstance(IterationsChartBuilder.class);
        RunsChartBuilder runsChartBuilder = InjectorFactory.getInstance(RunsChartBuilder.class);
        OverviewChartBuilder overviewChartBuilder = InjectorFactory.getInstance(OverviewChartBuilder.class);

        ChartLayout iterationsLayout = new IterationsChartLayout(layoutContext, iterationsChartBuilder, null, breadcrumb);
        ChartLayout runsLayout = new RunsChartLayout(layoutContext, runsChartBuilder, iterationsLayout, breadcrumb);
        ChartLayout overviewLayout = new OverviewChartLayout(layoutContext, overviewChartBuilder, runsLayout, breadcrumb);

        return overviewLayout;
    }

    private void initLayout() {

        splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(20);
        splitPanel.setFirstComponent( ModuleTreeBuilder.getTree(this) );
        splitPanel.setSecondComponent( initMainContainer() );

        setContent(splitPanel);
    }

    private AbsoluteLayout initMainContainer() {

        AbsoluteLayout container = new AbsoluteLayout();
        container.addComponent(header, "left: 0px; top: 0px;");
        container.addComponent(breadcrumb, "left: 0px; top: 30px;");

        mainLayout = UIUtil.addLayout(container, "", "left: 0px; top: 60px;", "1000px", "1000px");

        return container;
    }

    private void loadScripts() {
        JavaScriptUtil.loadFile("js/jquery.min.js");
        JavaScriptUtil.loadFile("js/highcharts.js");
    }

    @Override
    public void onModuleSelect(String moduleId) {
        header.showModule(moduleId);
        show(overviewLayout, new Params(moduleId) );
    }

    private void setChartLayout(ChartLayout chartLayout) {
        mainLayout.removeAllComponents();
        mainLayout.addComponent(chartLayout);
    }

    @Override
    public void show(ChartLayout chartLayout, Params params) {
        setChartLayout(chartLayout);
        chartLayout.show(params);
    }

    void show(ChartLayout chartLayout) {
        setChartLayout(chartLayout);
        chartLayout.loadChart();
    }
}
