package org.safehaus.chop.webapp.vaadin;

import com.vaadin.annotations.Title;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.safehaus.chop.webapp.vaadin.util.FileUtil;
//import org.safehaus.chop.webapp.util.FileUtil;

@Title("Test UI")
public class TestUI extends UI {

	protected void init(VaadinRequest request) {
		initLayout();
	}

	private void initLayout() {

//		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
//		setContent(splitPanel);

        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setWidth(1000, Sizeable.UNITS_PIXELS);
        layout.setHeight(1000, Sizeable.UNITS_PIXELS);
        setContent(layout);

        Button button = new Button("Button");
        button.setWidth(120, Sizeable.UNITS_PIXELS);

        layout.addComponent(button, "left: 10px; top: 150px;");

        button.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                buttonClicked();
            }
        });

        AbsoluteLayout chartLayout = new AbsoluteLayout();
        chartLayout.setWidth(800, Sizeable.UNITS_PIXELS);
        chartLayout.setHeight(300, Sizeable.UNITS_PIXELS);
        //chartLayout.setDebugId("chart");
        chartLayout.setId("chart");
        layout.addComponent(chartLayout, "left: 200px; top: 10px;");

    }

    private void buttonClicked() {

        System.out.println("button clicked");

        /*
        JavaScript.getCurrent().execute( FileUtil.getContent("../../js/jquery.min.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("../../js/highcharts.js") );
        JavaScript.getCurrent().execute( FileUtil.getContent("../../js/chart.js") );
        */
    }

}
