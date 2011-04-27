package com.iver.cit.gvsig.fmap.core;


/**
 * This factory is used to create geoemtries with the M and Z coordinates
 * 
 * @author Flavio Pompermaier (flavio.pompermaier@sinergis.it)
 */
public class ShapeZMFactory {
	//TODO implement
	//		public static IGeometry createPoint3DM(double x, double y,double z, double m) {
	//			return new FGeometryM(new FPoint3DM(x, y, z, m));
	//		}
	
	public static IGeometryM createPolyline3DM(FPolyline3DM polyline) {
		return new FGeometryM(polyline);
	}
	
	//TODO implement
	//		public static IGeometry createPolyline3DM(ByteBuffer data) {
	//
	//			int count = data.getInt();
	//			GeneralPathX gp = new GeneralPathX();
	//			//double[] ms = new double[count - 1];
	//			//ArrayList alMs = new ArrayList();
	//
	//			ArrayList<Double> ms = new ArrayList<Double>();
	//			//		        ArrayList<Double> ms_aux = null;
	//			//		        double[] ms = null;      //Intento de evitar el tener que encapsular las m's en
	//			//		        double[] ms_aux = null;  //objetos Double y de tener que recorrer un ArrayList
	//			int ms_lentgh = 0;
	//
	//			for (int i=0; i < count; i++) {
	//				parseTypeAndSRID(data);
	//				FPoint2DM[] points = parsePointArray(data);
	//				//		            ms_aux = new double[ms_lentgh + points.length];
	//
	//				gp.moveTo(points[0].getX(), points[0].getY());
	//				//alMs.add(new Double(points[0].getM()));
	//				//		            ms_aux[ms_lentgh + 0] = points[0].getM();
	//				ms.add(points[0].getM());
	//
	//				for (int j = 1; j< points.length; j++) {
	//					ms.add(points[j].getM());
	//					//		             ms_aux[ms_lentgh + j] = points[j].getM();
	//					gp.lineTo(points[j].getX(), points[j].getY());
	//				} 
	//
	//				//ms[i] = points[i].getM();
	//				//		            if (ms != null) {
	//				//		             System.arraycopy(ms, 0, ms_aux, ms.length, ms.length);
	//				//		            }
	//				//		            ms = ms_aux;
	//				//		            ms_lentgh = ms.length;
	//				//		            ms_aux = null;
	//			}//for
	//
	//
	//			// OJO: Para ahorrarme esto tendría que modificar la clase FPolyline2DM para
	//			//      que las ms se almacenaran como objetos Double en lugar de usar el tipo
	//			//      primitivo double.
	//			double[] aMs = new double[ms.size()];
	//			for (int i = 0; i < ms.size(); i++) {
	//				aMs[i] = ((Double)ms.get(i)).doubleValue();
	//			}
	//
	//			return new FGeometryM(new FPolyline3DM(gp, aMs));
	//		}
	
	public static IGeometry createPolygon3DM(GeneralPathX shape, double[] pZ, double[] pM) {
		throw new UnsupportedOperationException();
	}
	
	public static IGeometry createMultipoint3DM(double[] x, double[] y, double[] z, double[] m) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a Polyline in 3D with the Z and M coordinates.
	 * 
	 * @param shape
	 *            Coordinates to create the polyline
	 * @param pZ
	 *            Vector de Z.
	 * @param pM
	 *            Vector de M.
	 * @return Geometría.
	 * @author Flavio Pompermaier
	 */
	public static IGeometry createPolyline3DM(GeneralPathX shape, double[] pZ, double[] pM) {
		return new FGeometry(new FPolyline3DM(shape, pZ, pM));
	}
}