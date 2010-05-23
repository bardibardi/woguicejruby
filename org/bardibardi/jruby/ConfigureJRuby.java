package org.bardibardi.jruby;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.Singleton;

/**
 * Generalize configuration of JRuby with Guice.
 * <p>
 * This is a bit strange, because the JRuby configuration
 * requires the use of java properties. General configuration
 * does not assume the use of java properties. So quite possibly
 * the same java properties are read, then injected by Guice
 * and then set to the values they had in the first place.
 * <p>
 * In addition, when one uses a jruby complete jar the only
 * setting that possibly gets used is jruby.shell 
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
@Singleton
public class ConfigureJRuby {
//	System.setProperty("jruby.base", "/home/bardi/Desktop/jruby");
	@Inject @Named("jruby.base") String jrubyBase;
//	System.setProperty("jruby.home", "/home/bardi/Desktop/jruby");
	@Inject @Named("jruby.home") String jrubyHome;
//	System.setProperty("jruby.lib", "/home/bardi/Desktop/jruby/lib");
	@Inject @Named("jruby.lib") String jrubyLib;
	@Inject @Named("jruby.script") String jrubyScript;
	@Inject @Named("jruby.shell") String jrubyShell;
	
	/**
	 * configure JRuby
	 */
    public void configure() {
    	System.setProperty("jruby.base", jrubyBase);
    	System.setProperty("jruby.home", jrubyHome);
    	System.setProperty("jruby.lib", jrubyLib);
    	System.setProperty("jruby.script", jrubyScript);
    	System.setProperty("jruby.shell", jrubyShell);
    }
}
