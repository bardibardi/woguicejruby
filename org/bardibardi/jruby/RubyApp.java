package org.bardibardi.jruby;

import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The RubyApp (via IRubyO) is meant to be the
 * starting point for using JRuby from java.
 * A Guice Injector is used to construct/inject
 * a singleton RubyApp when creating an IRubyO.
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
@Singleton
public class RubyApp extends RubyO implements IRubyO {
	/**
	 * The RubyApp constructor is designed to be called
	 * by a Guice Injector exactly once.
	 * It creates ruby, a global JRuby engine, gets nil, the ruby
	 * nil and gets topSelf, the JRuby engine's top level object.
	 * The icra parameter is used to configure the 
	 * JRuby engines load path ($:) and to get the script
	 * which is run to create the wrapped IRubyObject.
	 * 
	 * @param icra, IConfigureRubyApp injected by Guice
	 */
    @Inject
    public RubyApp(IConfigureRubyApp icra) {
    	super(null); // Java nonsense
        ruby = JavaEmbedUtils.initialize(icra.loadPathAdditions());
        // script is an engine method -- does not require robj
        nil = script("nil");
        // script is an engine method -- does not require robj
        IRubyObject ts = script("self");
        // script is an engine method -- does not require robj
        robj = script(icra.appScript());
        // If this (java this) is wrapping the top level ruby object,
        // do not create an extra IRubyO.
        topSelf = ts.equals(robj) ? this : rubyO(ts);
    }
} // RubyApp
