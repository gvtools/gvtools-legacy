package es.gva.cit.jmrsid;

import java.io.IOException;

import junit.framework.TestCase;
import es.gva.cit.jmrsid.LTIMetadataDatabase;
import es.gva.cit.jmrsid.LTIMetadataRecord;
import es.gva.cit.jmrsid.MrSIDException;
import es.gva.cit.jmrsid.MrSIDImageReader;

/**
 * Test de lectura de jmrsid.
 * @author Miguel ¡ngel Querol Carratal· <miguelangel.querol@iver.es>
 *
 */
public class TestReadMrSid extends TestCase{

	private MrSIDImageReader		sid = null;
	
	private String 					fileName = "Urban_10cm.sid";
	private String 					baseDir = "./test-images/";
	private String 					file1 = baseDir + fileName;
	
	private int						numBands = 0;
	private int 					width = 0;
	private int						height = 0;
	private String					projection = null;
	private LTIMetadataDatabase		metadata = null;
	private LTIMetadataRecord		record = null;
	
	public void start(){
		setUp();
		testStack();
	}
	
	
	public void setUp(){
		try {
			sid = new MrSIDImageReader(file1);
			sid.initialize();
			
		} catch (MrSIDException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void testStack(){
		try {
			numBands = sid.getNumBands();
			width = sid.getWidth();
			height = sid.getHeight();
			metadata = sid.getMetadata();
			System.out.println("**** TEST DE ACCESO A IM√ÅGENES MRSID ****");
			System.out.println("     IMAGEN: " + fileName);
			System.out.println("N√∫mero de bandas: " + numBands);
			System.out.println("Anchura: " + width);
			System.out.println("Altura: " + height);
			
//			for (int i = 0 ; i<metadata.getIndexCount() ; i++){
//				record = metadata.getDataByIndex(i);
//				System.out.println(record.getTagName() + ": " + record.getScalarData());
//			}
			sid.close();
		} catch (MrSIDException e) {
			e.printStackTrace();
		}
		
	}
	
}
