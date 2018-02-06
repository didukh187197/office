package com.company.office.web.screens;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.app.core.restore.EntityRestore;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DialogAction;

import javax.inject.Inject;
import java.util.Map;

public class DeleteScreen extends EntityRestore {

    @Inject
    private Button btnClear;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entities.addValueChangeListener(e->
            btnClear.setEnabled(
                    (e.getValue() != null) && (entitiesDs.getItems().size() != 0)
            )
        );
    }

    public void onBtnClearClick() {
        showOptionDialog(
                getMessage("deleteSreen.delete.title"),
                getMessage("deleteSreen.delete.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> deleteEntities()),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    private void deleteEntities() {
        CommitContext commitContext = new CommitContext();
        commitContext.setSoftDeletion(false);
        int k = 0;

        Object[] items = entitiesDs.getItems().toArray();
        for (Object item : items) {
            StandardEntity entity = (StandardEntity) item;
            if (entity.getDeleteTs() != null) {
                commitContext.addInstanceToRemove(entity);
                k++;
            }
        }

        dataManager.commit(commitContext);
        entitiesDs.refresh();

        if (k != 0) {
            showNotification(getMessage("deleteSreen.deletedRecords") + k);
        }
    }

}