/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.web.widgets.client.tableshared;

import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.UIDL;

public class TableNoDataPanel implements EventListener {

    protected Runnable linkClickHandler;

    protected DivElement container;
    protected SpanElement reasonLabel;
    protected DivElement actionLink;

    protected boolean useClickHandler = false;
    protected boolean htmlEnabled = false;

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
        setHtmlEnabled(uidl.getBooleanAttribute("htmlEnabled"));
        setNoDataMessage(uidl.getStringAttribute("noDataMessage"));
        setNoDataLinkMessage(uidl.getStringAttribute("noDataLinkMessage"));

        useClickHandler = uidl.getBooleanAttribute("linkClickHandler");
    }

    public void setHtmlEnabled(boolean htmlEnabled) {
        this.htmlEnabled = htmlEnabled;
    }

    public void setNoDataMessage(String message) {
        if (htmlEnabled) {
            reasonLabel.setInnerHTML(message);
        } else {
            reasonLabel.setInnerText(message);
        }
    }

    public void setNoDataLinkMessage(String message) {
        if (htmlEnabled) {
            actionLink.setInnerHTML(message);
        } else {
            actionLink.setInnerText(message);
        }
    }

    public void setLinkClickHandler(Runnable linkClickHandler) {
        this.linkClickHandler = linkClickHandler;
    }

    public Element getElement() {
        return container;
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (event.getTypeInt() == Event.ONCLICK) {
            Element fromElement = Element.as(event.getEventTarget());

            if (actionLink.isOrHasChild(fromElement)
                    && linkClickHandler != null
                    && useClickHandler) {
                linkClickHandler.run();
            }
        }
    }
}
