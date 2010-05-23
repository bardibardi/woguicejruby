package org.bardibardi.jruby.guice;

import com.google.inject.Module;
import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * The JRuby engine configures itself
 * by the use of java properties with the same names
 * as the arguments to Names.named called in the configure
 * method. This module exists so that Guice can be
 * used to configure those java properties.
 * Precisely what the properties are used for is
 * not completely clear.
 * <p>
 * The settings here work with jruby-complete-1.1.2.jar
 * (NB: In order to use rubygems, one must use a normal
 * jruby installation)
 * <p>
 * Ignored setting:
 * jruby.base - ~/.jruby other .rb files, gems ?
 * <p>
 * Ignored setting:
 * jruby.home - JRuby directory, location of bin directory?
 * <p>
 * Ignored setting:
 * jruby.lib - directory for additional jars for JRuby
 * <p>
 * Not necessary setting(?):
 * jruby.script - name of JRuby shell script
 * <p>
 * jruby.shell - the shell to run (`shell command`)
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class JRubyCompleteModule implements Module {
	static String JRUBY_BASE = System.getProperty("user.home") + "/.jruby";
	static String JRUBY_HOME = "/home/bardi/Desktop/jruby";
	static String JRUBY_LIB = JRUBY_HOME + "/lib";
	static String JRUBY_SCRIPT = "jruby";
	static String JRUBY_SHELL = "/bin/bash";
	/**
	 * configure / add Guice bindings
	 * 
	 * @param b Binder, a Guice Binder
	 */
    public void configure(Binder b) {
		b.bindConstant().annotatedWith(Names.named("jruby.base")).to(JRUBY_BASE);
		b.bindConstant().annotatedWith(Names.named("jruby.home")).to(JRUBY_HOME);
		b.bindConstant().annotatedWith(Names.named("jruby.lib")).to(JRUBY_LIB);
		b.bindConstant().annotatedWith(Names.named("jruby.script")).to(JRUBY_SCRIPT);
		b.bindConstant().annotatedWith(Names.named("jruby.shell")).to(JRUBY_SHELL);
    }
}
