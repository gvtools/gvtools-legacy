// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JNCSFile.java
// [01] 01-Oct-2005 nbt New call to JNI function ECWOpenArray to convert name string to char array.

package com.ermapper.ecw;

import com.ermapper.util.JNCSDatasetPoint;
import com.ermapper.util.JNCSWorldPoint;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

// Referenced classes of package com.ermapper.ecw:
//            JNCSNativeLibraryException, JNCSFileOpenFailedException, JNCSInvalidSetViewException, JNCSException, 
//            JNCSFileNotOpenException, JNCSProgressiveUpdate

/**
 * Clase que representa a un fichero Ecw
 */

public class JNCSFile
    implements JNCSProgressiveUpdate
{

    private native int ECWOpen(String s, boolean flag);
    
    private native int ECWOpenArray(String s, boolean flag, byte[] b);

    private native void ECWClose(boolean flag);

    private native int ECWSetView(int i, int ai[], int j, int k, int l, int i1, double d, double d1, double d2, double d3, 
            int j1, int k1);

    private native int ECWReadLineRGBA(int ai[]);

    private native int ECWReadImageRGBA(int ai[], int i, int j);

    private native short ECWGetPercentComplete();

    private static native String ECWGetErrorString(int i);

    private static native int NCSJNIInit();

    private static native String ECWGetLibVersion();

    /**
     * Carga la libreria libecw.so que contiene el wrapper y llama a la
     * función de inicialización
     * @throws JNCSNativeLibraryException
     */
    
    static void initClass()
        throws JNCSNativeLibraryException
    {
    	 boolean flag = false;
         boolean flag1 = false;


         try
         {
             System.loadLibrary("jecw");
             bUseNativeMethods = true;
             bUnsatisfiedLink = false;
             int i = NCSJNIInit();
             if(i != 0)
             {
                 System.err.println("JNCSFile classes found on PATH failed to initialize correctly. Attempting to locate other libecw.so....");
             }
         }
         catch(Exception e)
         {
         	e.printStackTrace();
         }
    }

    /**
     * Constructor
     * @throws JNCSException
     */
    
    public JNCSFile()
        throws JNCSException
    {
        bSetViewIsWorld = false;
        progImageClient = null;
        initClass();
        cellSizeUnits = 0;
        bIsOpen = false;
    }

    /**
     * Constructor con open del fichero.
     * @throws JNCSException
     */
    
    public JNCSFile(String s, boolean flag)
        throws JNCSException
    {
        bSetViewIsWorld = false;
        progImageClient = null;
        initClass();
        open(s, flag);
    }

    /**
     * Llama al close del ecw para liberar la memoria de C.
     * @throws Throwable
     */
    
    protected void finalize()
        throws Throwable
    {
        if(bIsOpen)
            ECWClose(false);
        super.finalize();
    }

    /**
     * Abre el fichero de imagen.
     * @throws JNCSFileOpenFailedException
     */
    
    public int open(String s, boolean flag)
        throws JNCSFileOpenFailedException
    {
        if(s == null)
            throw new IllegalArgumentException();
        
        //int i = ECWOpen(s, flag);
        int i = ECWOpenArray(s, flag, s.getBytes());
        
        if(i != 0)
        {
            bIsOpen = false;
            String s1 = JNCSError.getError(i);
            throw new JNCSFileOpenFailedException(s1);
        } else
        {
            bIsOpen = true;
            progressive = flag;
            return 0;
        }
    }

    /**
     * Cierra el fichero de imagen.
     * @param flag	parámetro para la función NCScbmCloseFileViewEx
     */
    
    public void close(boolean flag)
    {
        ECWClose(flag);
        if(!flag);
    }
    
    public void addProgressiveUpdateListener(JNCSProgressiveUpdate jncsprogressiveupdate)
    {
        progImageClient = jncsprogressiveupdate;
    }

    public void refreshUpdate(int i, int j, double d, double d1, double d2, double d3)
    {
        if(progImageClient != null)
            progImageClient.refreshUpdate(i, j, d, d1, d2, d3);
    }

    public void refreshUpdate(int i, int j, int k, int l, int i1, int j1)
    {
        if(progImageClient != null)
            progImageClient.refreshUpdate(i, j, k, l, i1, j1);
    }

    /**
     * Crea una vista en un fichero ecw abierto dando las coordenadas de la vista
     * @param nBands Número de bandas en bandList
     * @param bandList Array de índices de bandas
     * @param width ancho de la vista en pixels
     * @param height alto de la vista en pixels
     * @param tlx coordenada X arriba-izquierda de la vista
     * @param tly coordenada Y arriba-izquierda de la vista
     * @param brx coordenada X abajo-derecha de la vista
     * @param bry coordenada Y abajo-derecha de la vista
     * @throws JNCSFileNotOpenException, JNCSInvalidSetViewException
     */
    
    public int setView(int i, int ai[], int j, int k, int l, int i1, int j1,
            int k1)
        throws JNCSFileNotOpenException, JNCSInvalidSetViewException
    {
    	if(!bIsOpen)
    		throw new JNCSFileNotOpenException("File not open");
    	
    	if (ai == null)
    		throw new JNCSInvalidSetViewException("Wrong parameter value");
    	
    	
        int l1 = ECWSetView(i, ai, j, k, l, i1, 0.0D, 0.0D, 0.0D, 0.0D, j1, k1);
        if(l1 != 0)
        {
            //String s = ECWGetErrorString(l1);
            String s = JNCSError.getError(l1);
            throw new JNCSInvalidSetViewException(s);
        } else
        {
            bSetViewIsWorld = false;
            return 0;
        }
    }

    /**
     * Asigna la vista pasando por parámetros los pixeles de inicio y fin y coordenadas de georreferenciación solicitadas-
     * El cliente de esta función tendrá que convertir las coordenadas reales en pixeles.
     */
    public int setView(int nBands, int posBands[], int iniX, int iniY, int endX, int endY, double tlX, double tlY, double brX, double brY, 
            int bufW, int bufH)throws JNCSFileNotOpenException, JNCSInvalidSetViewException{
    	
    	if(!bIsOpen)
    		throw new JNCSFileNotOpenException("File not open");
    	
    	if (posBands == null)
    		throw new JNCSInvalidSetViewException("Wrong parameter value");
    	
    	int l = ECWSetView(nBands, posBands, iniX, iniY, endX, endY, tlX, tlY, brX, brY, bufW, bufH);
        if(l != 0){
            String s = JNCSError.getError(l);
            throw new JNCSInvalidSetViewException(s);
        }else{
            bSetViewIsWorld = true;
            return 0;
        }

    }
    
    /**
     * Crea una vista en un fichero ecw abierto.
     *  
     * @param nBands Número de bandas en bandList
     * @param bandList Array de índices de bandas
     * @param width ancho de la vista en pixels
     * @param height alto de la vista en pixels
     * @param dWorldTLX coordenada X arriba-izquierda)
     * @param dWorldTLY coordenada Y arriba-izquierda
     * @param dWorldBRX coordenada X abajo-derecha)
     * @param dWorldBRY coordenada Y abajo-derecha
     * @throws JNCSFileNotOpenException, JNCSInvalidSetViewException
     */
    
    public int setView(int i, int ai[], double d, double d1, double d2, double d3, int j, int k)
        throws JNCSFileNotOpenException, JNCSInvalidSetViewException
    {
    	if(!bIsOpen)
    		throw new JNCSFileNotOpenException("File not open");
    	
    	if (ai == null)
    		throw new JNCSInvalidSetViewException("Wrong parameter value");
        JNCSDatasetPoint jncsdatasetpoint = convertWorldToDataset(d, d1);
        JNCSDatasetPoint jncsdatasetpoint1 = convertWorldToDataset(d2, d3);
        int l = ECWSetView(i, ai, jncsdatasetpoint.x, jncsdatasetpoint.y, jncsdatasetpoint1.x - 1, jncsdatasetpoint1.y - 1, d, d1, d2, d3, j, k);
        if(l != 0)
        {
            //String s = ECWGetErrorString(l);
            String s = JNCSError.getError(l);
            throw new JNCSInvalidSetViewException(s);
        } else
        {
            bSetViewIsWorld = true;
            return 0;
        }
    }

    /**
     * Lee una línea del fichero Ecw
     * @param buffer	Buffer donde se almacenan los datos de la línea
     * @throws JNCSException
     */
    
    public int readLineRGBA(int ai[])
        throws JNCSException
    {
    	if(!bIsOpen)
    		throw new JNCSFileNotOpenException("File not open");
        int i = ECWReadLineRGBA(ai);
        if(i != 0)
        {
            String s = JNCSError.getError(i);
            throw new JNCSException(s);
        } else
        {
            return 0;
        }
    }

    public int readLineBGRA(int ai[])
        throws JNCSException
    {
        throw new JNCSException("Not Yet Implemented!");
    }

    public int readLineBIL(int ai[])
        throws JNCSException
    {
        throw new JNCSException("Not Yet Implemented!");
    }

    public int readLineBIL(double ad[])
        throws JNCSException
    {
        throw new JNCSException("Not Yet Implemented!");
    }

    public int readImageRGBA(int ai[], int i, int j)
        throws JNCSException
    {
    	if(!bIsOpen)
    		throw new JNCSFileNotOpenException("File not open");
        int k = ECWReadImageRGBA(ai, i, j);
        if(k != 0)
        {
            String s = JNCSError.getError(k);
            throw new JNCSException(s);
        } else
        {
            return 0;
        }
    }

    /**
     * Obtiene una cadena que corresponde a un error a través del entero que lo representa
     * @return String	Cadena de error
     * @param error	Entero que representa el error
     */
    
    public String getLastErrorText(int i)
    {
        return JNCSError.getError(i);
    }

    /**
     * Convierte una coordenada del mundo real a coordenadas de la vista
     * @return JNCSDatasetPoint	Clase que representa a un punto en la imagen
     * @param x	Coordenada X del mundo real
     * @param y Coordenada Y del mundo real
     * @throws JNCSFileNotOpenException
     */
    
    public JNCSDatasetPoint convertWorldToDataset(double d, double d1)
        throws JNCSFileNotOpenException
    {
        int i;
        int j;
        if(bIsOpen)
        {
            i = (int)Math.round((d - originX) / cellIncrementX);
            j = (int)Math.round((d1 - originY) / cellIncrementY);
        } else
        {
            throw new JNCSFileNotOpenException();
        }
        return new JNCSDatasetPoint(i, j);
    }

    /**
     * Convierte una coordenada de la vista a coordenadas del mundo real
     * @return JNCSWorldPoint	Clase que representa una coordenada del mundo real
     * @param x	Coordenada X de la imagen
     * @param y Coordenada Y de la imagen
     * @throws JNCSFileNotOpenException
     */
    
    public JNCSWorldPoint convertDatasetToWorld(int i, int j)
        throws JNCSFileNotOpenException
    {
        double d;
        double d1;
        if(bIsOpen)
        {
            d = originX + (double)i * cellIncrementX;
            d1 = originY + (double)j * cellIncrementY;
        } else
        {
            throw new JNCSFileNotOpenException();
        }
        return new JNCSWorldPoint(d, d1);
    }

    public short getPercentComplete()
    {
        return ECWGetPercentComplete();
    }

    /**
     * Obtiene una cadena con la versión de la libreria
     * @return versión
     */
    
    public static String getLibVersion()
    {
        return ECWGetLibVersion();
    }

    private static void debug(String s)
    {
        if(debug)
            System.out.println(s);
    }

    private static boolean bUseNativeMethods = false;
    private static boolean bSecurityError = false;
    private static boolean bUnsatisfiedLink = false;
    static boolean bHaveClassInit = false;
    static boolean debug = false;
    public static final int ECW_CELL_UNITS_INVALID = 0;
    public static final int ECW_CELL_UNITS_METERS = 1;
    public static final int ECW_CELL_UNITS_DEGREES = 2;
    public static final int ECW_CELL_UNITS_FEET = 3;
    public int numBands;
    public int width;
    public int height;
    public double originX;
    public double originY;
    public double cellIncrementX;
    public double cellIncrementY;
    public int cellSizeUnits;
    public double compressionRate;
    public boolean progressive;
    public String fileName;
    public String datum;
    public String projection;
    public boolean bIsOpen;
    private long nativeDataPointer;
    private static final boolean doGarbageCollectionOnClose = false;
    private static final int ECW_OK = 0;
    private int nFileSetViewDatasetTLX;
    private int nFileSetViewDatasetTLY;
    private int nFileSetViewDatasetBRX;
    private int nFileSetViewDatasetBRY;
    private int nFileSetViewWidth;
    private int nFileSetViewHeight;
    private double dFileSetViewWorldTLX;
    private double dFileSetViewWorldTLY;
    private double dFileSetViewWorldBRX;
    private double dFileSetViewWorldBRY;
    private boolean bSetViewIsWorld;
    protected JNCSProgressiveUpdate progImageClient;

}
