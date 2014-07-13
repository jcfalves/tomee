/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.openejb.jee.oejb3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"description", "queryMethod", "objectQl"})
@XmlRootElement(name = "query")
public class Query {

    protected String description;
    @XmlElement(name = "query-method", required = true)
    protected QueryMethod queryMethod;
    @XmlElement(name = "object-ql", required = true)
    protected String objectQl;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String value) {
        this.description = value;
    }

    public QueryMethod getQueryMethod() {
        return queryMethod;
    }

    public void setQueryMethod(final QueryMethod value) {
        this.queryMethod = value;
    }

    public String getObjectQl() {
        return objectQl;
    }

    public void setObjectQl(final String value) {
        this.objectQl = value;
    }

}
