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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomee.myfaces;

import org.apache.myfaces.shared.util.ClassUtils;
import org.apache.myfaces.spi.FacesConfigResourceProvider;
import org.apache.myfaces.spi.ServiceProviderFinderFactory;
import org.apache.myfaces.spi.impl.DefaultFacesConfigResourceProviderFactory;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TomEEFacesConfigResourceProviderFactory extends DefaultFacesConfigResourceProviderFactory {
    private Logger getLogger() {
        return Logger.getLogger(TomEEFacesConfigResourceProviderFactory.class.getName());
    }

    @Override
    public FacesConfigResourceProvider createFacesConfigResourceProvider(final ExternalContext externalContext) {
        FacesConfigResourceProvider returnValue = null;
        try {
            if (System.getSecurityManager() != null) {
                returnValue = AccessController.doPrivileged(new PrivilegedExceptionAction<FacesConfigResourceProvider>() {
                    public FacesConfigResourceProvider run() throws Exception {
                        return resolveFacesConfigResourceProviderFromService(externalContext);
                    }
                });
            } else {
                returnValue = resolveFacesConfigResourceProviderFromService(externalContext);
            }
        } catch (final ClassNotFoundException e) {
            // ignore
        } catch (final NoClassDefFoundError e) {
            // ignore
        } catch (final InstantiationException e) {
            getLogger().log(Level.SEVERE, "", e);
        } catch (final IllegalAccessException e) {
            getLogger().log(Level.SEVERE, "", e);
        } catch (final InvocationTargetException e) {
            getLogger().log(Level.SEVERE, "", e);
        } catch (final PrivilegedActionException e) {
            throw new FacesException(e);
        }
        return returnValue;
    }

    private FacesConfigResourceProvider resolveFacesConfigResourceProviderFromService(final ExternalContext externalContext)
            throws ClassNotFoundException, NoClassDefFoundError, InstantiationException,
            IllegalAccessException, InvocationTargetException, PrivilegedActionException {
        List<String> classList = (List<String>) externalContext.getApplicationMap().get(FACES_CONFIG_PROVIDER_LIST);
        if (classList == null) {
            classList = ServiceProviderFinderFactory.getServiceProviderFinder(externalContext).
                    getServiceProviderList(FACES_CONFIG_PROVIDER);
            externalContext.getApplicationMap().put(FACES_CONFIG_PROVIDER_LIST, classList);
        }
        return ClassUtils.buildApplicationObject(FacesConfigResourceProvider.class, classList, new TomEEFacesConfigResourceProvider());
    }
}
