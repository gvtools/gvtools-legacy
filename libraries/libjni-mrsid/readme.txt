Notas de compilación de la librería jmrsid.

La compilación de la librería jmrsid se realiza automáticamente ejecutando el build.xml
del proyecto. Para ello el sistema tiene que tener instalado y accesible:

- CMake - Probado con la versión 2.4
- Compilador gcc - g++ en el caso de Linux - La versión de dicho compilador tiene que ser
  compatible con el SDK que disponga el usuario. Se ha compilado con gcc 3.3
- Entorno de desarrollo Visual Studio en caso de Windows con las variables de
  entorno cargadas. La versión del compilador también depende del SDK que el ususario
  tenga. - Compilado con Visual Studio 2005
- SDK de LizardTech instalado y accesible. Si el SDK está en alguna ruta distinta a las que
  se incluyen en el archivo FindMRSID, habrá que añadirla a dicho archivo o instalar el SDK
  en alguna de las indicadas.
- JDK de Java.

Con ejecutar el build.xml es suficiente para generar la librería y el jar.