package org.geotools.referencing.operation.projection;

import java.awt.geom.Point2D;
import java.util.Collection;

import javax.measure.unit.NonSI;

import org.geotools.metadata.iso.citation.CitationImpl;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.NamedIdentifier;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.CylindricalProjection;
import org.opengis.referencing.operation.MathTransform;



public class IdrCylindricalEqualArea extends MapProjection {

    private final double standardParallel;

    protected IdrCylindricalEqualArea(ParameterValueGroup parameters) throws ParameterNotFoundException {
		super(parameters);
        final Collection expected = getParameterDescriptors().descriptors();
        if (expected.contains(Provider.STANDARD_PARALLEL)) {
            standardParallel = Math.abs(doubleValue(expected,
                                        Provider.STANDARD_PARALLEL, parameters));
            ensureLatitudeInRange(Provider.STANDARD_PARALLEL, standardParallel, false);
        } else {
            standardParallel = Double.NaN;
         }
	}

	
	public ParameterDescriptorGroup getParameterDescriptors() {
        return Provider.PARAMETERS;
	}

    public ParameterValueGroup getParameterValues() {
        final ParameterValueGroup values = super.getParameterValues();
        if (!Double.isNaN(standardParallel)) {
            final Collection expected = getParameterDescriptors().descriptors();
            set(expected,Provider.STANDARD_PARALLEL, values, standardParallel);
        }
        return values;
    }


	protected Point2D inverseTransformNormalized(double x, double y,
			Point2D ptDst) throws ProjectionException {
		// TODO Auto-generated method stub
		return null;
	}

	protected Point2D transformNormalized(double x, double y, Point2D ptDst)
			throws ProjectionException {
		// TODO Auto-generated method stub
		return null;
	}
	   
	public static class Provider extends AbstractProvider {

	        /**
	         * The operation parameter descriptor for the {@link #standardParallel standard parallel}
	         * parameter value. Valid values range is from -90 to 90. Default value is 0.
	         */
	    public static final ParameterDescriptor STANDARD_PARALLEL = createDescriptor(
	                new NamedIdentifier[] {
	                    new NamedIdentifier(Citations.OGC,      "standard_parallel_1"),
	                    new NamedIdentifier(Citations.EPSG,     "Latitude of 1st standard parallel"),
	                    new NamedIdentifier(Citations.EPSG,     "Latitude of natural origin"),
	                    new NamedIdentifier(Citations.GEOTIFF,  "StdParallel1")
	                },
	                0, -90, 90, NonSI.DEGREE_ANGLE);

	        /**
	         * The parameters group. Note the EPSG includes a "Latitude of natural origin" parameter instead
	         * of "standard_parallel_1". I have sided with ESRI and Snyder in this case.
	         */
	        static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
	                new NamedIdentifier(Citations.OGC,      "Cylindrical_Equal_Area"),
        			new NamedIdentifier(Citations.EPSG,     "Cylindrical Equal Area"),
                    new NamedIdentifier(Citations.EPSG,     "Normal Authalic Cylindrical (FME)"),
                    new NamedIdentifier(Citations.EPSG,     "Lambert Cylindrical Equal Area"),
                    new NamedIdentifier(Citations.EPSG,     "Behrmann (standard parallel = 30)"),
                    new NamedIdentifier(Citations.EPSG,     "Lambert Cylindrical Equal Area (Spherical)"),
                    new NamedIdentifier(Citations.EPSG,     "Gall Orthographic (standard parallel = 45)"),
                    new NamedIdentifier(Citations.EPSG,     "Peters (approximated by Gall Orthographic)"),
                    new NamedIdentifier(new CitationImpl("IDR"), "IDR")
	                //new NamedIdentifier(CitationImpl.EPSG,     "9823")//,
//	                new NamedIdentifier(CitationImpl.GEOTOOLS, Vocabulary.formatInternational(
//	                                    VocabularyKeys.EQUIDISTANT_CYLINDRICAL_PROJECTION))
	            }, new ParameterDescriptor[] {
	                SEMI_MAJOR,       SEMI_MINOR,
	                CENTRAL_MERIDIAN, STANDARD_PARALLEL,
	                FALSE_EASTING,    FALSE_NORTHING
	            });

			/*String[] parameterName={"central_meridian"};
			projectionParameterList.add(count,parameterName);
			addProjectionParameter(count,"standard_parallel_1");
			addProjectionParameter(count,"false_easting");
			addProjectionParameter(count,"false_northing");*/

	        /**
	         * Constructs a new provider.
	         */
	        public Provider() {
	            super(PARAMETERS);
	        }

	        /**
	         * Returns the operation type for this map projection.
	         */
	        public Class getOperationType() {
	        	return CylindricalProjection.class;
	        }

	        /**
	         * Creates a transform from the specified group of parameter values.
	         *
	         * @param  parameters The group of parameter values.
	         * @return The created math transform.
	         * @throws ParameterNotFoundException if a required parameter was not found.
	         */
	        public MathTransform createMathTransform(final ParameterValueGroup parameters)
	                throws ParameterNotFoundException
	        {
	        	return new IdrCylindricalEqualArea(parameters);
	        }
	    }

}
