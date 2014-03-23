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
        initItems();
    }

    private void initItems() {
        items[0] = addButton(0, 10, "80px");
        items[1] = addButton(1, 100, "110px");
        items[2] = addLabel(240, "80px");
    }

    private Label addLabel(int left, String width) {
        String position = String.format("left: %spx; top: 10px;", left);
        return UIUtil.addLabel(this, "", position, width);
    }

    private Button addButton(final int pos, int left, String width) {

        String position = String.format("left: %spx; top: 10px;", left);

        Button button = UIUtil.addButton(this, "", position, width);
        button.setStyleName(Reindeer.BUTTON_LINK);

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                buttonClicked(pos);
            }
        });

        return button;
    }

    private void buttonClicked(int pos) {
        mainView.show(chartLayouts[pos]);
        hideItems(pos);
    }

    public void setItem(ChartLayout chartLayout, String caption, int pos) {

        setCaption(caption, pos);
        items[pos].setVisible(true);
        chartLayouts[pos] = chartLayout;

        hideItems(pos);
    }

    private void setCaption(String caption, int pos) {
        if (pos == 2) {
            ( (Label) items[pos] ).setValue( String.format("<b>%s</b>", caption) );
        } else {
            items[pos].setCaption(caption);
        }
    }

    private void hideItems(int pos) {
        for (int i = pos + 1; i < items.length; i++) {
            items[i].setVisible(false);
        }
    }
}
