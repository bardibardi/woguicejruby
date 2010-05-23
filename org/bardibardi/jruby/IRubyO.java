package org.bardibardi.jruby;

import org.jruby.runtime.builtin.IRubyObject;
/**
 * The interface IRubyO is intended to provide
 * a complete java interface to a single global
 * JRuby engine and an internal IRubyObject
 * instance.
 * IRubyO should be used as a complete interface
 * to ruby as provided by JRuby.
 * <p>
 * IRubyObject is JRuby's representation for all
 * ruby objects.
 * <p>
 * Methods referred to as engine methods do not
 * use the IRubyObject wrapped by an IRubyO.
 * <p>
 * The two fundamental methods are:
 * <code>
 * public IRubyObject script(String script);
 * public IRubyObject send(String method, Object... params);
 * </code>
 * script is an engine method that runs ruby scripts.
 * send is for running a ruby method on the wrapped
 * IRubyObject.
 * <p>
 * Tested for JRuby 1.1.2
 * in org.bardibardi.jruby.test.JRubyTest.java
 * <p>
 * javaGlobal... engine methods, ruby identifiers used:
 * $java_global, java_global, java_global_set, and
 * java_global_remove
 * <p>
 * All methods can throw JRuby ruby exceptions.
 * <p>
 * NB: IRubyObject should be treated as a tag
 * interface.
 * Users of JRuby should not call any methods on
 * IRubyObject. Calling methods on IRubyObject
 * simply introduces an unnecessary dependency
 * on IRubyObject's interface -- something
 * which may change in a later version of JRuby.
 * <p>
 * In any method below which has a Class clazz
 * parameter, an instance of clazz is the Object
 * that is actually returned.
 * When used, clazz is always the first parameter.
 * JRuby attempts to return an instance of clazz
 * by doing its standard conversions. If it fails
 * an exception will be thrown. Otherwise, the
 * returned Object can be cast to clazz.
 * <code>
 *     IRubyO iro;
 *     ...
 *     Ex ex = (Ex)iro.send(Ex.class, "amethod", ...
 * </code>
 * <p>
 * JRuby's standard conversions:
 * Normal immutable types are copied/converted to
 * corresponding ruby classes breaking assertSame
 * identity.
 * java.lang.String's become ruby String's.
 * NB: those ruby String's are UTF-8 bytes.
 * Otherwise object identity is preserved.
 * A java Object is wrapped by an IRubyObject
 * when used in ruby code. When an IRubyObject
 * is unwrapped to a java Object, the java
 * Object itself has not been converted / copied.
 * 
 * @author Bardi Einarsson, bardibardi.org
 * @version 1.0, 25 June 2008
 */
