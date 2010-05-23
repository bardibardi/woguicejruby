package org.bardibardi.wo.guice;

import org.bardibardi.guice.GetInjectorFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

/**
 * As it is not known how to intercept the construction
 * of a WOComponent so that a Guice Injector can be
 * used to create an instance of a WOComponent instead of
 * the WebObjects server, a work-around is necessary.
 * The work-around assumes that the WOComponent's associated
 * WOApplication has a property called "injector" which can
 * be accessed by KVC to get a Guice Injector.
 * The work-around relies on the Injector.injectMembers method
 * to do Guice injection on an already constructed Object.
 * <p>
 * Example code from WOApplication subclass, Application:
 * <code>
 * ...
 * import com.google.inject.Injector;
 * import org.bardibardi.guice.GetInjectorFactory;
 * import com.webobjects.appserver.WOApplication;
 * ...
 * private Injector injector;
 * public Injector getInjector() {
 *     return injector;
 * }
 * ...
 * public Application {
 *     super();
 *     System.setProperty("woisme.injector.factory",
 *         "org.bardibardi.woisme.WoismeInjectorFactory");
 *     injector = GetInjectorFactory.
 *         fromProperty("woisme.injector.factory").getInjector();
 *     ...
 * }
 * ...
 * </code>
 * <p>
 * Example code from WOComponent subclass, Main:
 * <code>
 * ...
 * import com.google.inject.Inject;
 * import org.bardibardi.wo.guice.InjectWOComponent;
 * import com.webobjects.appserver.WOComponent;
 * import com.webobjects.appserver.WOContext;
 * ...
 * public Main(WOContext context) {
 *     super(context);
       InjectWOComponent.injectMembers(this);
 *     ...
 * }
 * ...
 * private ITablePropertyList pList;
 * @Inject
 * public void setPList(ITablePropertyList ppList) {
 *     pList = ppList;
 * }
 * public String pList() {
 *     if (null != pList) {
 *         return pList.pList();
 *     }
 *     return "pList not configured";
 * }
 * ...
 * </code>
 * <p>
 * Example IInjectorFactory implementation:
 * <code>
 * package org.bardibardi.woisme;
 *
 * import com.google.inject.Guice;
 * import com.google.inject.Injector;
 * import com.google.inject.Module;
 * import com.google.inject.Binder;
 * import com.google.inject.name.Names;
 *
 * import org.bardibardi.guice.IInjectorFactory;
 * import org.bardibardi.jruby.guice.RubyAppGuiceModule;
 *
 * public class WoismeInjectorFactory implements IInjectorFactory {
 *     static String RUBY_APP_FILE_DIRECTORY = "/home/bardi/work/woisme";
 *     static String RUBY_APP_FILE_NAME_WITHOUT_RB_EXTENSION = "woisme_ruby_app";
 *     static String RUBY_APP_CLASS_NAME = "WoismeRubyApp";
 *     static String RUBY_APP_LOAD_PATH_ADDITIONS = 
 *         "/home/bardi/work/woisme/describe" +
 *         System.getProperty("path.separator") +
 *         "/home/bardi/work/woisme/util";
 *     static {
 *     	   System.setProperty("ruby.app.file.directory", RUBY_APP_FILE_DIRECTORY);
 *     	   System.setProperty("ruby.app.file.name.without.rb.extension", RUBY_APP_FILE_NAME_WITHOUT_RB_EXTENSION);
 * 		   System.setProperty("ruby.app.class.name", RUBY_APP_CLASS_NAME);
 * 		   System.setProperty("ruby.app.load.path.additions", RUBY_APP_LOAD_PATH_ADDITIONS);
 * 	   }
 *     static Injector i;
 *     public Injector getInjector() {
 *         if (null != i) {
 *         return i;
 *         }
 *         ApplicationGuiceModule g = new ApplicationGuiceModule();
 * 		   return Guice.createInjector(g);
 *     }
 *
 * static class ApplicationGuiceModule implements Module {
 *     static String TABLE_NAME_FOR_PLIST = "COUNTRIES";
 *     public void configure(Binder b) {
 *         b.install(new RubyAppGuiceModule());
 *         b.bind(ITablePropertyList.class).to(TablePropertyList.class);
 *         b.bindConstant().annotatedWith(Names.named("table.name.for.plist")).to(TABLE_NAME_FOR_PLIST);
 *     }
 * } // ApplicationGuiceModule
 * } // WoismeInjectorFactory
 * </code>
 *  
 * @author Bardi Einarsson, bardibardi.org
 *
 */
public class InjectWOComponent {
	/**
	 * Guice inject the WOComponent if the WOApplication's
	 * "injector" property is not null
	 * 
	 * @param wo, WOComponent to be Guice injected
	 */
    public static void injectMembers(WOComponent wo) {
    	Injector i = (Injector)wo.application().valueForKey("injector");
    	if (null == i) {
    		return;
    	}
    	i.injectMembers(wo);
    }
}
