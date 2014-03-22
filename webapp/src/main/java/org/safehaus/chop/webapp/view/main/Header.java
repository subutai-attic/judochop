package org.safehaus.chop.webapp.view.main;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.util.UIUtil;
import org.safehaus.chop.webapp.view.user.UserSubwindow;

public class Header extends AbsoluteLayout {

    private ModuleDao moduleDao = InjectorFactory.getInstance(ModuleDao.class);

    private Label moduleLabel = UIUtil.addLabel(this, "", "left: 10px; top: 10px;", "500px");
    private Label userLabel = UIUtil.addLabel(this, "<b>[username]</b>", "left: 800px; top: 10px;", "100px");

    public Header() {
        initSize();
        addManageButton();
    }

    private void addManageButton() {

        Button button = UIUtil.addButton(this, "Manage", "left: 900px; top: 10px;", "100px");
        button.setStyleName(Reindeer.BUTTON_LINK);

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                manageButtonClicked();
            }
        });
    }

    private void manageButtonClicked() {
        UI.getCurrent().addWindow( new UserSubwindow() );
    }

    private void initSize() {
        setWidth("1000px");
        setHeight("25px");
    }

    void showModule(String moduleId) {

        Module module = moduleDao.get(moduleId);
        String caption = String.format(
                "<b>%s / %s / %s</b>",
                module.getGroupId(),
                module.getArtifactId(),
                module.getVersion()
        );

        moduleLabel.setValue(caption);
    }
}
