package org.safehaus.chop.webapp.view.util;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

public class UIUtil {

    public static ComboBox getCombo(AbsoluteLayout layout, String caption, String position, Object values[]) {

        ComboBox combo = new ComboBox(caption);
        combo.setTextInputAllowed(false);
        combo.setNullSelectionAllowed(false);

        layout.addComponent(combo, position);
        populateCombo(combo, values);

        return combo;
    }

    public static void populateCombo(ComboBox combo, Object values[]) {

        if (values == null || values.length == 0) {
            return;
        }

        for (Object value : values) {
            combo.addItem(value);
        }

        combo.select(values[0]);
    }

    public static void select(ComboBox combo, Object value) {
        if (value != null) {
            combo.select(value);
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

    public static void addLabel(AbsoluteLayout parent, String text, String position, String width) {

        Label label = new Label(text, ContentMode.HTML);
        label.setWidth(width);

        parent.addComponent(label, position);
    }

    public static ListSelect addListSelect(AbsoluteLayout parent, String caption, String position, String width) {

        ListSelect list = new ListSelect(caption);
        list.setWidth(width);
        list.setNullSelectionAllowed(false);
        list.setImmediate(true);

        parent.addComponent(list, position);

        return list;
    }

}
