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
    protected SpanElement messageLabel;
    protected DivElement linkMessageLabel;

    public TableNoDataPanel() {
        container = Document.get().createDivElement();
        container.setClassName("c-table-nodata-panel");

        messageLabel = Document.get().createSpanElement();
        messageLabel.setClassName("c-table-nodata-panel-message");
        container.appendChild(messageLabel);

        linkMessageLabel = Document.get().createDivElement();
        linkMessageLabel.setClassName("c-table-nodata-panel-link");
        container.appendChild(linkMessageLabel);

        Event.sinkEvents(container, Event.ONCLICK);
        Event.setEventListener(container, this);
    }

    public void setNoDataMessage(String message) {
        messageLabel.setInnerText(message);
    }

    public void setNoDataLinkMessage(String message) {
        linkMessageLabel.setInnerText(message);
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

            if (linkMessageLabel.isOrHasChild(fromElement)
                    && linkClickHandler != null
                    && !linkMessageLabel.getInnerText().isEmpty()) {
                linkClickHandler.run();
            }
        }
    }
}
