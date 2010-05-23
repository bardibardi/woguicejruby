package org.bardibardi.jruby.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Binder;

import org.bardibardi.guice.IInjectorFactory;
import org.bardibardi.jruby.guice.RubyAppGuiceModule;

/**
 * Class to be dynamically loaded by 
 * org.bardibardi.jruby.test.JRubyTest.java for the purpose
 * of configuring JRuby.
 * <p>
 * In an regular application, static initializers
 * of a similar class can be used to read in configuration
 * information from property files and the like. Or,
 * configuration information could be put directly into
 * static members. (Dynamic loading implies property files
 * are really not necessary.)
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class JRubyTestInjectorFactory implements IInjectorFactory {
	static Injector i;
	/**
	 * Get the Guice Injector that knows how to create
	 * a org.bardibardi.jruby.RubyApp.
	 */
	public Injector getInjector() {
		if (null != i) {
			return i;
		}
		ApplicationGuiceModule g = new ApplicationGuiceModule();
		return Guice.createInjector(g);
	}
/**
 * Guice Module with binding information for the creation
 * of an org.bardibardi.jruby.RubyApp.
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
static class ApplicationGuiceModule implements Module {
	/**
	 * Configure the bindings necessary for creating
	 * an org.bardibardi.jruby.RubyApp.
	 * 
	 * @param b, Binder, Guice Binder
	 */
	public void configure(Binder b) {
		b.install(new RubyAppGuiceModule());
	}
} // ApplicationGuiceModule
} // JRubyTestInjectorFactory
