package org.safehaus.chop.webapp.view;

import com.vaadin.annotations.Title;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.apache.commons.lang.StringUtils;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsLayout;
import org.safehaus.chop.webapp.view.chart.overview.OverviewLayout;
import org.safehaus.chop.webapp.view.chart.runs.RunsLayout;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

@Title("Test UI")
public class MainUI extends UI {

    private final OverviewLayout overviewLayout = new OverviewLayout(this);
    private final RunsLayout runsLayout = new RunsLayout(this);
    private final IterationsLayout iterationsLayout = new IterationsLayout(this);

    private ModuleDao moduleDao = InjectorFactory.getInstance( ModuleDao.class );

    private HorizontalSplitPanel hsplit;

    protected void init(VaadinRequest request) {
        initLayout();
	}

	private void initLayout() {

        hsplit = new HorizontalSplitPanel();
        setContent(hsplit);

        hsplit.setSplitPosition(25);

        hsplit.setFirstComponent(getTreeTable());

        loadScripts();
//        showOverviewLayout();
//        showRunsLayout("");
//        showIterationsLayout();
    }

    public void loadScripts() {
        JavaScript.getCurrent().execute( FileUtil.getContent("js/jquery.min.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("js/highcharts.js") );
    }

    public void showRunsLayout(String commitId) {
        hsplit.setSecondComponent(runsLayout);
        runsLayout.loadChart(commitId);
    }

    String selectedModuleId = "";

    public void showOverviewLayout() {
        hsplit.setSecondComponent(overviewLayout);
//        overviewLayout.loadChart("1168044208");
        overviewLayout.loadChart( selectedModuleId );
    }

    public void showIterationsLayout(int runNumber) {
        hsplit.setSecondComponent(iterationsLayout);
        iterationsLayout.loadChart(runNumber);
    }

    private TreeTable getTreeTable() {

        TreeTable treeTable = new TreeTable( "Modules" );
        treeTable.addContainerProperty("Group", String.class, "");
        treeTable.addContainerProperty("Artifact", String.class, "");
        treeTable.setSizeFull();

        treeTable.addItemClickListener( new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick( ItemClickEvent event ) {
                String id = (String) event.getItemId();
                boolean parent = StringUtils.startsWith( id, "parent:" );

                if (!parent) {
                    selectedModuleId = id;
                    showOverviewLayout();
                }
            }
        });

        addTreeItems(treeTable);

        return treeTable;
    }

    private void addTreeItems( TreeTable treeTable ) {
        List<Module> modules = moduleDao.getAll();

        for (Module module : modules) {
            String parentId = String.format( "parent:%s-%s", module.getGroupId(), module.getArtifactId() );
            treeTable.addItem( new Object[]{ module.getGroupId(), module.getArtifactId() }, parentId );

            treeTable.addItem( new Object[]{ module.getVersion(), "" }, module.getId() );
            treeTable.setParent( module.getId(), parentId );
        }
    }
}
