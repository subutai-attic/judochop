package org.safehaus.chop.webapp.view.chart.view;

import com.vaadin.ui.AbsoluteLayout;
import org.safehaus.chop.webapp.view.chart.Params;
import org.safehaus.chop.webapp.view.chart.ViewContext;

public abstract class ChartView extends AbsoluteLayout {

    private ViewContext viewContext;
    private ChartView prevView;
    private ChartView nextView;
    //private ChartBuilder chartBuilder;

    protected ChartView(ViewContext viewContext, ChartView prevView, ChartView nextView) {
        this.viewContext = viewContext;
        this.prevView = prevView;
        this.nextView = nextView;
    }

    protected void showPrev() {

    }

    protected void showNext() {

    }

    protected void show() {

    }

    protected void show(Params params) {

    }

}