public interface IRubyO {
	/**
	 * Engine method for access to JRuby engine's
	 * top level object
	 * 
	 * @return An IRubyO, JRuby engine's top level object
	 * wrapped in an IRubyO
	 */
    public IRubyO topSelf();
	/**
	 * Engine method for access to ruby nil
	 * 
	 * @return An IRubyObject, ruby nil
	 */
    public IRubyObject nil();
	/**
	 * Engine method, test for ruby nil
	 * 
	 * @param irobj An IRubyObject, Is it ruby nil?
	 * @return A boolean, nil().equals(irobj)
	 */
    public boolean isNil(IRubyObject irobj);
	/**
	 * Engine method, test for ruby nil
	 *
	 * @param iro An IRubyO, Does it wrap ruby nil?
	 * @return A boolean, nil().equals(iro.unwrap())
	 */
    public boolean isNil(IRubyO iro);
	/**
	 * Engine method, wrap an IRubyObject
	 *
	 * @param irobj An IRubyObject to be wrapped
	 * @return An IRubyO wrapping an IRubyObject
	 */
    public IRubyO rubyO(IRubyObject irobj);
	/**
	 * Engine method which puts an object into the 
	 * $java_global hash at a key if the key is unused.
	 * 
	 * @param key A String, hash key for obj
	 * @param obj An Object to be stored
	 * @return A String key if successful, else null
	 */
    public String javaGlobalSet(String key, Object obj);
	/**
	 * Engine method which gets a java Object from
	 * the $java_global hash
	 *
	 * @param clazz A Class to convert to
	 * @param key A String, hash key
	 * @return An Object, $java_global[key] 
	 * converted to clazz instance (ruby nil to null)
	 */
    public Object javaGlobal(Class clazz, String key);
	/**
	 * Engine method which gets the IRubyObject wrapping
	 * the java Object put at key by javaGlobalSet
	 * 
	 * @param key A String, hash key
	 * @return An IRubyObject, $java_global[key] (or ruby nil)
	 */
    public IRubyObject javaGlobal(String key);
	/**
	 * Engine method which removes an object from the 
	 * $java_global hash at a key
	 * 
	 * @param key A String, hash key
	 * @return A String key if the key was in use, else null
	 */
    public String javaGlobalRemove(String key);
    /**
     * Engine method which does JRuby convert to IRubyObject
     *
     * @param obj An Object to be converted
     * @return An IRubyObject from obj as converted by JRuby
     */
    public IRubyObject rubyObject(Object obj);
    /**
     * Engine method which does JRuby convert to java Object
     *
	 * @param clazz A Class to convert to
     * @param iro An IRubyO wrapping the IRubyObject to be converted
     * @return An Object, an instance of clazz as converted by JRuby
     * from the IRubyObject wrapped by iro
     */
    public Object javaObject(Class clazz, IRubyO iro);
    /**
     * Engine method which does JRuby convert to java Object
     * 
	 * @param clazz A Class to convert to
     * @param irobj An IRubyObject to be converted
     * @return An Object, an instance of clazz as converted by JRuby
     * from irobj
     */
    public Object javaObject(Class clazz, IRubyObject irobj);
    /**
     * Engine method, return java Object from script
     * 
	 * @param clazz A Class to convert to
	 * @param script A String, ruby source code
     * @return An Object, an instance of clazz as converted by JRuby
     * from the result of ruby script
     */
    public Object script(Class clazz, String script);
    /**
     * Engine method, return java Object from script
     * 
	 * @param script A String, ruby source code
     * @return A String, an instance of String as converted by JRuby
     * from the result of ruby script
     */
    public String stringFromScript(String script);
    /**
     * Engine method, return java.lang.String from script
     * 
	 * @param script A String, ruby source code
     * @return An IRubyObject, the result of ruby script
     */
    public IRubyObject script(String script);
    /**
     * Engine method, return IRubyO from script
     * 
	 * @param script A String, ruby source code
     * @return An IRubyO, the result of ruby script wrapped as IRubyO
     */
    public IRubyO rubyOFromScript(String script);
    /**
     * @return A boolean, nil().equals(unwrap())
     */
    public boolean isNil();
    /**
     * JRuby convert to java Object
     * 
	 * @param clazz A Class to convert to
     * @return An Object, an instance of clazz as converted by JRuby
     * from this.unwrap()
     */
    public Object javaObject(Class clazz);
    /**
     * @return An IRubyObject, the wrapped IRubyObject
     */
    public IRubyObject unwrap();
    /**
     * send, return java Object
     * 
	 * @param clazz A Class to convert to
	 * @param method A String, ruby method name
     * @return An Object for clazz Ex:
     * (Ex)javaObject(Ex.class, send(method))
     */
    public Object send(Class clazz, String method);
    /**
     * send, return java Object
     * 
	 * @param clazz A Class to convert to
	 * @param method A String, ruby method name
	 * @param params An Object[], unconverted parameters
     * @return An Object, for clazz Ex:
     * (Ex)javaObject(Ex.class, send(method, param ...))
     */
    public Object send(Class clazz, String method, Object... params);
    /**
     *  send, return java.lang.String
     *  
	 * @param method A String, ruby method name
     * @return A String, the result of sending the method
     */
    public String stringFromSend(String method);
    /**
     * send, return java.lang.String
     * 
	 * @param method A String, ruby method name
	 * @param params An Object[], unconverted parameters
     * @return A String, (String)send(String.class, method, param ...)
     */
    public String stringFromSend(String method, Object... params);
    /**
     *  send, no params
     *  
	 * @param method A String, ruby method name
     * @return An IRubyObject, the result of sending the method with params
     */
    public IRubyObject send(String method);
    /**
     * send, call ruby method on wrapper IRubyObject
     * Returns the result of sending the method/message to the
     * wrapped IRubyObject with params as the parameters after
     * they have been converted to IRubyObjects as follows:
     * <p>
     * null is converted to ruby nil.
     * <p>
     * Instances of IRubyO are unwrapped (by IRubyO.unwrap).
     * <p>
     * Instances of IRubyObject are left unchanged.
     * <p>
     * Other instances converted by JRuby into IRubyObject's.
     * 
	 * @param method A String, ruby method name
	 * @param params An Object[], unconverted parameters
     * @return An IRubyObject, the result of sending the method with params
     */
    public IRubyObject send(String method, Object... params);
    /**
     * rubyOFromSend, no params
     *  
	 * @param method A String, ruby method name
     * @return An IRubyO, rubyO(send(method))
     */
    public IRubyO rubyOFromSend(String method);
    /**
     * send returning IRubyO
     * 
	 * @param method A String, ruby method name
	 * @param params An Object[], unconverted parameters
     * @return An IRubyO, rubyO(send(method, param ...))
     */
    public IRubyO rubyOFromSend(String method, Object... params);
}
