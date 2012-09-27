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


public class IdrCassiniSoldner extends MapProjection {

    private final double latitudeOfOrigin;

    protected IdrCassiniSoldner(ParameterValueGroup parameters) throws ParameterNotFoundException {
		super(parameters);
        final Collection expected = getParameterDescriptors().descriptors();
        if (expected.contains(Provider.LATITUDE_OF_ORIGIN)) {
        	latitudeOfOrigin = Math.abs(doubleValue(expected,
                                        Provider.LATITUDE_OF_ORIGIN, parameters));
            ensureLatitudeInRange(Provider.LATITUDE_OF_ORIGIN, latitudeOfOrigin, false);
        } else {
            // standard parallel is the equator (Plate Carree or Equirectangular)
        	latitudeOfOrigin = Double.NaN;
        }
		// TODO Auto-generated constructor stub
	}

	
	public ParameterDescriptorGroup getParameterDescriptors() {
		// TODO Auto-generated method stub
        return Provider.PARAMETERS;
	}

    public ParameterValueGroup getParameterValues() {
        final ParameterValueGroup values = super.getParameterValues();
        if (!Double.isNaN(latitudeOfOrigin)) {
            final Collection expected = getParameterDescriptors().descriptors();
            set(expected,Provider.LATITUDE_OF_ORIGIN, values, latitudeOfOrigin);
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

        public static final ParameterDescriptor LATITUDE_OF_ORIGIN = createDescriptor(
                new NamedIdentifier[] {
                    new NamedIdentifier(Citations.OGC,     "latitude_of_origin"),
                    new NamedIdentifier(Citations.EPSG,    "CenterLat"),
                    new NamedIdentifier(Citations.EPSG,    "Latitude of projection centre"),
                    new NamedIdentifier(Citations.GEOTIFF, "NatOriginLat"),
                    new NamedIdentifier(Citations.EPSG,    "FalseOriginLat"),
                    new NamedIdentifier(Citations.EPSG,    "Latitude of false origin"),		
                    new NamedIdentifier(Citations.EPSG,    "Latitude of natural origin"),
                    new NamedIdentifier(Citations.EPSG,    "Latitude of projection centre"),
                    new NamedIdentifier(Citations.EPSG,    "ProjCenterLat")
                }, 0.0, -90.0, 90.0, NonSI.DEGREE_ANGLE);

        /**
         * The parameters group. Note the EPSG includes a "Latitude of natural origin" parameter instead
         * of "standard_parallel_1". I have sided with ESRI and Snyder in this case.
         */
        static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,      "Cassini_Soldner"),
    			new NamedIdentifier(Citations.EPSG,     "Cassini-Soldner"),
                new NamedIdentifier(Citations.EPSG,     "Peters (approximated by Gall Orthographic)"),
                new NamedIdentifier(Citations.EPSG,     "9806"),
    			new NamedIdentifier(Citations.EPSG,     "Cassini"),
                new NamedIdentifier(new CitationImpl("IDR"), "IDR")//,
//                new NamedIdentifier(CitationImpl.GEOTOOLS, Vocabulary.formatInternational(
//                                    VocabularyKeys.EQUIDISTANT_CYLINDRICAL_PROJECTION))
            }, new ParameterDescriptor[] {
                SEMI_MAJOR,       SEMI_MINOR,
                CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
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

        protected Provider(final ParameterDescriptorGroup params) {
            super(params);
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
            return new IdrCassiniSoldner(parameters);
            //return null;
        	//return new EquidistantCylindrical(parameters);
        }
    }
	public static class Provider_Hyperbolic extends Provider {
 
	       public static final ParameterDescriptor LATITUDE_OF_ORIGIN = createDescriptor(
	                new NamedIdentifier[] {
	                    new NamedIdentifier(Citations.OGC,     "latitude_of_origin"),
	                    new NamedIdentifier(Citations.EPSG,    "CenterLat"),
	                    new NamedIdentifier(Citations.EPSG,    "Latitude of projection centre"),
	                    new NamedIdentifier(Citations.GEOTIFF, "NatOriginLat"),
	                    new NamedIdentifier(Citations.EPSG,    "FalseOriginLat"),
	                    new NamedIdentifier(Citations.EPSG,    "Latitude of false origin"),		
	                    new NamedIdentifier(Citations.EPSG,    "Latitude of natural origin"),
	                    new NamedIdentifier(Citations.EPSG,    "Latitude of projection centre"),
	                    new NamedIdentifier(Citations.EPSG,    "ProjCenterLat")
	                }, 0.0, -90.0, 90.0, NonSI.DEGREE_ANGLE);

	        /**
	         * The parameters group. Note the EPSG includes a "Latitude of natural origin" parameter instead
	         * of "standard_parallel_1". I have sided with ESRI and Snyder in this case.
	         */
	        static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
	                new NamedIdentifier(Citations.OGC,      "Hyperbolic Cassini-Soldner"),
	    			new NamedIdentifier(Citations.EPSG,     "Hyperbolic_Cassini-Soldner"),
	                new NamedIdentifier(Citations.EPSG,     "Cassini-Soldner (Hyperbolic)"),
	                new NamedIdentifier(Citations.EPSG,     "Cassini-Soldner_(Hyperbolic)"),
	                new NamedIdentifier(new CitationImpl("IDR"), "IDR")//,
//	                new NamedIdentifier(CitationImpl.GEOTOOLS, Vocabulary.formatInternational(
//	                                    VocabularyKeys.EQUIDISTANT_CYLINDRICAL_PROJECTION))
	            }, new ParameterDescriptor[] {
	                SEMI_MAJOR,       SEMI_MINOR,
	                CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
	                FALSE_EASTING,    FALSE_NORTHING
	            });


	          /**
	         * Constructs a new provider.
	         */
	        public Provider_Hyperbolic() {
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
	            //return null;
	        	return new IdrCassiniSoldner(parameters);
	        }
	    }

}
