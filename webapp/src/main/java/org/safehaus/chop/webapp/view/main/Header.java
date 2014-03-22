package org.safehaus.chop.webapp.view.main;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;
import org.safehaus.chop.webapp.view.util.UIUtil;
import org.safehaus.chop.webapp.view.window.UserSubwindow;

public class Header extends AbsoluteLayout {

    public Header() {

        setWidth("1000px");
        setHeight("25px");

        Label module = UIUtil.addLabel(this, "<b>usergrid</b> / <b>collection</b> / <b>version1</b>", "left: 10px; top: 10px;", "200px");

//        Label label = UIUtil.addLabel(this, "<b>username</b> •", "left: 300px; top: 10px;", "200px");
        Label label = UIUtil.addLabel(this, "<b>username</b> | ", "left: 300px; top: 10px;", "100px");

        Button button = UIUtil.addButton(this, "Manage", "left: 420px; top: 10px;", "250px");
        button.setStyleName(Reindeer.BUTTON_LINK);
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserSubwindow sub = new UserSubwindow();
                UI.getCurrent().addWindow(sub);
            }
        });

    }
}
