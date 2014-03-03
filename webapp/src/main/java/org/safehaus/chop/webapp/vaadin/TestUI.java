package org.safehaus.chop.webapp.vaadin;

import com.vaadin.annotations.Title;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
//import org.safehaus.chop.webapp.util.FileUtil;

/* 
 * UI class is the starting point for your app. You may deploy it with VaadinServlet
 * or VaadinPortlet by giving your UI class name a parameter. When you browse to your
 * app a web page showing your UI is automatically generated. Or you may choose to 
 * embed your UI to an existing web page. 
 */
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
//        JavaScript.getCurrent().execute("console.log($('#chart'));");

/*        JavaScript.getCurrent().execute(s);

        JavaScript.getCurrent().execute("console.log(2);");

        s = FileUtil.getContent("../../js/highcharts.js");
        JavaScript.getCurrent().execute(s);

        JavaScript.getCurrent().execute("console.log(3);");

        s = FileUtil.getContent("../../js/chart.js");
        JavaScript.getCurrent().execute(s);

        JavaScript.getCurrent().execute("console.log(4);");*/
    }

}
