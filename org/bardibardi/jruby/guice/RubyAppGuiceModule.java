package org.bardibardi.jruby.guice;

import com.google.inject.Module;
import com.google.inject.Binder;
import org.bardibardi.guice.SystemProperty;

import org.bardibardi.jruby.ConfigureRubyApp;
import org.bardibardi.jruby.IConfigureRubyApp;
import org.bardibardi.jruby.IRubyO;
import org.bardibardi.jruby.RubyApp;

/**
 * set up Guice injection of all configuration necessary to
 * use JRuby from java with a designated ruby application
 * script.
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class RubyAppGuiceModule implements Module {
	/**
     * set up Guice injection of all configuration necessary to
     * use JRuby from java with a designated ruby application
     * script.
	 * <p>
	 * Look at the javadoc of org.bardibardi.jruby.ConfigureRubyApp to see
	 * precisely how the ruby.app... java properties are used.
	 * 
	 * @param b, Binder, Guice Binder
	 */
    public void configure(Binder b) {
		b.install(new JRubyCompleteModule());
		SystemProperty.bind(b, "ruby.app.file.directory");
		SystemProperty.bind(b, "ruby.app.file.name.without.rb.extension");
		SystemProperty.bind(b, "ruby.app.class.name");
		SystemProperty.bind(b, "ruby.app.load.path.additions");
		b.bind(IConfigureRubyApp.class).to(ConfigureRubyApp.class);
		b.bind(IRubyO.class).to(RubyApp.class);
    }
}
