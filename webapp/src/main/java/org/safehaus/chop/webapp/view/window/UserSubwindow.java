package org.safehaus.chop.webapp.view.window;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.*;
import org.safehaus.chop.webapp.shiro.MyShiroRealm;

import java.util.Map;

/**
 * Created by daralbaev on 3/17/14.
 */
public class UserSubwindow extends Window {

    /* User interface components are stored in session. */
    private Table userList = new Table();
    private TextField searchField = new TextField();
    private Button addNewUserButton = new Button("New");
    private Button removeUserButton = new Button("Remove this user");
    private Button editUserButton = new Button("Edit this user's groups");
    private Button saveButton = new Button("Save all");
    private Button cancelButton = new Button("Cancel");
    private FormLayout editorLayout = new FormLayout();
    private FieldGroup editorFields = new FieldGroup();

    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String[] fieldNames = new String[]{USERNAME, PASSWORD};

    /*
     * Any component can be bound to an external data source. This example uses
     * just a dummy in-memory list, but there are many more practical
     * implementations.
     */
    IndexedContainer userContainer = createUserDatasource();

    /*
     * After UI class is created, init() is executed. You should build and wire
     * up your user interface here.
     */
    public UserSubwindow() {
        super("Edit Users"); // Set window caption

        center();
        setClosable(false);
        setModal(true);

        // Set window size.
        setHeight("100%");
        setWidth("100%");

        userContainer = createUserDatasource();

        initLayout();
        initContactList();
        initEditor();
        initSearch();
        initAddRemoveButtons();
    }

    /*
    * In this example layouts are programmed in Java. You may choose use a
    * visual editor, CSS or HTML templates for layout instead.
    */
    private void initLayout() {

		/* Root of the user interface component tree is set */
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        setContent(splitPanel);

		/* Build the component tree */
        VerticalLayout leftLayout = new VerticalLayout();
        splitPanel.addComponent(leftLayout);
        splitPanel.addComponent(editorLayout);
        leftLayout.addComponent(userList);
        HorizontalLayout bottomLeftLayout = new HorizontalLayout();
        leftLayout.addComponent(bottomLeftLayout);
        bottomLeftLayout.addComponent(searchField);
        bottomLeftLayout.addComponent(addNewUserButton);
        bottomLeftLayout.addComponent(saveButton);
        bottomLeftLayout.addComponent(cancelButton);

		/* Set the contents in the left of the split panel to use all the space */
        leftLayout.setSizeFull();

		/*
         * On the left side, expand the size of the userList so that it uses
		 * all the space left after from bottomLeftLayout
		 */
        leftLayout.setExpandRatio(userList, 1);
        userList.setSizeFull();

		/*
         * In the bottomLeftLayout, searchField takes all the width there is
		 * after adding addNewUserButton. The height of the layout is defined
		 * by the tallest component.
		 */
        bottomLeftLayout.setWidth("100%");
        searchField.setWidth("100%");
        bottomLeftLayout.setExpandRatio(searchField, 1);

		/* Put a little margin around the fields in the right side editor */
        editorLayout.setMargin(true);
        editorLayout.setVisible(false);
    }

    private void initEditor() {

        editorLayout.addComponent(removeUserButton);
        editorLayout.addComponent(editUserButton);

		/* User interface can be created dynamically to reflect underlying data. */
        for (String fieldName : fieldNames) {
            TextField field = new TextField(fieldName);
            editorLayout.addComponent(field);
            field.setWidth("100%");

			/*
             * We use a FieldGroup to connect multiple components to a data
			 * source at once.
			 */
            editorFields.bind(field, fieldName);
        }

		/*
         * Data can be buffered in the user interface. When doing so, commit()
		 * writes the changes to the data source. Here we choose to write the
		 * changes automatically without calling commit().
		 */
        editorFields.setBuffered(false);
    }

    private void initSearch() {

		/*
         * We want to show a subtle prompt in the search field. We could also
		 * set a caption that would be shown above the field or description to
		 * be shown in a tooltip.
		 */
        searchField.setInputPrompt("Search contacts");

		/*
         * Granularity for sending events over the wire can be controlled. By
		 * default simple changes like writing a text in TextField are sent to
		 * server with the next Ajax call. You can set your component to be
		 * immediate to send the changes to server immediately after focus
		 * leaves the field. Here we choose to send the text over the wire as
		 * soon as user stops writing for a moment.
		 */
        searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);

