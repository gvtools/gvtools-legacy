

package com.iver.cit.gvsig.geoprocess.core;

import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;


public class ViewTools {

   public static ProjectView getViewFromLayer(final FLayer layer) {

      final Project project = ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject();
      final ArrayList<ProjectDocument> views = project.getDocumentsByType(ProjectViewFactory.registerName);

      for (int i = 0; i < views.size(); i++) {
         final ProjectView view = (ProjectView) views.get(i);
         final FLayers flayers = view.getMapContext().getLayers();
         for (int j = 0; j < flayers.getLayersCount(); j++) {
            final FLayer lyr = flayers.getLayer(j);
            if (lyr.equals(layer)) {
               return view;
            }

            final ArrayList layerList = new ArrayList();
            splitLayerGroup(lyr, layerList);
            for (int k = 0; k < layerList.size(); k++) {
               final FLayer lyr2 = ((FLayer) layerList.get(k));
               if (lyr2.equals(layer)) {
                  return view;
               }
            }
         }
      }

      return null;

   }


   private static void splitLayerGroup(final FLayer layer,
                                       final ArrayList result) {
      int i;
      FLayers layerGroup;
      if (layer instanceof FLayers) {
         layerGroup = (FLayers) layer;
         for (i = 0; i < layerGroup.getLayersCount(); i++) {
            splitLayerGroup(layerGroup.getLayer(i), result);
         }
      }
      else {
         result.add(layer);
      }
   }

}
