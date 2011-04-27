package com.iver.utiles.extensionPoints;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import junit.framework.TestCase;

public class TestExtensionPoint extends TestCase {
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreacion() {
		String name = "LayerWizars";
		String description = "Punto de extension que registra los distintos Wizars para añadir capas.";
		ExtensionPoint ep = null;
		
		ep = new ExtensionPoint(name);
		TestExtensionPoint.assertEquals(ep.getName(),name);
		TestExtensionPoint.assertEquals(ep.getDescription(),null);
		ep.setDescripcion(description);
		TestExtensionPoint.assertEquals(ep.getDescription(),description);
		
		
		ep = new ExtensionPoint(name,description);
		TestExtensionPoint.assertEquals(ep.getName(),name);
		TestExtensionPoint.assertEquals(ep.getDescription(),description);
		
		ep.put("WMSLayer",(Object)ExtensionDePrueba2.class);
		ep.put("WCSLayer",(Object)ExtensionDePrueba1.class);
		ep.put("WFSLayer",(Object)ExtensionDePrueba1.class);
		
		TestExtensionPoint.assertEquals(ep.size(),3);
		
		// Comprobamos que la lista de extensiones
		// mantiene el orden de insercion.
		Iterator iter = ep.keySet().iterator();
		TestExtensionPoint.assertEquals("WMSLayer",iter.next());
		TestExtensionPoint.assertEquals("WCSLayer",iter.next());
		TestExtensionPoint.assertEquals("WFSLayer",iter.next());

		try {
			/*Object extension =*/ ep.create("WCSLayer");
		} catch (InstantiationException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WCSLayer con InstantiationException");
		} catch (IllegalAccessException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WCSLayer con IllegalAccessException");
		}
		
		ExtensionDePrueba2 extension = null;
		try {
			Object args[] = {"pepe",new Integer(5)};
			extension =(ExtensionDePrueba2)ep.create("WMSLayer",args);
		} catch (InstantiationException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WMSLayer con InstantiationException");
		} catch (IllegalAccessException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WMSLayer con IllegalAccessException");
		} catch (SecurityException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WMSLayer con SecurityException");
		} catch (IllegalArgumentException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WMSLayer con IllegalArgumentException");
		} catch (NoSuchMethodException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WMSLayer con NoSuchMethodException");
		} catch (InvocationTargetException e) {
			TestExtensionPoint.fail("Ha petado la creacion de WMSLayer con InvocationTargetException");
		}
		
		TestExtensionPoint.assertEquals("pepe",extension.nombre);
		TestExtensionPoint.assertEquals(5,extension.ancho);
		
	}

	public void testInsert() {
		String name = "LayerWizars";
		String description = "Punto de extension que registra los distintos Wizars para añadir capas.";
		ExtensionPoint ep = null;
		
		ep = new ExtensionPoint(name,description);
		
		ep.put("WMSLayer",(Object)ExtensionDePrueba2.class);
		ep.put("WCSLayer",(Object)ExtensionDePrueba1.class);
		ep.put("WFSLayer",(Object)ExtensionDePrueba1.class);
		Iterator i=ep.keySet().iterator();
		TestExtensionPoint.assertEquals("WMSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("WCSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("WFSLayer",(String)i.next());	
		
		ep.insert("WCSLayer","testA",null,(Object)ExtensionDePrueba1.class);

		i=ep.keySet().iterator();
		TestExtensionPoint.assertEquals("WMSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("testA",(String)i.next());
		TestExtensionPoint.assertEquals("WCSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("WFSLayer",(String)i.next());	

		ep.insert("XXXX","testB",null,(Object)ExtensionDePrueba1.class);
		i=ep.keySet().iterator();
		TestExtensionPoint.assertEquals("WMSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("testA",(String)i.next());
		TestExtensionPoint.assertEquals("WCSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("WFSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("testB",(String)i.next());	

		ep.insert("testB","testC",null,(Object)ExtensionDePrueba1.class);
		i=ep.keySet().iterator();
		TestExtensionPoint.assertEquals("WMSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("testA",(String)i.next());
		TestExtensionPoint.assertEquals("WCSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("WFSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("testC",(String)i.next());	
		TestExtensionPoint.assertEquals("testB",(String)i.next());	

		ep.insert("WMSLayer","testD",null,(Object)ExtensionDePrueba1.class);
		i=ep.keySet().iterator();
		TestExtensionPoint.assertEquals("testD",(String)i.next());
		TestExtensionPoint.assertEquals("WMSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("testA",(String)i.next());
		TestExtensionPoint.assertEquals("WCSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("WFSLayer",(String)i.next());	
		TestExtensionPoint.assertEquals("testC",(String)i.next());	
		TestExtensionPoint.assertEquals("testB",(String)i.next());	
	}
}

class ExtensionDePrueba1 {
    public ExtensionDePrueba1() {
		;
	}
}
class ExtensionDePrueba2 {
	public int ancho;
	public String nombre;
	
	public ExtensionDePrueba2(String nombre, Integer ancho) {
		this.ancho = ancho.intValue();
		this.nombre = nombre;
	}
}