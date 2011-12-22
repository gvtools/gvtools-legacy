

package es.unex.sextante.vectorTools.symDifference;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import es.unex.sextante.additionalInfo.AdditionalInfoVectorLayer;
import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.dataObjects.vectorFilters.BoundingBoxFilter;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.IteratorException;
import es.unex.sextante.exceptions.RepeatedParameterNameException;
import es.unex.sextante.outputs.OutputVectorLayer;


public class SymDifferenceAlgorithm
         extends
            GeoAlgorithm {

   public static final String LAYER     = "LAYER";
   public static final String CLIPLAYER = "CLIPLAYER";
   public static final String RESULT    = "RESULT";

   private IVectorLayer       m_Output;
   private Geometry           m_ClipGeometry;


   @Override
   public boolean processAlgorithm() throws GeoAlgorithmExecutionException {

      final IVectorLayer layerIn = m_Parameters.getParameterValueAsVectorLayer(LAYER);
      final IVectorLayer layerClip = m_Parameters.getParameterValueAsVectorLayer(CLIPLAYER);

      if (!m_bIsAutoExtent) {
         layerIn.addFilter(new BoundingBoxFilter(m_AnalysisExtent));
         layerClip.addFilter(new BoundingBoxFilter(m_AnalysisExtent));
      }

      m_ClipGeometry = computeJtsClippingPoly(layerClip);

      m_Output = getNewVectorLayer(RESULT, Sextante.getText("Symmetric_difference"), layerIn.getShapeType(),
               layerIn.getFieldTypes(), layerIn.getFieldNames());

      final IFeatureIterator iter = layerIn.iterator();

      int i = 0;
      final int iShapeCount = layerIn.getShapesCount();
      while (iter.hasNext() && setProgress(i, iShapeCount)) {
         final IFeature feature = iter.next();
         final Geometry g = symDifference(feature.getGeometry());
         if (g != null) {
            m_Output.addFeature(g, feature.getRecord().getValues());
         }
         i++;
      }
      iter.close();

      return !m_Task.isCanceled();

   }


   @Override
   public void defineCharacteristics() {

      setName(Sextante.getText("Symmetric_difference"));
      setGroup(Sextante.getText("Tools_for_polygon_layers"));
      setUserCanDefineAnalysisExtent(true);

      try {
         m_Parameters.addInputVectorLayer(LAYER, Sextante.getText("Primera_capa"), AdditionalInfoVectorLayer.SHAPE_TYPE_POLYGON,
                  true);
         m_Parameters.addInputVectorLayer(CLIPLAYER, Sextante.getText("Segunda_capa"),
                  AdditionalInfoVectorLayer.SHAPE_TYPE_POLYGON, true);
         addOutputVectorLayer(RESULT, Sextante.getText("Symmetric_difference"), OutputVectorLayer.SHAPE_TYPE_POLYGON);
      }
      catch (final RepeatedParameterNameException e) {
         Sextante.addErrorToLog(e);
      }

   }


   public Geometry symDifference(final Geometry g) throws GeoAlgorithmExecutionException {

      if (g == null) {
         return null;
      }

      final Geometry env = g.getEnvelope();
      if (env == null) {
         return null;
      }
      if (!env.intersects(m_ClipGeometry.getEnvelope())) {
         return null;
      }
      if (g.intersects(m_ClipGeometry)) {
         try {
            final Geometry newGeom = g.symDifference(m_ClipGeometry);
            return newGeom;
         }
         catch (final com.vividsolutions.jts.geom.TopologyException e) {
            if (!g.isValid()) {
               throw new GeoAlgorithmExecutionException("Wrong input geometry");
            }
            if (!m_ClipGeometry.isValid()) {
               throw new GeoAlgorithmExecutionException("Wrong clipping geometry");
            }
         }
      }
      return null;
   }


   private Geometry computeJtsClippingPoly(final IVectorLayer layer) throws IteratorException {

      Geometry currentGeometry;
      Geometry geometry = null;
      final GeometryFactory geomFact = new GeometryFactory();

      final IFeatureIterator iter = layer.iterator();
      while (iter.hasNext()) {
         final IFeature feature = iter.next();
         currentGeometry = feature.getGeometry();
         if (geometry == null) {
            geometry = currentGeometry;
         }
         else {
            final Geometry[] geoms = new Geometry[2];
            geoms[0] = geometry;
            geoms[1] = currentGeometry;
            final GeometryCollection gc = geomFact.createGeometryCollection(geoms);
            geometry = gc.buffer(0d);
         }
      }
      iter.close();

      return geometry;

   }


}
