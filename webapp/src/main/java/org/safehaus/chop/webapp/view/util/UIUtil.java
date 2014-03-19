package org.safehaus.chop.webapp.view.util;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class UIUtil {

    public static ComboBox getCombo(String caption, String ... values) {

        ComboBox combo = new ComboBox(caption);
        combo.setTextInputAllowed(false);
        combo.setNullSelectionAllowed(false);

        populuteCombo(combo, values);

        return combo;
    }

    public static void populuteCombo(ComboBox combo, String ... values) {

        for (String testName : values) {
            combo.addItem(testName);
        }

        if (values.length > 0) {
            combo.select(values[0]);
        }
    }

    public static Button getButton(String caption, String width) {

        Button button = new Button(caption);
        button.setWidth(width);

        return button;
    }

    public static AbsoluteLayout getLayout(String id, String width, String height) {

        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setId(id);
        layout.setWidth(width);
        layout.setHeight(height);

        return layout;
    }

}