		/*
         * When the event happens, we handle it in the anonymous inner class.
		 * You may choose to use separate controllers (in MVC) or presenters (in
		 * MVP) instead. In the end, the preferred application architecture is
		 * up to you.
		 */
        searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {

				/* Reset the filter for the userContainer. */
                userContainer.removeAllContainerFilters();
                userContainer.addContainerFilter(new ContactFilter(event
                        .getText()));
            }
        });
    }

    /*
     * A custom filter for searching names and companies in the
     * userContainer.
     */
    private class ContactFilter implements Container.Filter {
        private String needle;

        public ContactFilter(String needle) {
            this.needle = needle.toLowerCase();
        }

        public boolean passesFilter(Object itemId, Item item) {
            String haystack = ("" + item.getItemProperty(USERNAME).getValue()).toLowerCase();
            return haystack.contains(needle);
        }

        public boolean appliesToProperty(Object id) {
            return true;
        }
    }

    private void initAddRemoveButtons() {
        addNewUserButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

				/*
				 * Rows in the Container data model are called Item. Here we add
				 * a new row in the beginning of the list.
				 */
                userContainer.removeAllContainerFilters();
                Object contactId = userContainer.addItemAt(0);

				/*
				 * Each Item has a set of Properties that hold values. Here we
				 * set a couple of those.
				 */
                userList.getContainerProperty(contactId, USERNAME).setValue(
                        "Username");
                userList.getContainerProperty(contactId, PASSWORD).setValue(
                        "Password");

				/* Lets choose the newly created contact to edit it. */
                userList.select(contactId);
            }
        });

        removeUserButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Object contactId = userList.getValue();
                userList.removeItem(contactId);
            }
        });

        editUserButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Object contactId = userList.getValue();
                String username = (String) userList.getItem(contactId).getItemProperty(USERNAME).getValue();

                GroupSubwindow sub = new GroupSubwindow(username);
                // Add it to the root component
                UI.getCurrent().addWindow(sub);
            }
        });

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                close(); // Close the sub-window
                MyShiroRealm.initRealm();
            }
        });

        saveButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Map<String, String> users = MyShiroRealm.getUsers();
                users.clear();

                for(Object itemId : userList.getItemIds()){
                    String username = (String) userList.getItem(itemId).getItemProperty(USERNAME).getValue();
                    String password = (String) userList.getItem(itemId).getItemProperty(PASSWORD).getValue();
                    users.put(username, password);
                }

                close(); // Close the sub-window
                MyShiroRealm.saveRealm();
            }
        });
    }

    private void initContactList() {
        userList.setContainerDataSource(userContainer);
        userList.setVisibleColumns(new String[]{USERNAME});
        userList.setSelectable(true);
        userList.setImmediate(true);

        userList.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object contactId = userList.getValue();

				/*
				 * When a contact is selected from the list, we want to show
				 * that in our editor on the right. This is nicely done by the
				 * FieldGroup that binds all the fields to the corresponding
				 * Properties in our contact at once.
				 */
                if (contactId != null) {
                    editorFields.setItemDataSource(userList
                            .getItem(contactId));
                }

                editorLayout.setVisible(contactId != null);
            }
        });
    }

    /*
     * Generate some in-memory example data to play with. In a real application
     * we could be using SQLContainer, JPAContainer or some other to persist the
     * data.
     */
    private static IndexedContainer createUserDatasource() {
        IndexedContainer ic = new IndexedContainer();

        for (String p : fieldNames) {
            ic.addContainerProperty(p, String.class, "");
        }

        Map<String, String> users = MyShiroRealm.getUsers();

        for(String username : users.keySet()){
            Object id = ic.addItem();
            ic.getContainerProperty(id, USERNAME).setValue(
                    username);
            ic.getContainerProperty(id, PASSWORD).setValue(
                    users.get(username));
        }

        return ic;
    }
}
