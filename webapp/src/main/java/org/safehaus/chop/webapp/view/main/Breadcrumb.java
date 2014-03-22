package org.safehaus.chop.webapp.view.main;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.safehaus.chop.webapp.view.chart.layout.ChartLayout;
import org.safehaus.chop.webapp.view.util.UIUtil;

public class Breadcrumb extends AbsoluteLayout {

    private AbstractComponent items[] = new AbstractComponent[3];
    private ChartLayout chartLayouts[] = new ChartLayout[3];
    private MainView mainView;

    public Breadcrumb(MainView mainView) {
        this.mainView = mainView;
        initSize();
        initItems();
    }

    private void initSize() {
        setWidth("1000px");
        setHeight("500px");
    }

    private void initItems() {
        addButton(0, 10);
        addButton(1, 140);
        items[2] = UIUtil.addLabel(this, "", "left: 340px; top: 23px;", "100px");
    }

    private void addButton(final int pos, int left) {

        String position = String.format("left: %spx; top: 10px;", left);

        Button button = UIUtil.addButton(this, "", position, "120px");
        button.setStyleName(Reindeer.BUTTON_LINK);

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                buttonClicked(pos);
            }
        });

        items[pos] = button;
    }

    private void buttonClicked(int pos) {
        mainView.show(chartLayouts[pos]);
        hideItems(pos);
    }

    public void setItem(ChartLayout chartLayout, String caption, int pos) {

        items[pos].setCaption(caption);
        items[pos].setVisible(true);
        chartLayouts[pos] = chartLayout;

        hideItems(pos);
    }

    private void hideItems(int pos) {
        for (int i = pos + 1; i < items.length; i++) {
            items[i].setVisible(false);
        }
    }
}
