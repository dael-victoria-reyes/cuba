/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */

package com.haulmont.cuba.web.widgets.grid;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.Editor;
import com.vaadin.ui.components.grid.EditorCancelEvent;

import java.util.Map;

public class CubaEditorCancelEvent<T> extends EditorCancelEvent<T> {

    protected Map<Grid.Column<T, ?>, Component> columnFieldMap;

    /**
     * Constructor for a editor cancel event.
     *
     * @param editor the source of the event
     * @param bean   the bean being edited
     */
    public CubaEditorCancelEvent(Editor<T> editor, T bean, Map<Grid.Column<T, ?>, Component> columnFieldMap) {
        super(editor, bean);
        this.columnFieldMap = columnFieldMap;
    }

    /**
     * @return a mapping of field to columns
     */
    public Map<Grid.Column<T, ?>, Component> getColumnFieldMap() {
        return columnFieldMap;
    }
}
