package org.bardibardi.jruby;

import java.util.List;

import org.jruby.javasupport.JavaEmbedUtils;

/**
 * To configure RubyApp
 * <code>
 * IConfigureRubyApp icra;
 * ...
 * ruby = JavaEmbedUtils.initialize(icra.loadPathAdditions());
 * IRubyObject robj;
 * ...
 * robj = script(icra.appScript());
 * </code>
 *
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public interface IConfigureRubyApp {
	/**
	 * configure the JRuby engine's load path
	 * @return List, (of Strings) to add to $:
	 */
    public List loadPathAdditions();
    /**
     * get script whose result is the IRubyObject
     * wrapped by the RubyApp instance
     * 
     * @return String, ruby script
     */
    public String appScript();
}
