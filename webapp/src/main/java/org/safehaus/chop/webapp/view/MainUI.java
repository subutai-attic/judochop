package org.safehaus.chop.webapp.view;

import com.vaadin.annotations.Title;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.ModuleService;
import org.safehaus.chop.webapp.view.chart.OverviewChart;
import org.safehaus.chop.webapp.view.chart.format.OverviewFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

@Title("Test UI")
public class MainUI extends UI {

    private ModuleService moduleService = InjectorFactory.getInstance(ModuleService.class);

	protected void init(VaadinRequest request) {
        initLayout();
	}

	private void initLayout() {

        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        setContent(hsplit);

        hsplit.setSplitPosition(25);

        hsplit.setFirstComponent(getTreeTable());

        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(800, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(300, Sizeable.UNITS_PIXELS);
        chartLayout.setId("chart");

        hsplit.setSecondComponent(chartLayout);

        loadScripts();
    }

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

    private void loadScripts() {
        JavaScript.getCurrent().execute( FileUtil.getContent("js/jquery.min.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("js/highcharts.js") );

        try {
            JavaScript.getCurrent().execute( new OverviewChart().get() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
