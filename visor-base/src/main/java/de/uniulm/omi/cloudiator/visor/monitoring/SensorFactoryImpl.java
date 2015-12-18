/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.monitoring;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by daniel on 15.01.15.
 */
public class SensorFactoryImpl implements SensorFactory {
	
	private final Map<URL, ClassLoader> loaders = new WeakHashMap<URL, ClassLoader>();

	@Override
	public final Sensor fromUriSource(String remoteURI, String className) throws SensorNotFoundException, SensorInitializationException {
		checkNotNull(remoteURI);
        checkArgument(!remoteURI.isEmpty());
		return fromAnySource(remoteURI, className);
	}
	
    @Override
    public final Sensor from(String className) throws SensorNotFoundException, SensorInitializationException {
    	return fromAnySource(null, className); 
    }
    
    protected Sensor fromAnySource(String uri, String className) throws SensorNotFoundException, SensorInitializationException {
    	checkNotNull(className);
        checkArgument(!className.isEmpty());
        
        final ClassLoader loader = getClassLoader(uri);
        checkNotNull(loader);
		
        return this.loadAndInitializeSensor(className, loader);
    }

    protected final Sensor loadAndInitializeSensor(String className, ClassLoader loader) throws SensorNotFoundException, SensorInitializationException {
        try {
            Sensor sensor = (Sensor) loader.loadClass(className).newInstance();
            sensor.init();
            return sensor;
        } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new SensorNotFoundException("Could not load sensor with name " + className, e);
        }
    }
    
    private ClassLoader getClassLoader(String remoteUri) throws SensorNotFoundException {
    	if(null == remoteUri || remoteUri.isEmpty()) {
    		return SensorFactoryImpl.class.getClassLoader();
    	}
    	// FIXME: add checks that this is really a remote uri 
    	// (e.g. starting with http and not a link to the local
    	// file system
    	try {
    		URL url = new URL(remoteUri);
    		return getRemoteClassLoader(url);
    	} catch (MalformedURLException ex) {
    		throw new SensorNotFoundException("could not create class loader", ex);
    	}
    }
    
    private ClassLoader getRemoteClassLoader(URL url){
    	checkNotNull(url);
    	synchronized(loaders) {
    		ClassLoader cl = loaders.get(url);
    		if(cl != null) {
    			return cl; 
    		}
    		return initAndRegisterURLClassLoader(url);
    	}
    }
    
    /* can only be called with lock on loaders */
    private URLClassLoader initAndRegisterURLClassLoader(URL url){
		URLClassLoader urlCl = new URLClassLoader(new URL[]{url}, SensorFactoryImpl.class.getClassLoader());
		loaders.put(url, urlCl);
		return urlCl;
    }

}
