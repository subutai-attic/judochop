package org.safehaus.chop.webapp.view.main;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.Reindeer;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.view.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NoteLayout extends AbsoluteLayout {

    private static final Logger LOG = LoggerFactory.getLogger(NoteLayout.class);

    private NoteDao noteDao = InjectorFactory.getInstance(NoteDao.class);
    private TextArea textArea;
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;

    private String commitId;
    private int runNumber;
    private String oldText;

    public NoteLayout() {
        init();
        addTextArea();
        addButtons();
    }

    private void init() {
        setWidth("250px");
        setHeight("250px");
    }

    private void addTextArea() {

        textArea = new TextArea("Note for selected run:");
        textArea.setWidth("250px");
        textArea.setHeight("100px");
        textArea.setWordwrap(false);
        textArea.setReadOnly(true);

        addComponent(textArea, "left: 0px; top: 15px;");
    }

    private void addButtons() {

        editButton = createButton("Edit", "left: 210px; top: 120px;", true);
        editButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                edit();
            }
        });

        saveButton = createButton("Save", "left: 180px; top: 120px;", false);
        saveButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                save();
                cancel();
            }
        });

        cancelButton = createButton("Cancel", "left: 210px; top: 120px;", false);
        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                restoreText();
                cancel();
            }
        });
    }

    private void restoreText() {
        textArea.setValue(oldText);
    }

    private Button createButton(String caption, String position, boolean visible) {

        Button button = UIUtil.getButton(this, caption, position, "50px");
        button.setStyleName(Reindeer.BUTTON_LINK);
        button.setVisible(visible);

        return button;
    }

    private void edit() {
        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        textArea.setReadOnly(false);
    }

    private void cancel() {
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        textArea.setReadOnly(true);
    }

    private void save() {

        Note note = new Note(commitId, runNumber, textArea.getValue() );

        try {
            noteDao.save( note );
        } catch (IOException e) {
            LOG.error("Exception while saving a note: ", e);
        }
    }

    private void doLoad(String commitId, int runNumber) {

        this.commitId = commitId;
        this.runNumber = runNumber;

        Note note = noteDao.get(commitId, runNumber);
        oldText = note != null ? note.getText() : "";

        textArea.setReadOnly(false);
        textArea.setValue(oldText);
    }

    public void load(String commitId, int runNumber) {
        doLoad(commitId, runNumber);
        cancel();
    }

}
