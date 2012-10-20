package org.gvsig.exceptions;

import junit.framework.TestCase;

public class BaseExceptionTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSimple() {
		try {
			throw new NullPointerException("Excepcion de puntero nulo");
		} catch (NullPointerException e) {
			DriverException de = new DriverException("SimpleDriver", e);
			assertEquals("Error in the driver SimpleDriver", de.getMessage());
			assertEquals(
					"Error in the driver SimpleDriver\nExcepcion de puntero nulo\n",
					de.getMessageStack());
		}
	}

	public void testSimpleLocalized() {
		class MyTranslator implements IExceptionTranslator {
			public String getText(String clave) {
				return clave.toUpperCase();
			}
		}
		BaseException.setTranslator(new MyTranslator());
		try {
			throw new NullPointerException("Excepcion de puntero nulo");
		} catch (NullPointerException e) {
			DriverException de = new DriverException("SimpleDriver", e);
			assertEquals("ERROR_IN_THE_DRIVER_%(DRIVERNAME)S",
					de.getLocalizedMessage());
			assertEquals(
					"ERROR_IN_THE_DRIVER_%(DRIVERNAME)S\nExcepcion de puntero nulo\n",
					de.getLocalizedMessageStack());
		}
		BaseException.setTranslator(null);
	}

	public void testSimple2() {
		try {
			throw new NullPointerException("Excepcion de puntero nulo");
		} catch (NullPointerException e) {
			BadDateException de = new BadDateException("SimpleDriver", e);
			assertEquals("Driver SimpleDriver: Formato de fecha incorrecto",
					de.getMessage());
			assertEquals(
					"Driver SimpleDriver: Formato de fecha incorrecto\nExcepcion de puntero nulo\n",
					de.getMessageStack());
		}
	}

	public void testSimpleLocalized2() {
		class MyTranslator implements IExceptionTranslator {
			public String getText(String clave) {
				return clave.toUpperCase();
			}
		}
		BaseException.setTranslator(new MyTranslator());
		try {
			throw new NullPointerException("Excepcion de puntero nulo");
		} catch (NullPointerException e) {
			BadDateException de = new BadDateException("SimpleDriver", e);
			assertEquals("DRIVER_%(DRIVERNAME)S_FORMATO_DE_FECHA_INCORRECTO",
					de.getLocalizedMessage());
			assertEquals(
					"DRIVER_%(DRIVERNAME)S_FORMATO_DE_FECHA_INCORRECTO\nExcepcion de puntero nulo\n",
					de.getLocalizedMessageStack());
		}
		BaseException.setTranslator(null);
	}

	public void testTranslatorWithoutInterface() {
		class MyTranslator {
		}
		BaseException.setTranslator(new MyTranslator());
		try {
			throw new NullPointerException("Excepcion de puntero nulo");
		} catch (NullPointerException e) {
			BadDateException de = new BadDateException("SimpleDriver", e);
			assertEquals("DRIVER_%(DRIVERNAME)S_FORMATO_DE_FECHA_INCORRECTO",
					de.getLocalizedMessage());
			assertEquals(
					"DRIVER_%(DRIVERNAME)S_FORMATO_DE_FECHA_INCORRECTO\nExcepcion de puntero nulo\n",
					de.getLocalizedMessageStack());
		}
		BaseException.setTranslator(null);

	}

	class BadDateException extends DriverException {
		private static final long serialVersionUID = -8985920349210629998L;

		public BadDateException(String driverName, Throwable cause) {
			super(driverName, cause);
			messageKey = "Driver_%(driverName)s_Formato_de_fecha_incorrecto";
			formatString = "Driver %(driverName)s: Formato de fecha incorrecto";
			code = serialVersionUID;
		}
	}
}
