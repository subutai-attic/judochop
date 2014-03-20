package org.safehaus.chop.webapp.view.tree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.TreeTable;
import org.apache.commons.lang.StringUtils;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.service.InjectorFactory;

import java.util.List;

public class ModuleTreeBuilder {

    private static final String PARENT_PREFIX = "parent:";

    public static TreeTable getTree(ModuleSelectListener listener) {

        TreeTable treeTable = new TreeTable("Modules");
        treeTable.addContainerProperty("Group", String.class, "");
        treeTable.addContainerProperty("Artifact", String.class, "");
        treeTable.setSizeFull();
        treeTable.addItemClickListener( getItemClickListener(listener) );

        addItems(treeTable);

        return treeTable;
    }

    private static ItemClickListener getItemClickListener(final ModuleSelectListener listener) {
        return new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                onItemClick(event, listener);
            }
        };
    }

    private static void onItemClick(ItemClickEvent event, ModuleSelectListener listener) {

        String id = (String) event.getItemId();
        boolean isModuleVersion = !StringUtils.startsWith(id, PARENT_PREFIX);

        if (isModuleVersion) {
            listener.onModuleSelect(id);
        }
    }

    private static void addItems(TreeTable treeTable) {

        ModuleDao moduleDao = InjectorFactory.getInstance(ModuleDao.class);
        List<Module> modules = moduleDao.getAll();

        for (Module module : modules) {
            addItem(treeTable, module);
        }
    }

    private static void addItem(TreeTable treeTable, Module module) {

        String parentId = String.format( PARENT_PREFIX + "%s-%s", module.getGroupId(), module.getArtifactId() );
        treeTable.addItem( new Object[]{ module.getGroupId(), module.getArtifactId() }, parentId );
        treeTable.addItem( new Object[]{ module.getVersion(), "" }, module.getId() );

        treeTable.setParent(module.getId(), parentId);
    }

}
