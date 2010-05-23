package org.bardibardi.guice;

/**
 * Used to remove compilation dependency on specific
 * Guice Injector's.
 * Used to dynamically load an IInjectorFactory.
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class GetInjectorFactory {
	/**
	 * Dynamically load an IInjectorFactory
	 * 
	 * @param p, A String, java property naming IInjectorFactory implementor
	 * @return IInjectorFactory, Injector
	 */
    public static IInjectorFactory fromProperty(String p) {
    	String className = System.getProperty(p);
    	IInjectorFactory iif = null;
    	try {
    	    Class c = Class.forName(className);
    	    iif = (IInjectorFactory)c.newInstance();
    	}
    	catch (Exception e) {
    		;
    	}
    	return iif;
    }
} // GetInjectorFactory
