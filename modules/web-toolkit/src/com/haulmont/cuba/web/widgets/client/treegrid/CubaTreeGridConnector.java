package com.haulmont.cuba.web.widgets.client.treegrid;

import com.haulmont.cuba.web.widgets.CubaTreeGrid;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.treegrid.TreeGridConnector;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;
import elemental.json.JsonObject;

import java.util.List;

@Connect(CubaTreeGrid.class)
public class CubaTreeGridConnector extends TreeGridConnector {

    @Override
    public CubaTreeGridWidget getWidget() {
        return (CubaTreeGridWidget) super.getWidget();
    }

    @Override
    public CubaTreeGridState getState() {
        return (CubaTreeGridState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);

        if (event.hasPropertyChanged("showNoDataPanel")) {
            getWidget().showNoDataPanel(getState().showNoDataPanel);
            if (getState().showNoDataPanel) {
                // as noDataPanel can be recreated set all messages
                getWidget().getNoDataPanel().setNoDataMessage(getState().noDataMessage);
                getWidget().getNoDataPanel().setNoDataLinkMessage(getState().noDataLinkMessage);
                getWidget().getNoDataPanel().setNoDataLinkShortcut(getState().noDataLinkShortcut);
                getWidget().getNoDataPanel().setLinkClickHandler(getWidget().noDataPanelLinkClickHandler);
            }
        }
        if (event.hasPropertyChanged("noDataMessage")) {
            if (getWidget().getNoDataPanel() != null) {
                getWidget().getNoDataPanel().setNoDataMessage(getState().noDataMessage);
            }
        }
        if (event.hasPropertyChanged("noDataLinkMessage")) {
            if (getWidget().getNoDataPanel() != null) {
                getWidget().getNoDataPanel().setNoDataLinkMessage(getState().noDataLinkMessage);
            }
        }
        if (event.hasPropertyChanged("noDataLinkShortcut")) {
            if (getWidget().getNoDataPanel() != null) {
                getWidget().getNoDataPanel().setNoDataLinkShortcut(getState().noDataLinkShortcut);
            }
        }
    }

    @Override
    protected void updateColumns() {
        super.updateColumns();

        if (getWidget().getColumnIds() != null) {
            getWidget().setColumnIds(null);
        }

        if (getState().columnIds != null) {
            List<Grid.Column<?, JsonObject>> currentColumns = getWidget().getColumns();

            for (Grid.Column<?, JsonObject> column : currentColumns) {
                String id = getColumnId(column);
                if (getState().columnIds.containsKey(id)) {
                    getWidget().addColumnId(column, getState().columnIds.get(id));
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        getWidget().noDataPanelLinkClickHandler = () -> getRpcProxy(CubaTreeGridServerRpc.class).onNoDataPanelLinkClick();
    }
}
