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

        OverviewChartView overviewChartView = new OverviewChartView(viewContext, null, null);

        return overviewChartView;
    }

    private void initLayout() {

        splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(25);
        splitPanel.setFirstComponent( ModuleTreeBuilder.getTree(this) );
//        splitPanel.setSecondComponent(initialLayout);

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
        System.out.println(params);
//        chartView.show(params);
    }





    // ===================================================================================================

//    public void show(ChartView chartView) {
//
//        Params params = new Params(
//            "1168044208",
//            "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest",
//            null,
//            0,
//            "Avg Time",
//            100,
//            "ALL"
//        );
//
//        chartView.show(params);
//    }

//    public void showOverviewLayout() {
//        hsplit.setSecondComponent(overviewLayout);
//        overviewLayout.loadChart( selectedModuleId );
//    }

    private final OverviewLayout overviewLayout = new OverviewLayout(this);
    private final RunsLayout runsLayout = new RunsLayout(this);
    private final IterationsLayout iterationsLayout = new IterationsLayout(this);

    private ModuleDao moduleDao = InjectorFactory.getInstance( ModuleDao.class );

    private HorizontalSplitPanel hsplit;


//    public void showRunsLayout(String commitId) {
    public void showRunsLayout(Params params) {
        hsplit.setSecondComponent(runsLayout);
        runsLayout.loadChart(params);
    }

    String selectedModuleId = "";

    public void showOverviewLayout() {
        hsplit.setSecondComponent(overviewLayout);
        overviewLayout.loadChart( selectedModuleId );
    }

    public void showIterationsLayout(int runNumber) {
        hsplit.setSecondComponent(iterationsLayout);
        iterationsLayout.loadChart(runNumber);
    }

}
