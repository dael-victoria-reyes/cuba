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

package com.haulmont.cuba.core.sys.javacl;

import com.haulmont.cuba.core.sys.javacl.compiler.CharSequenceCompiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Intended to store compiled classes bytecode generated after compilation
 * in {@link JavaClassLoader}.
 *
 * @see JavaClassLoader
 * @see CharSequenceCompiler
 */
public class CompiledClass {

    protected final String fqn;
    protected final byte[] byteCode;

    public CompiledClass(String fqn, byte[] byteCode) {
        this.fqn = fqn;
        this.byteCode = byteCode;
    }

    public String getFqn() {
        return fqn;
    }

    public byte[] getByteCode() {
        return byteCode;
    }

    public InputStream getByteCodeAsStream() {
        return new ByteArrayInputStream(byteCode);
    }
}
