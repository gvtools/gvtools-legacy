Notas de compilación de la librería jpotrace.

La compilación de la librería jpotrace se realiza automáticamente ejecutando el
build.xml del proyecto. Para ello el sistema tiene que tener instalado y accesible:

Windows:
- CMake - Probado con la versión 2.6 en Windows.
- Entorno de desarrollo Visual Studio 2005 con las variables de entorno cargadas.
	Para cargar las variables de entorno de Visual Studio en Eclipse, mirar el
	fichero eclipse-vs8.bat en la carpeta resources
- Librería potrace instalada. Hasta el momento (0.0.1) se está compilando con
	potrace 1.8.0. Se encuentra en el directorio resources listo para compilar
	desde eclipse. Para compilarla hace falta tener instalado MinGW.
- JDK de Java.

Linux:
- CMake - Probado con la versión 2.4 en Windows.
- Compilador gcc - g++
- Compilado con gcc 4.1. Con la versión 3.3 de gcc - g++ no compila correctamente.
- Librería potrace instalada. Hasta el momento (0.0.1) se está compilando con
	potrace 1.8.0. Se encuentra en el directorio resources listo para compilar
	desde eclipse.
- JDK de Java.

Con ejecutar el build.xml es suficiente para generar la librería y el jar.