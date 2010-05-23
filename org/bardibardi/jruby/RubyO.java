package org.bardibardi.jruby;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
/*
Taken from part of org.jruby.javasupport.JavaEmbedUtils
    public static Object invokeMethod(Ruby runtime, Object receiver, String method, Object args[], Class returnType) {
        IRubyObject rubyReceiver = receiver == null ? runtime.getTopSelf() : JavaUtil.convertJavaToRuby(runtime, receiver);
        IRubyObject rubyArgs[] = JavaUtil.convertJavaArrayToRuby(runtime, args);
        for(int i = 0; i < rubyArgs.length; i++) {
            IRubyObject obj = rubyArgs[i];
            if(obj instanceof JavaObject)
                rubyArgs[i] = Java.wrap(runtime, obj);
        }

        IRubyObject result = rubyReceiver.callMethod(runtime.getCurrentContext(), method, rubyArgs);
        return rubyToJava(runtime, result, returnType);
    }

    public static Object rubyToJava(Ruby runtime, IRubyObject value, Class type) {
        return JavaUtil.convertArgument(runtime, Java.ruby_to_java(runtime.getObject(), value, Block.NULL_BLOCK), type);
    }

    public static IRubyObject javaToRuby(Ruby runtime, Object value) {
        if(value instanceof IRubyObject) {
            return (IRubyObject)value;
        } else {
            IRubyObject result = JavaUtil.convertJavaToRuby(runtime, value);
            return (result instanceof JavaObject) ? Java.wrap(runtime, result) : result;
        }
    }


*/
/**
 * Main implementation of IRubyO, which will not function
 * correctly unless statics Ruby ruby, IRubyObject nil and
 * IRubyO topSelf have been initialized (can be done by creating
 * a RubyApp).
 * 
 * @author Bardi Einarsson, bardibardi.org
 */
public class RubyO implements IRubyO {
    static protected Ruby ruby;
    static boolean isDefJavaGlobal = false;
    static IRubyObject nil;
    static IRubyO topSelf;
    protected IRubyObject robj;
    protected RubyO(IRubyObject irobj) {
    	robj = irobj;
    }
    /**
     * Filter call to JavaEmbedUtils.invokeMethod using global
     * JRuby engine, ruby and wrapped IRubyObject, robj.
     * <p>
     * Conversions done:
     * <p>
     * params null becomes new Object[] {}
     * (eliminates a surprise)
     * <p>
     * single param of null becomes ruby nil
     * (probably unnecessary)
     * <p>
     * single param of IRubyO becomes the wrapped IRubyObject
     * 
     * @param method, String, ruby method name 
     * @param params, Object[], unconverted parameters
     * @param clazz, Class to convert result to
     * @return result of IRubyObject from method, converted to clazz
     */
    public Object invokeMethod(String method, Object[] params, Class clazz) {
    	if (null == params) {
    		params = new Object[] {};
    	}
    	for (int i = 0; i < params.length; ++i) {
    	    Object obj = params[i];
    	    if (obj instanceof IRubyO) {
    	        params[i] = ((IRubyO)obj).unwrap();
    	    }
    		if (null == obj) {
    			params[i] = nil;
    		}
    	}
    	return JavaEmbedUtils.invokeMethod(ruby, robj, method, params, clazz);
    }

    // START PSEUDO STATIC
    public IRubyO topSelf() {
    	return topSelf;
    }
    public IRubyObject nil() {
    	return nil;
    }
    public boolean isNil(IRubyObject irobj) {
//    	return null == javaObject(Object.class, irobj);
    	return nil.equals(irobj);
    }
    public boolean isNil(IRubyO iro) {
//    	return null == javaObject(Object.class, irobj);
    	return nil.equals(iro.unwrap());
    }
    public IRubyO rubyO(IRubyObject irobj) {
    	return new RubyO(irobj);
    }
    static String JAVA_GLOBAL_HASH = 
    	"$java_global_hash = {}";
    static String JAVA_GLOBAL =
    	"def java_global(name)\n" +
	        "$java_global_hash[name]\n" +
        "end";
    static String JAVA_GLOBAL_SET =
    	"def java_global_set(name, x)\n" +
		    "return nil if $java_global_hash[name]\n" +
		    "$java_global_hash[name] = x\n" +
		    "name\n" +
	    "end";
    static String JAVA_GLOBAL_REMOVE =
    	"def java_global_remove(name)\n" +
		    "return nil unless $java_global_hash[name]\n" +
		    "$java_global_hash[name] = nil\n" +
		    "name\n" +
	    "end";
    /**
     * Make sure that the ruby scripts supporting
     * the javaGlobal... engine methods have been
     * run.
     */
    public void defJavaGlobal() {
    	if (isDefJavaGlobal) {
    		return;
    	}
        script(JAVA_GLOBAL_HASH);
        script(JAVA_GLOBAL);
        script(JAVA_GLOBAL_SET);
        script(JAVA_GLOBAL_REMOVE);
        isDefJavaGlobal = true;
    }
    public String javaGlobalSet(String key, Object obj) {
    	defJavaGlobal();
    	return (String)send(String.class, "java_global_set", key, obj);
    }
    public IRubyObject javaGlobal(String key) {
    	defJavaGlobal();
    	return send("java_global", key);
    }
    public Object javaGlobal(Class clazz, String key) {
    	defJavaGlobal();
    	return send(clazz, "java_global", key);
    }
    public String javaGlobalRemove(String key) {
    	defJavaGlobal();
    	return stringFromSend("java_global_remove", key);
    }
    public IRubyObject rubyObject(Object obj) {
    	return JavaEmbedUtils.javaToRuby(ruby, obj);
    }
    public Object javaObject(Class clazz, IRubyO iro) {
    	return JavaEmbedUtils.rubyToJava(ruby, iro.unwrap(), clazz);
    }
    public Object javaObject(Class clazz, IRubyObject irobj) {
    	return JavaEmbedUtils.rubyToJava(ruby, irobj, clazz);
    }
    public Object script(Class clazz, String script) {
    	return javaObject(clazz, script(script));
    }
    public String stringFromScript(String script) {
    	return (String)javaObject(String.class, script(script));
    }
    public IRubyObject script(String script) {
    	return ruby.evalScriptlet(script + "\n");
    }
    public IRubyO rubyOFromScript(String script) {
    	return new RubyO(script(script));
    }
    // END PSEUDO STATIC
    public boolean isNil() {
    	return nil.equals(robj);
    }
    public Object javaObject(Class clazz) {
    	return JavaEmbedUtils.rubyToJava(ruby, robj, clazz);
    }
    public IRubyObject unwrap() {
    	return robj;
    }
    public Object send(Class clazz, String method) {
        return invokeMethod(method, new Object[] {}, clazz);
    }
    public Object send(Class clazz, String method, Object... params) {
        return invokeMethod(method, params, clazz);
    }
    public String stringFromSend(String method) {
    	return (String)send(String.class, method);
    }
    public String stringFromSend(String method, Object... params) {
    	return (String)send(String.class, method, params);
    }
    public IRubyObject send(String method) {
    	return (IRubyObject)send(IRubyObject.class, method);
    }
    public IRubyObject send(String method, Object... params) {
    	return (IRubyObject)send(IRubyObject.class, method, params);
    }
    public IRubyO rubyOFromSend(String method) {
    	return new RubyO(send(method));
    }
    public IRubyO rubyOFromSend(String method, Object... params) {
    	return new RubyO(send(method, params));
    }
}
