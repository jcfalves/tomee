/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openejb.jee.sun;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mappingProperties",
    "isOneOneCmp",
    "oneOneFinders",
    "prefetchDisabled"
})
public class Cmp {
    @XmlElement(name = "mapping-properties")
    protected String mappingProperties;
    @XmlElement(name = "is-one-one-cmp")
    protected String isOneOneCmp;
    @XmlElement(name = "one-one-finders")
    protected OneOneFinders oneOneFinders;
    @XmlElement(name = "prefetch-disabled")
    protected PrefetchDisabled prefetchDisabled;

    public String getMappingProperties() {
        return mappingProperties;
    }

    public void setMappingProperties(final String value) {
        this.mappingProperties = value;
    }

    public String getIsOneOneCmp() {
        return isOneOneCmp;
    }

    public void setIsOneOneCmp(final String value) {
        this.isOneOneCmp = value;
    }

    public OneOneFinders getOneOneFinders() {
        return oneOneFinders;
    }

    public void setOneOneFinders(final OneOneFinders value) {
        this.oneOneFinders = value;
    }

    public PrefetchDisabled getPrefetchDisabled() {
        return prefetchDisabled;
    }

    public void setPrefetchDisabled(final PrefetchDisabled value) {
        this.prefetchDisabled = value;
    }
}
