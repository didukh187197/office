package com.company.office.web.screens;

import com.company.office.OfficeConfig;
import com.company.office.entity.Applicant;
import com.company.office.entity.Request;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Formatter;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.impl.DsContextImplementation;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainSettingsScreen extends AbstractWindow {

    @Inject
    private DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Inject
    private TextField txtCompanyName;

    @Inject
    private LookupField lookupApplicantsGroup;

    @Inject
    private LookupField lookupWorkersGroup;

    @Inject
    private OfficeConfig officeConfig;

    protected CollectionDatasource entitiesDs;

    @Override
    public void init(Map<String, Object> params) {
        if (officeConfig.getCompanyName() != null)
            txtCompanyName.setValue(officeConfig.getCompanyName());

        if (officeConfig.getApplicantsGroup() != null)
            lookupApplicantsGroup.setValue(officeConfig.getApplicantsGroup());

        if (officeConfig.getWorkersGroup() != null)
            lookupWorkersGroup.setValue(officeConfig.getWorkersGroup());
    }

    public void onBtnOkClick() {
        showOptionDialog(
                getMessage("settingsDialog.saveAndClose.title"),
                getMessage("settingsDialog.saveAndClose.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            if (lookupApplicantsGroup.getValue() == null) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.empty.title"),
                                        String.format(getMessage("settingsDialog.warning.empty.msg"),"Applicant"),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            if (lookupWorkersGroup.getValue() == null) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.empty.title"),
                                        String.format(getMessage("settingsDialog.warning.empty.msg"),"Worker"),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            if (lookupApplicantsGroup.getValue() == lookupWorkersGroup.getValue()) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.equals.title"),
                                        getMessage("settingsDialog.warning.equals.msg"),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            officeConfig.setCompanyName(txtCompanyName.getValue());

                            officeConfig.setApplicantsGroup(lookupApplicantsGroup.getValue());
                            officeConfig.setApplicantsGroupQuery(
                                    String.format("select e from sec$User e where e.group.id = '%s'", officeConfig.getApplicantsGroup().getId())
                            );

                            officeConfig.setWorkersGroup(lookupWorkersGroup.getValue());
                            officeConfig.setWorkersGroupQuery(
                                    String.format("select e from sec$User e where e.group.id = '%s'", officeConfig.getWorkersGroup().getId())
                            );

                            this.close("");
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onBtnCancelClick() {
        this.close("");
    }

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected BoxLayout tableBox;

    @Inject
    protected MessageTools messageTools;

    public void onApplicant(Component source) {
        /*
        //LoadContext.Query query = LoadContext.createQuery("select e from office$Applicant e where e.deleteTs is not null");
        LoadContext.Query query = LoadContext.createQuery("select e from office$Applicant e");
        LoadContext<Applicant> loadContext = LoadContext.create(Applicant.class).setQuery(query).setView("applicant-delete");

        CommitContext commitContext = new CommitContext();
        //commitContext.setSoftDeletion(false);

        //showMessageDialog("Deletion","Deletion", MessageType.CONFIRMATION);
        for (Applicant applicant : dataManager.loadList(loadContext)) {
            showMessageDialog("Deletion","" + applicant.getUser().getName(), MessageType.CONFIRMATION);
            //commitContext.addInstanceToRemove(applicant);
        }

        //dataManager.commit(commitContext);
        */
        createEntitiesTable(metadata.getClass(Applicant.class));
        //createEntitiesTable(Applicant.class);

    }

    public void onRequest(Component source) {
        createEntitiesTable(metadata.getClass(Request.class));
        //createEntitiesTable(Request.class);
    }

    Table entitiesTable;
    public static final int MAX_TEXT_LENGTH = 50;

    protected boolean isEmbedded(MetaProperty metaProperty) {
        return metaProperty.getAnnotatedElement().isAnnotationPresent(javax.persistence.Embedded.class);
    }

    protected String getPropertyCaption(MetaClass metaClass, MetaProperty metaProperty) {
        return messageTools.getPropertyCaption(metaClass, metaProperty.getName());
    }

    //protected void createEntitiesTable(Class meta) {
    protected void createEntitiesTable(MetaClass meta) {
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(getMessage("dateTimeFormat"));
        Formatter dateTimeFormatter = value -> {
            if (value == null) {
                return StringUtils.EMPTY;
            }

            return dateTimeFormat.format(value);
        };

        if (entitiesTable != null)
            tableBox.remove(entitiesTable);

        if (entitiesDs != null)
            ((DsContextImplementation) getDsContext()).unregister(entitiesDs);

        entitiesDs = DsBuilder.create(getDsContext())
                .setId("entitiesDs")
                .setMetaClass(meta)
                .setView(createView(meta))
                //.setViewName(View.BASE)
                .setSoftDeletion(false)
                .buildCollectionDatasource();

        entitiesTable = componentsFactory.createComponent(Table.class);
        entitiesTable.setFrame(frame);

        //collect properties in order to add non-system columns first
        List<Table.Column> nonSystemPropertyColumns = new ArrayList<>(10);
        List<Table.Column> systemPropertyColumns = new ArrayList<>(10);
        for (MetaProperty metaProperty : meta.getProperties()) {
            //don't show embedded, transient & multiple referred entities
            if (isEmbedded(metaProperty) || metadata.getTools().isNotPersistent(metaProperty))
                continue;

            Range range = metaProperty.getRange();
            if (range.getCardinality().isMany())
                continue;

            Table.Column column = new Table.Column(meta.getPropertyPath(metaProperty.getName()));

            if (range.isDatatype() && range.asDatatype().getJavaClass().equals(Date.class)) {
                column.setFormatter(dateTimeFormatter);
            }

            if (metaProperty.getJavaType().equals(String.class)) {
                column.setMaxTextLength(MAX_TEXT_LENGTH);
            }

            if (!metadata.getTools().isSystem(metaProperty)) {
                column.setCaption(getPropertyCaption(meta, metaProperty));
                nonSystemPropertyColumns.add(column);
            } else {
                column.setCaption(metaProperty.getName());
                systemPropertyColumns.add(column);
            }
        }
        for (Table.Column column : nonSystemPropertyColumns) {
            entitiesTable.addColumn(column);
        }

        for (Table.Column column : systemPropertyColumns) {
            entitiesTable.addColumn(column);
        }

        entitiesTable.setDatasource(entitiesDs);
        tableBox.add(entitiesTable);
        entitiesDs.refresh();

        /*
        Object[] items = entitiesDs.getItems().toArray();
        for (int i = 0; i < items.length; i++)
            showMessageDialog("Deletion","" + i + ". " + ((Entity) items[i]).getId(), MessageType.CONFIRMATION);
            */

    }

    protected View createView(MetaClass meta) {
        //noinspection unchecked
        View view = new View(meta.getJavaClass(), false);
        for (MetaProperty metaProperty : meta.getProperties()) {
            switch (metaProperty.getType()) {
                case DATATYPE:
                case ENUM:
                    view.addProperty(metaProperty.getName());
                    break;
                case ASSOCIATION:
                case COMPOSITION:
                    if (!metaProperty.getRange().getCardinality().isMany()) {
                        View minimal = metadata.getViewRepository()
                                .getView(metaProperty.getRange().asClass(), View.MINIMAL);
                        View propView = new View(minimal, metaProperty.getName() + "Ds", false);
                        view.addProperty(metaProperty.getName(), propView);
                    }
                    break;
                default:
                    throw new IllegalStateException("unknown property type");
            }
        }
        return view;
    }
}