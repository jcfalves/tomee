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
package org.apache.openejb.jee.wls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;


/**
 * <p>Java class for work-manager-shutdown-trigger complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="work-manager-shutdown-trigger">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="max-stuck-thread-time" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="stuck-thread-count" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "work-manager-shutdown-trigger", propOrder = {
    "maxStuckThreadTime",
    "stuckThreadCount"
})
public class WorkManagerShutdownTrigger {

    @XmlElement(name = "max-stuck-thread-time")
    protected BigInteger maxStuckThreadTime;
    @XmlElement(name = "stuck-thread-count", required = true)
    protected BigInteger stuckThreadCount;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;

    /**
     * Gets the value of the maxStuckThreadTime property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getMaxStuckThreadTime() {
        return maxStuckThreadTime;
    }

    /**
     * Sets the value of the maxStuckThreadTime property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setMaxStuckThreadTime(final BigInteger value) {
        this.maxStuckThreadTime = value;
    }

    /**
     * Gets the value of the stuckThreadCount property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getStuckThreadCount() {
        return stuckThreadCount;
    }

    /**
     * Sets the value of the stuckThreadCount property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setStuckThreadCount(final BigInteger value) {
        this.stuckThreadCount = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(final String value) {
        this.id = value;
    }

}
