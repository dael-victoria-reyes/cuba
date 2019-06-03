/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.web.widgets.client.grid;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class CubaGridNoDataPanel implements EventListener {

    protected Runnable linkClickHandler;

    protected DivElement container;
    protected DivElement messageBox;
    protected DivElement messageLabel;

    protected SpanElement linkMessageLabel;
    protected SpanElement linkShortcutLabel;

    public CubaGridNoDataPanel() {
        container = Document.get().createDivElement();
        container.setClassName("c-datagrid-nodata-panel");

        messageBox = Document.get().createDivElement();
        messageBox.setClassName("c-datagrid-nodata-panel-message-box");

        messageLabel = Document.get().createDivElement();
        messageLabel.setClassName("c-datagrid-nodata-panel-message");
        messageBox.appendChild(messageLabel);

        linkMessageLabel = Document.get().createSpanElement();
        linkMessageLabel.setClassName("c-datagrid-nodata-panel-link-message v-button-link");
        messageBox.appendChild(linkMessageLabel);

        linkShortcutLabel = Document.get().createSpanElement();
        linkShortcutLabel.setClassName("c-datagrid-nodata-panel-link-shortcut");
        messageBox.appendChild(linkShortcutLabel);

        container.appendChild(messageBox);

        Event.sinkEvents(container, Event.ONCLICK);
        Event.setEventListener(container, this);
    }

    public void setNoDataMessage(String message) {
        messageLabel.setInnerText(message);
    }

    public void setNoDataLinkMessage(String message) {
        linkMessageLabel.setInnerText(message);
    }

    public void setNoDataLinkShortcut(String shortcut) {
        linkShortcutLabel.setInnerText(shortcut);
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
                    && isAddedToMessageBox(linkMessageLabel)) {
                linkClickHandler.run();
            }
        }
    }

    protected boolean isAddedToMessageBox(Element element) {
        Element parent = element.getParentElement();
        return parent != null && parent.equals(messageBox);
    }
}
