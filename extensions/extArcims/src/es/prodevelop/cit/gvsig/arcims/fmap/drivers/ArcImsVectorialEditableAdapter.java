package es.prodevelop.cit.gvsig.arcims.fmap.drivers;

import java.awt.geom.Rectangle2D;

import org.cresques.cts.IProjection;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.SpatialQueryFeatureIterator;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;

/**
 * We need to override one method to avoid an index problem with ArcIMS vector layers,
 * but keeping a VectorialEditableAdapter because we need it to create
 * the ProjectTable (?).
 *  
 * @author jldominguez
 *
 */

public class ArcImsVectorialEditableAdapter extends VectorialEditableAdapter {
	
	public ArcImsVectorialEditableAdapter() {
		super();
	}
	
	/**
	* Makes an spatial query returning a feature iterator over the features which intersects
	* or are contained in the rectangle query. Applies a restriction to the alphanumeric fields
	* returned by the iterator.
	* @param rect
	* @param fields
	* @return
	 * @throws ReadDriverException
	*/
	public IFeatureIterator getFeatureIterator(
			Rectangle2D rect,
			String[] fields,
			IProjection newProjection,
			boolean fastIteration) throws ReadDriverException {

		return new SpatialQueryFeatureIterator(
				this,
				projection,
				newProjection,
				fields,
				rect,
				fastIteration);
		
	}	

}
