package org.safehaus.chop.webapp.view.util;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class UIUtil {

    public static ComboBox getCombo(AbsoluteLayout layout, String caption, String position, String... values) {

        ComboBox combo = new ComboBox(caption);
        combo.setTextInputAllowed(false);
        combo.setNullSelectionAllowed(false);

        layout.addComponent(combo, position);
        populateCombo(combo, values);

        return combo;
    }

    public static void populateCombo(ComboBox combo, String... values) {

        for (String testName : values) {
            combo.addItem(testName);
        }

        if (values.length > 0) {
            combo.select(values[0]);
        }
    }

    public static Button getButton(AbsoluteLayout layout, String caption, String position, String width) {

        Button button = new Button(caption);
        button.setWidth(width);
        layout.addComponent(button, position);

        return button;
    }

    public static AbsoluteLayout getLayout(AbsoluteLayout parent, String id, String position, String height, String width) {

        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setId(id);
        layout.setWidth(width);
        layout.setHeight(height);

        parent.addComponent(layout, position);

        return layout;
    }

}
