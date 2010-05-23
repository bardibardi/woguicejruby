package org.bardibardi.jruby;

import java.util.List;
import java.util.ArrayList;

import com.google.inject.Inject;
import com.google.inject.name.Named;
/**
 * For configuring the loadpath ($:) for JRuby and
 * configuring the location of the .rb file to run
 * and the name of the ruby class whose instance
 * is the org.bardibardi.jruby.RubyApp being configured.
 * The configuration parameters injected by Guice
 * are optional. They are to used so that when they
 * are null / don't exist, Guice will inject "", the
 * empty String.
 * <p>
 * The behavior of the configuration parameters:
 * <p>
 * "ruby.app.file.directory" prefixed to the ruby loadpath
 * if it exists
 * <p>
 * "ruby.app.file.name.without.rb.extension" will be
 * required if it exists
 * <p>
 * When "ruby.app.class.name" exists, the ruby app will
 * effectively be an instance of the specified class.
 * Otherwise, the ruby app will effectively be an instance
 * of the ruby top level object.
 * "ruby.app.load.path.additions", if it exists, will be
 * prefixed to the ruby loadpath
 * (follows "ruby.app.file.directory")
 * <p>
 * NB: The java classpath is not added to the loadpath
 * ($:) by JRuby. (The BSF stuff does add it.)
 * <p>
 * The code here explicitly prefixes the java.class.path
 * to the ruby loadpath (follows "ruby.app.file.directory"
 * and "ruby.app.load.path.additions")
 * `pwd` is like /Developer/workspace/woisme/build/woisme.woa
 * <p>
 * if `pwd` is PWD, a typical default loadpath ($:) is
 * <code>
 * PWD/lib/ruby/site_ruby/1.8
 * :PWD/lib/ruby/site_ruby
 * :PWD/lib/ruby/1.8
 * :PWD/lib/ruby/1.8/java
 * :lib/ruby/1.8
 * :.
 * </code>
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class ConfigureRubyApp implements IConfigureRubyApp {
	@Inject @Named("ruby.app.file.directory") String rubyAppFileDirectory;
	@Inject @Named("ruby.app.file.name.without.rb.extension") String rubyAppFileNameWithoutRbExtension;
	@Inject @Named("ruby.app.class.name") String rubyAppClassName;
	@Inject @Named("ruby.app.load.path.additions") String rubyAppLoadPathAdditions;
    /**
     * constructor used by Guice Injector
     * 
     * @param cjr, ConfigureJRuby, used to configure JRuby java property settings
     */
    @Inject
    public ConfigureRubyApp(ConfigureJRuby cjr) {
    	cjr.configure();
    }
    /**
     * add paths to an ArrayList for use with
     * manipulation of the ruby load path ($:).
     * NB: potential path entries of null and
     * "", the empty String are skipped.
     * 
     * @param al, ArrayList (of Strings) to add to
     * @param path, String, path.separator delimited path
     */
    static void addPath(ArrayList al, String path) {
    	if (null == path) {
    		return;
    	}
    	if ("" == path) {
    		return;
    	}
    	String separator = System.getProperty("path.separator");
    	String[] aPath = path.split(separator);
    	for (String s : aPath) {
    		al.add(s);
    	}
    }
    /**
     * return List (of String) to add to the JRuby loadpath ($:)
     * 
     * @return List (of String)
     */
    public List loadPathAdditions() {
    	ArrayList al = new ArrayList();
        addPath(al, rubyAppFileDirectory);
    	addPath(al, rubyAppLoadPathAdditions);
    	addPath(al, System.getProperty("java.class.path"));
    	return al;
    }
    /**
     * return script executed to get internal IRubyObject of the
     * starting/first IRubyO (called ruby app).
     * NB: "" as the java "ruby.app.class.name" results in the "self"
     * script which returns the ruby top level object.
     * NB: "" is returned when the 
     * 
     * @return String, ruby script
     */
    public String appScript() {
    	if ("" == rubyAppFileNameWithoutRbExtension) {
    		if ("" == rubyAppClassName) {
    		    return "self";
    		}
    		else {
    			return rubyAppClassName +
    			    ".new\n + self";
    		}
    	}
    	String result = "require '" +
            rubyAppFileNameWithoutRbExtension +
            "'\n";
    	if ("" == rubyAppClassName) {
    		result += "self";
    	}
    	else {
    		result += rubyAppClassName + ".new";
    	}
    	return result;
    }
 }
