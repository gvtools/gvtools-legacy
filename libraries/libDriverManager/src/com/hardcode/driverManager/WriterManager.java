package com.hardcode.driverManager;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Para el writer manager, el writer viene determinado por un directorio dentro
 * del cual se encuentran uno o más jar's. La clase Writer ha de implementar
 * la interfaz Writer y su nombre debe terminar en "Writer" y tener un
 * constructor sin parámetros.
 *
 * <p>
 * Esta clase es la encargada de la carga y validación de los writers y de la
 * obtención de los mismo apartir de un tipo
 * </p>
 *
 * @author Vicente Caballero Navarro
 */
public class WriterManager {
	private DriverValidation validation;
	private HashMap nombreWriterClass = new HashMap();
	private ArrayList failures = new ArrayList();
	private ArrayList writersClassLoaders = new ArrayList();

	/**
	 * Devuelve un array con los directorios de los plugins
	 *
	 * @param dirExt Directorio a partir del que se cuelgan los directorios de
	 * 		  los drivers
	 *
	 * @return Array de los subdirectorios
	 */
	private File[] getPluginDirs(File dirExt) {
		if (!dirExt.exists()) {
			return new File[0];
		}

		ArrayList ret = new ArrayList();
		File[] files = dirExt.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				ret.add(files[i]);
			}
		}

		return (File[]) ret.toArray(new File[0]);
	}

	/**
	 * Obtiene los jar's de un directorio y los devuelve en un array
	 *
	 * @param dir Directorio del que se quieren obtener los jars
	 *
	 * @return Array de jars
	 */
	private URL[] getJars(File dir) {
		ArrayList ret = new ArrayList();
		File[] dirContent = dir.listFiles();

		for (int i = 0; i < dirContent.length; i++) {
			if (dirContent[i].getName().toLowerCase().endsWith(".jar")) {
				try {
					ret.add(new URL("file:" + dirContent[i].getAbsolutePath()));
				} catch (MalformedURLException e) {
					//No se puede dar
				}
			}
		}

		return (URL[]) ret.toArray(new URL[0]);
	}

	/**
	 * Carga los drivers y asocia con el tipo del driver.
	 *
	 * @param dir Directorio raíz de los drivers
	 */
	public void loadWriters(File dir) {
		try {
			if (validation == null) {
				validation = new DriverValidation() {
							public boolean validate(Driver d) {
								return true;
							}
						};
			}

			//Se obtiene la lista de directorios
			File[] dirs = getPluginDirs(dir);

			//Para cada directorio se obtienen todos sus jars
			for (int i = 0; i < dirs.length; i++) {
				URL[] jars = getJars(dirs[i]);

				//Se crea el classloader
				DriverClassLoader cl = new DriverClassLoader(jars,
						dirs[i].getAbsolutePath(),
						this.getClass().getClassLoader());

				//Se obtienen los drivers
				Class[] writers = cl.getWriters();
				writersClassLoaders.add(cl);
				//Se asocian los drivers con su tipo si superan la validación
				for (int j = 0; j < writers.length; j++) {
					try {
						Driver driver = (Driver) writers[j].newInstance();

						if (validation.validate(driver)) {
							if (nombreWriterClass.put(driver.getName(),
										writers[j]) != null) {
								throw new IllegalStateException(
									"Two drivers with the same name");
							}
						}
					} catch (ClassCastException e) {
						/*
						 * No todos los que terminan en Driver son drivers
						 * de los nuestros, los ignoramos
						 */
					} catch (Throwable t) {
						/*
						 * Aún a riesgo de capturar algo que no debemos, ignoramos cualquier driver que pueda
						 * dar cualquier tipo de problema, pero continuamos
						 */
						failures.add(t);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			failures.add((Throwable) e);
		} catch (IOException e) {
			failures.add((Throwable) e);
		}
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Throwable[] getLoadFailures() {
		return (Throwable[]) failures.toArray(new Throwable[0]);
	}

	/**
	 * Obtiene el Writer asociado al tipo que se le pasa como parámetro
	 *
	 * @param name Objeto que devolvió alguno de los writers en su método
	 * 		  getType
	 *
	 * @return El writer asociado o null si no se encuentra el driver
	 *
	 * @throws DriverLoadException if this Class represents an abstract class,
	 * 		   an interface, an array class, a primitive type, or void; or if
	 * 		   the class has no nullary constructor; or if the instantiation
	 * 		   fails for some other reason
	 */
	public Driver getWriter(String name) throws DriverLoadException {
		try {
			Class driverClass = (Class) nombreWriterClass.get(name);
			if (driverClass == null) throw new DriverLoadException("No se encontró el writer: " + name);
			return (Driver) driverClass.newInstance();
		} catch (InstantiationException e) {
			throw new DriverLoadException();
		} catch (IllegalAccessException e) {
			throw new DriverLoadException();
		}
	}

	/**
	 * Establece el objeto validador de los drivers. En la carga se comprobará
	 * si cada driver es válido mediante el método validate del objeto
	 * validation establecido con este método. Pro defecto se validan todos
	 * los drivers
	 *
	 * @param validation objeto validador
	 */
	public void setValidation(DriverValidation validation) {
		this.validation = validation;
	}

	/**
	 * Obtiene los tipos de todos los writers del sistema
	 *
	 * @return DOCUMENT ME!
	 */
	public String[] getWriterNames() {
		ArrayList names = new ArrayList(nombreWriterClass.size());

		Iterator iterator = nombreWriterClass.keySet().iterator();

		while (iterator.hasNext()) {
			names.add((String) iterator.next());
		}

		return (String[]) names.toArray(new String[0]);
	}

	/**
	 * Obtiene la clase del writer relacionado con el tipo que se pasa como
	 * parámetro
	 *
	 * @param driverName DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Class getWriterClassByName(String writerName) {
		return (Class) nombreWriterClass.get(writerName);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param writerName DOCUMENT ME!
	 * @param superClass DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws RuntimeException DOCUMENT ME!
	 */
	public boolean isA(String writerName, Class superClass) {
		Class writerClass = (Class) nombreWriterClass.get(writerName);

		if (writerClass == null) {
			throw new RuntimeException("No such driver");
		}

		Class[] interfaces = writerClass.getInterfaces();

		if (interfaces != null) {
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i] == superClass) {
					return true;
				} else {
					if (recursiveIsA(interfaces[i], superClass)) {
						return true;
					}
				}
			}
		}

		Class class_ = writerClass.getSuperclass();

		if (class_ != null) {
			if (class_ == superClass) {
				return true;
			} else {
				if (recursiveIsA(class_, superClass)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param interface_ DOCUMENT ME!
	 * @param superInterface DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	private boolean recursiveIsA(Class interface_, Class superInterface) {
		Class[] interfaces = interface_.getInterfaces();

		if (interfaces != null) {
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i] == superInterface) {
					return true;
				} else {
					if (recursiveIsA(interfaces[i], superInterface)) {
						return true;
					}
				}
			}
		}

		Class class_ = interface_.getSuperclass();

		if (class_ != null) {
			if (class_ == superInterface) {
				return true;
			} else {
				if (recursiveIsA(class_, superInterface)) {
					return true;
				}
			}
		}

		return false;
	}
	public ArrayList getWriterClassLoaders() {
		return writersClassLoaders;
	}
}
