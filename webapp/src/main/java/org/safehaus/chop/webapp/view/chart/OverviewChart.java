package org.safehaus.chop.webapp.view.chart;

import org.safehaus.chop.webapp.view.chart.format.OverviewFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

public class OverviewChart {

    public static String get() {

        String s = FileUtil.getContent("js/overview-chart.js");
        s = s.replace("$categories", "'7072b19e', 'cc47b827'");

        String series = FileUtil.getContent("js/series.js");
        s = s.replace("$series", series);

//        return s;
        return OverviewFormat.get();
    }

}
