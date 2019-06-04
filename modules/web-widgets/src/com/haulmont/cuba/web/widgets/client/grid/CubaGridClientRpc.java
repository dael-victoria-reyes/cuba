/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.web.widgets.client.grid;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

public interface CubaGridClientRpc extends ClientRpc {

    @NoLayout
    void showNoDataPanelLink(boolean show);
}
