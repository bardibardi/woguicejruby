package org.bardibardi.guice;

import com.google.inject.Injector;

/**
 * Get a Guice Injector
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public interface IInjectorFactory {
	/**
	 * Get a Guice Injector
	 * 
	 * @return Injector, a Guice Injector
	 */
    public Injector getInjector();
}
