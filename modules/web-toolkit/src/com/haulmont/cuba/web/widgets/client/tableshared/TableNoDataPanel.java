/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.web.widgets.client.tableshared;

import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class TableNoDataPanel implements EventListener {

    protected Runnable linkClickHandler;

    protected DivElement container;
    protected DivElement messageBox;
    protected DivElement messageLabel;

    protected SpanElement linkMessageLabel;
    protected SpanElement linkShortcutLabel;

    public TableNoDataPanel() {
        container = Document.get().createDivElement();
        container.setClassName("c-table-nodata-panel");

        messageBox = Document.get().createDivElement();
        messageBox.setClassName("c-table-nodata-panel-message-box");

        messageLabel = Document.get().createDivElement();
        messageLabel.setClassName("c-table-nodata-panel-message");
        messageBox.appendChild(messageLabel);

        container.appendChild(messageBox);

        linkMessageLabel = Document.get().createSpanElement();
        linkMessageLabel.setClassName("c-table-nodata-panel-link-message v-button-link");

        linkShortcutLabel = Document.get().createSpanElement();
        linkShortcutLabel.setClassName("c-table-nodata-panel-link-shortcut");

        Event.sinkEvents(container, Event.ONCLICK);
        Event.setEventListener(container, this);
    }

    public void setNoDataMessage(String message) {
        messageLabel.setInnerText(message);
    }

    public void setNoDataLinkMessage(String message) {
        if (message == null || message.isEmpty()) {
            linkMessageLabel.removeFromParent();
        } else {
            linkMessageLabel.setInnerText(message);
            if (!isAddedToMessageBox(linkMessageLabel)) {
                messageBox.appendChild(linkMessageLabel);
            }
        }
    }

    public void setNoDataLinkShortcut(String shortcut) {
        if (shortcut == null || shortcut.isEmpty()) {
            linkShortcutLabel.removeFromParent();
        } else {
            linkShortcutLabel.setInnerText(shortcut);
            if (!isAddedToMessageBox(linkShortcutLabel)) {
                messageBox.appendChild(linkShortcutLabel);
            }
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

            if (linkMessageLabel.isOrHasChild(fromElement)
                    && linkClickHandler != null
                    && isAddedToMessageBox(linkMessageLabel)) {
                linkClickHandler.run();
            }
        }
    }

    protected boolean isAddedToMessageBox(Element element) {
        return element.getParentElement().equals(messageBox);
    }
}
