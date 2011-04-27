Notas de compilaci�n de la librer�a jmrsid.

La compilaci�n de la librer�a jmrsid se realiza autom�ticamente ejecutando el build.xml
del proyecto. Para ello el sistema tiene que tener instalado y accesible:

- CMake - Probado con la versi�n 2.4
- Compilador gcc - g++ en el caso de Linux - La versi�n de dicho compilador tiene que ser
  compatible con el SDK que disponga el usuario. Se ha compilado con gcc 3.3
- Entorno de desarrollo Visual Studio en caso de Windows con las variables de
  entorno cargadas. La versi�n del compilador tambi�n depende del SDK que el ususario
  tenga. - Compilado con Visual Studio 2005
- SDK de LizardTech instalado y accesible. Si el SDK est� en alguna ruta distinta a las que
  se incluyen en el archivo FindMRSID, habr� que a�adirla a dicho archivo o instalar el SDK
  en alguna de las indicadas.
- JDK de Java.

Con ejecutar el build.xml es suficiente para generar la librer�a y el jar.