package org.bardibardi.jruby.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.google.inject.Injector;
import org.jruby.runtime.builtin.IRubyObject;

import org.bardibardi.guice.GetInjectorFactory;

import org.bardibardi.jruby.IRubyO;

import java.math.BigDecimal;
import java.io.File;

/**
 * Test the package org.bardibardi.jruby -- primarily test the implementation
 * of the org.bardibardi.jruby.IRubyO interface, calling JRuby from java.
 * <p>
 * NB: These tests do not clean up the state of the JRubyEngine.
 * As the tests are run in arbitrary order, one must review them
 * all before writing a new test to insure that that test will not
 * be affected by the running of other tests. It is not difficult
 * to do the review.
 * <p>
 * These tests require java 5, JUnit 4, Guice 1 and JRuby 1.1,
 * as well as the specific code being tested.
 * <p>
 * /home/bardi/Desktop/eclipse/plugins/org.junit4_4.3.1/junit.jar
 * <p>
 * guice-1.0.jar
 * <p>
 * jruby-complete-1.1.2.jar
 * <p>
 * Necessary code:
 * <p>
 * org.bardibardi.guice.GetInjectorFactory.java
 * <p>
 * org.bardibardi.guice.IInjectorFactory.java
 * <p>
 * org.bardibardi.guice.SystemProperty.java - not used here yet
 * <p>
 * org.bardibardi.jruby.ConfigureJRuby.java
 * <p>
 * org.bardibardi.jruby.ConfigureRubyApp.java
 * <p>
 * org.bardibardi.jruby.IConfigureRubyApp.java
 * <p>
 * org.bardibardi.jruby.IRubyO.java
 * <p>
 * org.bardibardi.jruby.RubyApp.java
 * <p>
 * org.bardibardi.jruby.RubyO.java
 * <p>
 * org.bardibardi.jruby.guice.JRubyCompleteModule.java
 * <p>
 * org.bardibardi.jruby.guice.RubyAppGuiceModule.java
 * <p>
 * org.bardibardi.jruby.test.JRubyTest.java
 * <p>
 * org.bardibardi.jruby.test.JRubyTestInjectorFactory.java
 * 
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class JRubyTest {
	public static final String ICELANDIC = "áéíóúýæöðþÁÉÍÓÚÝÆÖÐÞ";
	static Injector injector;
	static IRubyO rubyApp;
	/**
	 * Initialize JRuby using Guice.
	 * NB: Guice is used to get the one instance of rubyApp.
	 * After rubyApp is gotten, all configuration necessary to
	 * use IRubyO's is complete. rubyApp is the only IRubyO
	 * constructed by Guice.
	 */
    @BeforeClass
    public static void initializeJRuby() {
        System.setProperty("jruby.test.injector.factory", "org.bardibardi.jruby.test.JRubyTestInjectorFactory");
        injector = GetInjectorFactory.fromProperty("jruby.test.injector.factory").getInjector();
    	rubyApp = injector.getInstance(IRubyO.class);
    }
    /**
     * rubyApp should wrap JRuby's top level object.
     * Test RubyO.unwrap and RubyO.topSelf
     */
    @Test
    public void topSelf() {
    	IRubyObject irobj1 = rubyApp.unwrap();
    	IRubyObject irobj2 = rubyApp.topSelf().unwrap();
    	assertSame(irobj1, irobj2);
    }
    /**
     * rubyApp should wrap JRuby's top level object.
     * Test RubyO.unwrap and RubyO.script(String)
     */
    @Test
    public void topSelf2() {
    	IRubyObject irobj1 = rubyApp.unwrap();
    	IRubyObject irobj2 = rubyApp.script("self");
    	assertSame(irobj1, irobj2);
    }
    /**
     * rubyApp should be the unique IRubyO that
     * wraps JRuby's top level object.
     * Test RubyO.topSelf
     */
    @Test
    public void topSelf3() {
    	IRubyO iro1 = rubyApp;
    	IRubyO iro2 = rubyApp.topSelf();
    	assertSame(iro1, iro2);
    }
    /**
     * JRuby should add.
     * Test RubyO.rubyOFromScript(String)
     * and RubyO.javaObject(Class)
     */
    @Test
    public void twoPlusTwo() {
    	IRubyO iro = rubyApp.rubyOFromScript("2+2");
    	Integer i = (Integer)iro.javaObject(Integer.class);
    	assertEquals(4, i);
    }
    /**
     * JRuby should add.
     * Test RubyO.script(Class, String)
     */
    @Test
    public void twoPlusTwo2() {
    	Integer i = (Integer)rubyApp.script(Integer.class, "2+2");
    	assertEquals(4, i);
    }
    /**
     * JRuby should accept method definition.
     * Test RubyO.script(String) and RubyO.stringFromSend(String)
     */
    @Test
    public void helloWorld() {
    	rubyApp.script("def hello_world\n'Hello World!'\nend");
    	String hello = rubyApp.stringFromSend("hello_world");
    	assertEquals("Hello World!", hello);
    }
    /**
     * JRuby method should accept null parameter
     * and be able to return null.
     * Test RubyO.script(String) and 
     * RubyO.stringFromSend(String, Object...)
     */
    @Test
    public void say() {
    	rubyApp.script("def say(it)\nit\nend");
    	String it = rubyApp.stringFromSend("say", (Object)null);
    	assertEquals(null, it);
    }
    /**
     * JRuby method should complain about missing parameters.
     * Test RubyO.script(String) and 
     * RubyO.stringFromSend(String, Object...)
     */
    @Test
    public void say2() {
    	rubyApp.script("def say(it)\nit\nend");
    	String it = null;
    	try {
    	    it = rubyApp.stringFromSend("say", (Object[])null);
    	}
    	catch (Exception e) {
    		;
    	}
    	assertNull(it);
    }
    /**
     * JRuby method should accept ruby nil parameter
     * and be able to return null.
     * Test RubyO.script(String), RubyO.nil and 
     * RubyO.stringFromSend(String, Object...)
     */
    @Test
    public void say3() {
    	rubyApp.script("def say(it)\nit\nend");
    	String it = rubyApp.stringFromSend("say", rubyApp.nil());
    	assertEquals(null, it);
    }
    /**
     * JRuby method should not step on java Unicode.
     * Test RubyO.script(String) and 
     * RubyO.stringFromSend(String, Object...)
     */
    @Test
    public void say4() {
    	rubyApp.script("def say(it)\nit\nend");
    	String it = rubyApp.stringFromSend("say", ICELANDIC);
    	assertEquals(ICELANDIC, it);
    }
    /**
     * JRuby ruby String is UTF-8.
     * Test RubyO.script(String) and
     * RubyO.send(Class, String, Object...)
     */
    @Test
    public void utf8length() {
    	rubyApp.script("def len(s)\ns.length\nend");
    	int i = (Integer)rubyApp.send(Integer.class, "len", ICELANDIC);
    	assertEquals(2*ICELANDIC.length(), i);
    }
    /**
     * JRuby method should not step on java Unicode.
     * NB: ruby strings are bytes so indexing is on bytes.
     * Test RubyO.script(String) and 
     * RubyO.stringFromSend(String, Object...)
     */
    @Test
    public void utf8char() {
    	rubyApp.script("def substring(s,n1,n2)\ns[n1..n2]\nend");
    	String s = rubyApp.stringFromSend("substring", ICELANDIC, 2, 3);
    	assertEquals("é", s);
    }
    /**
     * JRuby method should not step on java Unicode.
     * NB: ruby strings are mutable
     * Test RubyO.script(String) and 
     * RubyO.stringFromSend(String, Object...)
     */
    @Test
    public void utf8char2() {
    	rubyApp.script("def bardi_string(s)\ns[3..4] = 'd'\ns\nend");
    	String s = rubyApp.stringFromSend("bardi_string", "Barði");
    	assertEquals("Bardi", s);
    }
    /**
     * The JRuby nil object must be unique.
     * Test RubyO.nil and Ruby.script(String)
     */
    @Test 
    public void isNil() {
    	IRubyObject irobj1 = rubyApp.nil();
    	IRubyObject irobj2 = rubyApp.script("nil");
    	assertSame(irobj1, irobj2);
    }
    /**
     * The JRuby nil object must convert to java null.
     * Test RubyO.nil and RubyO.javaObject(Class, IRubyObject)
     */
    @Test 
    public void isNil2() {
    	IRubyObject irobj = rubyApp.nil();
    	assertNull(rubyApp.javaObject(Object.class, irobj));
    }
    /**
     * IRubyO wrapping ruby nil must report nil.
     * Test RubyO.rubyO and RubyO.isNil
     */
    @Test
    public void isNil3() {
    	IRubyO iro = rubyApp.rubyO(rubyApp.nil());
    	assertTrue(iro.isNil());
    }
    /**
     * IRubyO wrapping ruby nil must convert to java null.
     * Test RubyO.rubyOFromScript(String) and
     * RubyO.javaObject(Class)
     */
    @Test
    public void nil() {
    	IRubyO iro = rubyApp.rubyOFromScript("nil");
    	Object obj = iro.javaObject(Object.class);
    	assertNull(obj);
    }
    /**
     * IRubyO wrapping ruby nil must convert to java null.
     * Test RubyO.rubyOFromScript(String) and
     * RubyO.javaObject(Class)
     */
    @Test
    public void nil2() {
    	IRubyO iro = rubyApp.rubyOFromScript("nil");
    	String s = (String)iro.javaObject(String.class);
    	assertNull(s);
    }
    /**
     * JRuby must preserve java.lang.String value.
     * Test RubyO.javaGlobalSet and RubyO.javaGlobal
     */
    @Test
    public void jgs() {
    	String hello = "Hello World!";
    	rubyApp.javaGlobalSet("hello", hello);
    	String helloAgain = (String)rubyApp.javaGlobal(String.class, "hello");
    	// assertSame fails
    	assertEquals(hello, helloAgain);
    }
    /**
     * RubyO.javaGlobalSet must not remove/replace values
     * Test RubyO.javaGlobalSet and RubyO.javaGlobal
     */
    @Test
    public void jgs2() {
    	String hello = "Hello World!";
    	String hello2 = "Will not be set";
    	rubyApp.javaGlobalSet("hello", hello);
    	String shouldBeNull = rubyApp.javaGlobalSet("hello", hello2);
    	assertNull(shouldBeNull);
    	String helloAgain = (String)rubyApp.javaGlobal(String.class, "hello");
    	// assertSame fails
    	assertEquals(hello, helloAgain);
    }
    /**
     * RubyO.javaGlobalRemove must remove values.
     * Test RubyO.javaGlobalSet, RubyO.javaGlobalRemove
     * and RubyO.javaGlobal
     */
    @Test
    public void jgs3() {
    	String hello = "Hello World!";
    	String hello2 = "Hello World2!";
    	rubyApp.javaGlobalSet("hello", hello);
    	rubyApp.javaGlobalRemove("hello");
    	String key = rubyApp.javaGlobalSet("hello", hello2);
    	assertEquals(key, "hello");
    	String helloAgain = (String)rubyApp.javaGlobal(String.class, "hello");
    	// assertSame fails
    	assertEquals(hello2, helloAgain);
    }
    /**
     * JRuby's float conversion works.
     * Test RubyO.script(Class, String)
     */
    @Test
    public void floatTest() {
    	float f = (Float)rubyApp.script(Float.class, "45.6");
    	assertEquals(45.6, f);
    }
    /**
     * JRuby's double conversion works.
     * Test RubyO.script(Class, String)
     */
    @Test
    public void doubleTest() {
    	double d = (Double)rubyApp.script(Double.class, "45.6");
    	assertEquals(45.6, d);
    }
    /**
     * JRuby preserves identity for objects it does not convert.
     * Test RubyO.javaGlobalSet and RubyO.javaGlobal
     */
    @Test
    public void bigDecimalTest() {
    	BigDecimal b = new BigDecimal(45.6);
    	rubyApp.javaGlobalSet("b", b);
    	BigDecimal bd = (BigDecimal)rubyApp.javaGlobal(BigDecimal.class, "b");
    	assertSame(b, bd);
    }
    /**
     * JRuby non-existent global must return ruby nil.
     * Test RubyO.script(String) and RubyO.nil
     */
    @Test 
    public void doesNotExist() {
    	IRubyObject irobj = rubyApp.script("$what_me_worry");
    	assertSame(rubyApp.nil(), irobj);
    }
    /**
     * JRuby non-existent global must return ruby nil.
     * Test RubyO.script(String) and RubyO.nil
     */
    @Test 
    public void doesNotExist2() {
    	IRubyObject irobj = rubyApp.script("x = $what_me_worry2");
    	assertSame(rubyApp.nil(), irobj);
    }
    /**
     * JRuby non-existent global must return ruby nil.
     * Test RubyO.script(String) and RubyO.nil
     */
    @Test 
    public void doesNotExist3() {
    	rubyApp.script("x2 = $what_me_worry3");
    	IRubyObject irobj = rubyApp.script("x2");
    	assertSame(rubyApp.nil(), irobj);
    }
    /**
     * JRuby top level object local variables are available.
     * Test RubyO.script(String) and
     * RubyO.stringFromString(String)
     */
    @Test 
    public void localVariable() {
    	rubyApp.script("x3 = 'Hello World!'");
    	String s = rubyApp.stringFromScript("x3");
    	assertEquals("Hello World!", s);
    }
    /**
     * JRuby non-existent local variable,
     * when referred to, must throw a JRuby exception.
     * Test RubyO.script(String)
     */
    @Test 
    public void localVariable2() {
    	IRubyObject irobj = null;
    	try {
    	    irobj = rubyApp.script("y");
    	}
    	catch (Exception e) {
    		;
    	}
    	assertNull(irobj);
    }
    /**
     * JRuby local variables must be convertable to
     * IRubyO's that give access to engine methods.
     * Test RubyO.rubyOFromScript, RubyO.stringFromScript
     * RubyO.script(Class, Object...), and
     * RubyO.send(Class, String)
     */
    @Test 
    public void localVariable3() {
    	IRubyO z = rubyApp.rubyOFromScript("z = '1'");
    	IRubyO z1 = rubyApp.rubyOFromScript("2");
    	assertEquals(rubyApp.stringFromScript("z"), z1.stringFromScript("z"));
    	assertEquals(z.stringFromScript("z"), z1.stringFromScript("z"));
    	assertEquals(1, (Integer)rubyApp.script(Integer.class, "z.to_i"));
    	assertEquals(1, (Integer)z.send(Integer.class, "to_i"));
    }
    /**
     * Generated ruby script must work.
     * Test RubyO.script and RubyO.stringFromScript
     */
    @Test
    public void javaString() {
    	String hello = "Hello World!";
    	rubyApp.script("$hello = '" + hello + "'");
    	String helloAgain = rubyApp.stringFromScript("$hello");
    	assertEquals(hello, helloAgain);
    }
    /**
     * Generated ruby script must work.
     * Test RubyO.script and RubyO.stringFromScript
     */
    @Test
    public void javaString2() {
    	String hello = "Hello World!";
    	rubyApp.script("$JString = java.lang.String");
    	rubyApp.script("$hello = $JString.new '" + hello + "'");
    	String helloAgain = rubyApp.stringFromScript("$hello");
    	assertEquals(hello, helloAgain);
    }
    /**
     * Generated ruby script must work.
     * Test RubyO.script and RubyO.stringFromScript
     * NB: This test fails. Arguably a BUG.
     * The setting of $KCODE does not fix this.
     * see: http://jira.codehaus.org/browse/JRUBY-2484
     * jirb_swing handles this correctly.
     */
    @Ignore @Test
    public void javaString3() {
    	rubyApp.script("$KCODE = 'UTF8'");
    	assertEquals("UTF8", rubyApp.stringFromScript("$KCODE"));
    	String hello = ICELANDIC;
    	rubyApp.script("$hello = '" + hello + "'");
    	String helloAgain = rubyApp.stringFromScript("$hello");
    	assertEquals(hello, helloAgain);
    }
    /**
     * Conversion to ruby object must work.
     * Test RubyO.rubyObject and
     * RubyO.script(String)
     */
    @Test
    public void rubyString() {
    	String hello = "Hello World!";
    	IRubyObject rubyHello = rubyApp.rubyObject(hello);
    	rubyApp.script("$hello = '" + hello + "'");
    	IRubyObject helloAgain = rubyApp.script("$hello");
    	assertEquals(rubyHello, helloAgain);
    }
    /**
     * Calling shell via JRuby must work.
     * Test RubyO.stringFromScript
     */
    @Test
    public void pwd() {
    	String pwd = rubyApp.stringFromScript("`pwd`");
    	String dir = null;
    	try {
    		dir = (new File(".")).getCanonicalPath();
    	}
    	catch (Exception e) {
    		;
    	}
    	assertEquals(dir + "\n", pwd);
    }
    /**
     * Calling shell via JRuby must work.
     * Test RubyO.stringFromScript
     */
    @Test
    public void pwd2() {
    	String pwd = rubyApp.stringFromScript("`pwd`");
    	String dir = System.getProperty("user.dir");
    	assertEquals(dir + "\n", pwd);
    }
    static int pwd3Times = 1; // 30 milliseconds per time, YUCK!
    /**
     * performance of pwd2
     */
    @Test
    public void pwd3() {
    	for (int i = 0; i < pwd3Times; ++i) {
    		pwd2();
    	}
    }
    static int rubyString2Times = 1; // 4 milliseconds per time, YUCK!
    /**
     * performance of rubyString
     */
    @Test
    public void rubyString2() {
    	for (int i = 0; i < rubyString2Times; ++i) {
    		rubyString();
    	}
    }
}
