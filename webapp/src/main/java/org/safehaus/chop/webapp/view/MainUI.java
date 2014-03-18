package org.safehaus.chop.webapp.view;

import com.vaadin.annotations.Title;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.safehaus.chop.webapp.dao.*;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsLayout;
import org.safehaus.chop.webapp.view.chart.overview.OverviewLayout;
import org.safehaus.chop.webapp.view.chart.runs.RunsLayout;
import org.safehaus.chop.webapp.view.util.FileUtil;

@Title("Test UI")
public class MainUI extends UI {

    private final OverviewLayout overviewLayout = new OverviewLayout(this);
    private final RunsLayout runsLayout = new RunsLayout(this);
    private final IterationsLayout iterationsLayout = new IterationsLayout(this);

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
        showRunsLayout("");
//        showIterationsLayout();
    }

    public void loadScripts() {
        JavaScript.getCurrent().execute( FileUtil.getContent("js/jquery.min.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("js/highcharts.js") );
    }

    public void showRunsLayout(String commitId) {
        hsplit.setSecondComponent(runsLayout);
        runsLayout.loadScripts();
    }

    public void showOverviewLayout() {
        hsplit.setSecondComponent(overviewLayout);
        overviewLayout.loadScripts();
    }

    public void showIterationsLayout(int runNumber) {
        hsplit.setSecondComponent(iterationsLayout);
//        iterationsLayout.loadScripts();
        iterationsLayout.loadChart(runNumber);
    }

//    private void saveNote() {
//        Note note = new Note("noteCommitId", 1, ""+System.currentTimeMillis());
//
//        try {
//            noteDao.save(note);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void readNote() {
//        try {
//            System.out.println( noteDao.get("noteCommitId", 1) );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private TreeTable getTreeTable() {

        final TreeTable ttable = new TreeTable("Modules");
        ttable.addContainerProperty("Module", String.class, "");
        ttable.addContainerProperty("Property", String.class, "");
        ttable.setSizeFull();

        ttable.addItem(new Object[]{"Module1", "v1"}, "id1");
        ttable.addItem(new Object[]{"Module2", "v2"}, 2);
        ttable.addItem(new Object[]{"version1", "v3"}, 3);
        ttable.addItem(new Object[]{"version2", "v4"}, 4);
        ttable.addItem(new Object[]{"version3", "v5"}, 5);
        ttable.addItem(new Object[]{"version4", "v6"}, 6);

        ttable.setParent(3, "id1");
        ttable.setParent(4, "id1");
        ttable.setParent(5, 2);
        ttable.setParent(6, 2);

        ttable.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
//                System.out.println("clicked: " + event.getItemId() + ", " + event.getPropertyId());
                itemClicked(event);
            }
        });

        return ttable;
    }

    private void itemClicked(ItemClickEvent event) {
//        System.out.println( FileUtil.getContent("js/chart.js") );
    }

}
