/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.cuba.gui.components.mainwindow;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.HasSettings;

/**
 * A component displaying a panel with application and search folders.
 */
public interface FoldersPane extends Component.BelongToFrame, HasSettings {

    String NAME = "foldersPane";

    /**
     * Initially show the panel and load folders. Invoked by the component loader.
     */
    void loadFolders();

    /**
     * Refresh folders if the panel is visible.
     */
    void refreshFolders();
}