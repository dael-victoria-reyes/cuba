/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.web.widgets.client.tableshared;

import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.UIDL;
import com.vaadin.client.WidgetUtil;

public class TableNoDataPanel implements EventListener {

    protected DivElement container;

    protected SpanElement reasonLabel;
    protected DivElement actionLink;

    public TableNoDataPanel() {
        container = Document.get().createDivElement();
        container.setClassName("c-table-nodata-panel");

        reasonLabel = Document.get().createSpanElement();
        reasonLabel.setClassName("c-table-nodata-panel-reason");
        container.appendChild(reasonLabel);

        actionLink = Document.get().createDivElement();
        actionLink.setClassName("c-table-nodata-panel-action");
        container.appendChild(actionLink);

        Event.sinkEvents(container, Event.ONCLICK);
        Event.setEventListener(container, this);
    }

    public void updateFromUIDL(UIDL uidl) {
        String reasonMsg = uidl.getStringAttribute("reasonMsg");
        if (reasonMsg != null) {
            reasonLabel.setInnerHTML(WidgetUtil.escapeHTML(reasonMsg));
        }

        String actionMsg = uidl.getStringAttribute("actionMsg");
        if (actionMsg != null) {
            actionLink.setInnerHTML(WidgetUtil.escapeHTML(actionMsg));
        }
    }

    public Element getElement() {
        return container;
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (event.getTypeInt() == Event.ONCLICK) {
            Element fromElement = Element.as(event.getEventTarget());

            if (actionLink.isOrHasChild(fromElement)) {

            }
        }
    }
}
