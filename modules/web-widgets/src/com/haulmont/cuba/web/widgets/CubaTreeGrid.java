package com.haulmont.cuba.web.widgets;

import com.haulmont.cuba.web.widgets.client.treegrid.CubaTreeGridState;
import com.haulmont.cuba.web.widgets.data.EnhancedHierarchicalDataProvider;
import com.haulmont.cuba.web.widgets.grid.CubaEditorField;
import com.haulmont.cuba.web.widgets.grid.CubaEditorImpl;
import com.haulmont.cuba.web.widgets.grid.CubaGridColumn;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.components.grid.Editor;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.renderers.AbstractRenderer;

import java.util.*;
import java.util.function.Consumer;

public class CubaTreeGrid<T> extends TreeGrid<T> implements CubaEnhancedGrid<T> {

    protected CubaGridEditorFieldFactory<T> editorFieldFactory;

    @Override
    public void setGridSelectionModel(GridSelectionModel<T> model) {
        setSelectionModel(model);
    }

    @Override
    protected CubaTreeGridState getState() {
        return (CubaTreeGridState) super.getState();
    }

    @Override
    protected CubaTreeGridState getState(boolean markAsDirty) {
        return (CubaTreeGridState) super.getState(markAsDirty);
    }

    @Override
    public Map<String, String> getColumnIds() {
        return getState().columnIds;
    }

    @Override
    public void setColumnIds(Map<String, String> ids) {
        getState().columnIds = ids;
    }

    @Override
    public void addColumnId(String column, String value) {
        if (getState().columnIds == null) {
            getState().columnIds = new HashMap<>();
        }

        getState().columnIds.put(column, value);
    }

    @Override
    public void removeColumnId(String column) {
        if (getState().columnIds != null) {
            getState().columnIds.remove(column);
        }
    }

    @Override
    public void repaint() {
        markAsDirtyRecursive();
        getDataCommunicator().reset();
    }

    @Override
    protected <V, P> Column<T, V> createColumn(ValueProvider<T, V> valueProvider,
                                               ValueProvider<V, P> presentationProvider,
                                               AbstractRenderer<? super T, ? super P> renderer) {
        return new CubaGridColumn<>(valueProvider, presentationProvider, renderer);
    }

    @Override
    public CubaGridEditorFieldFactory<T> getCubaEditorFieldFactory() {
        return editorFieldFactory;
    }

    @Override
    public void setCubaEditorFieldFactory(CubaGridEditorFieldFactory<T> editorFieldFactory) {
        this.editorFieldFactory = editorFieldFactory;
    }

    @Override
    protected Editor<T> createEditor() {
        return new CubaEditorImpl<>(getPropertySet());
    }

    @Override
    public CubaEditorField<?> getColumnEditorField(T bean, Column<T, ?> column) {
        return editorFieldFactory.createField(bean, column);
    }

    @SuppressWarnings("unchecked")
    public int getLevel(T item) {
        HierarchicalDataProvider<T, ?> dataProvider = getDataProvider();
        if (!(dataProvider instanceof EnhancedHierarchicalDataProvider)) {
            throw new IllegalStateException(
                    "Data provider must implement com.haulmont.cuba.web.widgets.data.EnhancedHierarchicalDataProvider"
            );
        }
        return ((EnhancedHierarchicalDataProvider<T>) dataProvider).getLevel(item);
    }

    public void expandItemWithParents(T item) {
        List<T> itemsToExpand = new ArrayList<>();

        T current = item;
        while (current != null) {
            itemsToExpand.add(current);
            current = getParentItem(current);
        }

        expand(itemsToExpand);
    }

    @SuppressWarnings("unchecked")
    protected T getParentItem(T item) {
        return ((EnhancedHierarchicalDataProvider<T>) getDataProvider()).getParent(item);
    }

    @Override
    public void setBeforeRefreshHandler(Consumer<T> beforeRefreshHandler) {
        getDataCommunicator().setBeforeRefreshHandler(beforeRefreshHandler);
    }
}