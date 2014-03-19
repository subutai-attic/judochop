package org.safehaus.chop.webapp.view;

import com.vaadin.annotations.Title;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.apache.commons.lang.StringUtils;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.chart.Params;
import org.safehaus.chop.webapp.view.chart.ViewContext;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsLayout;
import org.safehaus.chop.webapp.view.chart.overview.OverviewLayout;
import org.safehaus.chop.webapp.view.chart.runs.RunsLayout;
import org.safehaus.chop.webapp.view.chart.view.ChartView;
import org.safehaus.chop.webapp.view.chart.view.OverviewView;
import org.safehaus.chop.webapp.view.tree.ModuleSelectListener;
import org.safehaus.chop.webapp.view.tree.ModuleTreeHelper;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

@Title("Judo Chop")
public class MainView extends UI implements ViewContext, ModuleSelectListener {

    private HorizontalSplitPanel splitPanel;
    private ChartView overviewView;

    @Override
    protected void init(VaadinRequest request) {
        AbsoluteLayout initialLayout = initViews();
        initLayout(initialLayout);
        loadScripts();
    }

    private AbsoluteLayout initViews() {

        overviewView = new OverviewView(this, null, null);

        return overviewView;
    }

    private void initLayout(AbsoluteLayout initialLayout) {

        splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(25);
        splitPanel.setFirstComponent( ModuleTreeHelper.getTreeTable(this) );
        splitPanel.setSecondComponent(initialLayout);

        setContent(splitPanel);
    }

    private void loadScripts() {
        JavaScript.getCurrent().execute( FileUtil.getContent("js/jquery.min.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("js/highcharts.js") );
    }

    public void show(ChartView chartView) {

    }

    public void show(ChartView chartView, Params params) {

    }

    public void onModuleSelect(String moduleId) {
        System.out.println("module selected: " + moduleId);
    }



    // ===================================================================================================


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
