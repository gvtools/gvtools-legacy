package es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle;

import java.sql.SQLException;

import oracle.sql.NUMBER;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.IConnection;

public class TestStCreator {
	
	private static int index = 0;
	private STRUCT[] st = new STRUCT[2];
	private static IConnection conn;
	
	public static STRUCT getStruct() {
		
		STRUCT resp = getStruct(index);
		index++;
		return resp;
	}
		
	private static STRUCT getStruct(int ind) {
		
		STRUCT resp = null;
        StructDescriptor dsc = null;
        
        Object[] obj = new Object[5];
        obj[0] = new NUMBER(2007);
        obj[1] = null;
        obj[2] = null;
        
		NUMBER[] indices = null;
		NUMBER[] ords = null;

        try {
			dsc = StructDescriptor.createDescriptor(
					"MDSYS.SDO_GEOMETRY",
					((ConnectionJDBC) conn).getConnection());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}


		if (ind == 0) {
			
			System.err.println("CREANDO STRUCT 0...");
			indices = new NUMBER[36];
			indices[0] = new NUMBER(1);
			indices[1] = new NUMBER(1005);
			indices[2] = new NUMBER(4);
			indices[3] = new NUMBER(1);
			indices[4] = new NUMBER(2);
			indices[5] = new NUMBER(2);
			indices[6] = new NUMBER(5);
			indices[7] = new NUMBER(2);
			indices[8] = new NUMBER(2);
			indices[9] = new NUMBER(9);
			indices[10] = new NUMBER(2);
			indices[11] = new NUMBER(2);
			indices[12] = new NUMBER(13);
			indices[13] = new NUMBER(2);
			indices[14] = new NUMBER(1);
			indices[15] = new NUMBER(181);
			indices[16] = new NUMBER(1003);
			indices[17] = new NUMBER(1);
			indices[18] = new NUMBER(315);
			indices[19] = new NUMBER(1003);
			indices[20] = new NUMBER(1);
			indices[21] = new NUMBER(409);
			indices[22] = new NUMBER(1005);
			indices[23] = new NUMBER(4);
			indices[24] = new NUMBER(409);
			indices[25] = new NUMBER(2);
			indices[26] = new NUMBER(2);
			indices[27] = new NUMBER(413);
			indices[28] = new NUMBER(2);
			indices[29] = new NUMBER(1);
			indices[30] = new NUMBER(621);
			indices[31] = new NUMBER(2);
			indices[32] = new NUMBER(1);
			indices[33] = new NUMBER(685);
			indices[34] = new NUMBER(2);
			indices[35] = new NUMBER(1);
			
			ords = new NUMBER[688];
			// =================================
			for (int i=0; i<89; i++) {
				ords[2 * i] = new NUMBER(5000 + i);
				ords[2 * i + 1] = new NUMBER(i*i);
			}
			ords[178] = new NUMBER(5000);
			ords[179] = new NUMBER(0);
			// =================================
			for (int i=90; i<156; i++) {
				ords[2 * i] = new NUMBER(6000 + i);
				ords[2 * i + 1] = new NUMBER(i*i);
			}
			ords[312] = new NUMBER(6000 + 90);
			ords[313] = new NUMBER(90*90);
			// =================================
			for (int i=157; i<202; i++) {
				ords[2 * i] = new NUMBER(7000 + i);
				ords[2 * i + 1] = new NUMBER(10 * (i % 2));
			}
			ords[404] = new NUMBER(7070);
			ords[405] = new NUMBER(500);
			ords[406] = new NUMBER(7157);
			ords[407] = new NUMBER(10 * (157 % 2));
			// =================================
			for (int i=204; i<342; i++) {
				ords[2 * i] = new NUMBER(8000 + i);
				ords[2 * i + 1] = new NUMBER(10 * (i % 2));
			}
			ords[684] = new NUMBER(8300);
			ords[685] = new NUMBER(500);
			ords[686] = new NUMBER(8204);
			ords[687] = new NUMBER(10 * (204 % 2));
			// =================================

		} else {
			
			System.err.println("CREANDO STRUCT 1...");
			indices = new NUMBER[30];
			indices[0] = new NUMBER(1);
			indices[1] = new NUMBER(1003);
			indices[2] = new NUMBER(1);
			indices[3] = new NUMBER(39);
			indices[4] = new NUMBER(1003);
			indices[5] = new NUMBER(1);
			indices[6] = new NUMBER(327);
			indices[7] = new NUMBER(1005);
			indices[8] = new NUMBER(6);
			indices[9] = new NUMBER(327);
			indices[10] = new NUMBER(2);
			indices[11] = new NUMBER(2);
			indices[12] = new NUMBER(331);
			indices[13] = new NUMBER(2);
			indices[14] = new NUMBER(1);
			indices[15] = new NUMBER(1011);
			indices[16] = new NUMBER(2);
			indices[17] = new NUMBER(2);
			indices[18] = new NUMBER(1015);
			indices[19] = new NUMBER(2);
			indices[20] = new NUMBER(2);
			indices[21] = new NUMBER(1019);
			indices[22] = new NUMBER(2);
			indices[23] = new NUMBER(2);
			indices[24] = new NUMBER(1023);
			indices[25] = new NUMBER(2);
			indices[26] = new NUMBER(1);
			indices[27] = new NUMBER(2033);
			indices[28] = new NUMBER(2003);
			indices[29] = new NUMBER(1);
			
			ords = new NUMBER[2040];
			// =================================
			for (int i=0; i<18; i++) {
				ords[2 * i] = new NUMBER(1000 + i);
				ords[2 * i + 1] = new NUMBER(i*i);
			}
			ords[36] = new NUMBER(1000);
			ords[37] = new NUMBER(0);
			// =================================
			for (int i=19; i<162; i++) {
				ords[2 * i] = new NUMBER(2000 + i);
				ords[2 * i + 1] = new NUMBER(i*i);
			}
			ords[324] = new NUMBER(2000 + 19);
			ords[325] = new NUMBER(19 * 19);
			// =================================
			for (int i=163; i<1014; i++) {
				ords[2 * i] = new NUMBER(3000 + i);
				ords[2 * i + 1] = new NUMBER(10 * (i % 2));
			}
			ords[2028] = new NUMBER(4000);
			ords[2029] = new NUMBER(100);
			ords[2030] = new NUMBER(3163);
			ords[2031] = new NUMBER(10 * (163 % 2));
			// =================================
			ords[2032] = new NUMBER(3500);
			ords[2033] = new NUMBER(-100);
			ords[2034] = new NUMBER(3600);
			ords[2035] = new NUMBER(-100);
			ords[2036] = new NUMBER(3500);
			ords[2037] = new NUMBER(-200);
			ords[2038] = new NUMBER(3500);
			ords[2039] = new NUMBER(-100);
			// =================================
		}
		
        obj[3] = indices;
        obj[4] = ords;
        
        try {
			resp = new STRUCT(dsc, ((ConnectionJDBC) conn).getConnection(), obj);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		return resp;
	}

	public static void init(IConnection _conn) {
		conn = _conn;
	}

}
