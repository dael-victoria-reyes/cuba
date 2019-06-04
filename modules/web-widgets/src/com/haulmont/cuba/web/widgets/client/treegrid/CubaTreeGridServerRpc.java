/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.web.widgets.client.treegrid;

import com.vaadin.shared.communication.ServerRpc;

public interface CubaTreeGridServerRpc extends ServerRpc {

    void onNoDataPanelLinkClick();
}
