package org.safehaus.chop.webapp.view;

import com.vaadin.annotations.Title;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.ModuleService;
import org.safehaus.chop.webapp.view.chart.overview.OverviewChart;
import org.safehaus.chop.webapp.view.chart.runs.IterationsChart;
import org.safehaus.chop.webapp.view.chart.runs.RunsChart;
import org.safehaus.chop.webapp.view.util.FileUtil;

@Title("Test UI")
public class MainUI extends UI {

    private ModuleService moduleService = InjectorFactory.getInstance(ModuleService.class);
    private NoteDao noteDao = InjectorFactory.getInstance(NoteDao.class);

	protected void init(VaadinRequest request) {
        initLayout();
	}

	private void initLayout() {

        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        setContent(hsplit);

        hsplit.setSplitPosition(25);

        hsplit.setFirstComponent(getTreeTable());

        AbsoluteLayout mainLayout = new AbsoluteLayout();
        mainLayout.setWidth(800, Sizeable.UNITS_PIXELS);
        mainLayout.setHeight(800, Sizeable.UNITS_PIXELS);

        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(700, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(400, Sizeable.UNITS_PIXELS);
        chartLayout.setId("chart");

        mainLayout.addComponent(chartLayout, "left: 0px; top: 0px;");

        addNoteControls(mainLayout);

        hsplit.setSecondComponent(mainLayout);

        loadScripts();
    }

    private void addNoteControls(AbsoluteLayout layout) {

        Button saveButton = new Button("Save");
        saveButton.setWidth(100, Sizeable.UNITS_PIXELS);

        saveButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                saveNote();
            }
        });

        layout.addComponent(saveButton, "left: 10px; top: 400px;");



        Button readButton = new Button("Read");
        readButton.setWidth(100, Sizeable.UNITS_PIXELS);

        readButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                readNote();
            }
        });

        layout.addComponent(readButton, "left: 200px; top: 400px;");



        TextArea textArea = new TextArea("Log:");
        textArea.setWidth(100, Sizeable.UNITS_PIXELS);
        textArea.setHeight(100, Sizeable.UNITS_PIXELS);
        textArea.setWordwrap(false);

        layout.addComponent(textArea, "left: 10px; top: 450px;");
    }

    private void saveNote() {
        Note note = new Note("noteCommitId", 1, ""+System.currentTimeMillis());

        try {
            noteDao.save(note);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readNote() {
        try {
            System.out.println( noteDao.get("noteCommitId", 1) );
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//            JavaScript.getCurrent().execute( new OverviewChart().get() );
//            JavaScript.getCurrent().execute( new RunsChart().get() );
            JavaScript.getCurrent().execute( new IterationsChart().get() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
