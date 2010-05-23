package org.bardibardi.guice;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Utility to bind a java property value to a
 * Guice @Named annotation having the value
 * equal to the name of that java property.
 * <code>
 * @Inject
 * @Named("some.property") String someProperty;
 * </code>
 * someProperty is to be injected with the value
 * of the java property "some.property".
 * NB: a java property with a value of null
 * results in a binding to "", the empty String.
 * Guice will not inject a null value.
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class SystemProperty {
	/**
	 * Bind a @Named annotation with java property.
	 *  
	 * @param b, Binder, a Guice Binder
	 * @param name, String, property name
	 */
    public static void bind(Binder b, String name) {
    	String property = System.getProperty(name);
    	if (null == property) {
    		property = "";
    	}
		b.bindConstant().annotatedWith(Names.named(name)).to(property);
    }
}
