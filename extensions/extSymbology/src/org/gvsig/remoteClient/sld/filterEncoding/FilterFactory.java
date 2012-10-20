package org.gvsig.remoteClient.sld.filterEncoding;

import org.gvsig.symbology.fmap.rendering.filter.operations.AddOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.AndOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.BooleanConstant;
import org.gvsig.symbology.fmap.rendering.filter.operations.DivOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.DoubleConstant;
import org.gvsig.symbology.fmap.rendering.filter.operations.EqOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.Expression;
import org.gvsig.symbology.fmap.rendering.filter.operations.GreaterThanOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.GreaterThanOrEqualsOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.IsBetweenOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.IsNullOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.LessThanOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.LessThanOrEqualsOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.MinusOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.MultOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.NeOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.NotOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.NullConstant;
import org.gvsig.symbology.fmap.rendering.filter.operations.OrOperator;
import org.gvsig.symbology.fmap.rendering.filter.operations.ReplaceOperator;

import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;

/**
 * Implements a class that allows the user to create an XML document from an
 * expression
 * 
 * @see http://www.opengeospatial.org/standards/filter
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */

public class FilterFactory {

	public String createXMLFromExpression(Expression filter4Symbol) {

		XmlBuilder xmlBuilder = new XmlBuilder();

		if (filter4Symbol instanceof AndOperator)
			xmlBuilder.openTag(FilterTags.AND);
		else if (filter4Symbol instanceof OrOperator)
			xmlBuilder.openTag(FilterTags.OR);
		else if (filter4Symbol instanceof NotOperator)
			xmlBuilder.openTag(FilterTags.NOT);
		else if (filter4Symbol instanceof EqOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISEQUALTO);
		else if (filter4Symbol instanceof NeOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISNOTEQUALTO);
		else if (filter4Symbol instanceof AddOperator)
			xmlBuilder.openTag(FilterTags.ADD);
		else if (filter4Symbol instanceof MinusOperator)
			xmlBuilder.openTag(FilterTags.SUB);
		else if (filter4Symbol instanceof MultOperator)
			xmlBuilder.openTag(FilterTags.MULT);
		else if (filter4Symbol instanceof DivOperator)
			xmlBuilder.openTag(FilterTags.DIV);
		else if (filter4Symbol instanceof GreaterThanOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISGREATERTHAN);
		else if (filter4Symbol instanceof GreaterThanOrEqualsOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISGREATEROREQUALTHAN);
		else if (filter4Symbol instanceof LessThanOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISLESSTHAN);
		else if (filter4Symbol instanceof LessThanOrEqualsOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISLESSOREQUALTHAN);
		else if (filter4Symbol instanceof IsBetweenOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISBETWEEN);
		else if (filter4Symbol instanceof IsNullOperator)
			xmlBuilder.openTag(FilterTags.PROPERTYISNULL);
		else if (filter4Symbol instanceof ReplaceOperator) {
			ReplaceOperator replace = (ReplaceOperator) filter4Symbol;
			xmlBuilder.writeTag(FilterTags.PROPERTYNAME, replace.getValue());
			return xmlBuilder.getXML();
		} else if (filter4Symbol instanceof BooleanConstant) {
			BooleanConstant myBool = (BooleanConstant) filter4Symbol;
			xmlBuilder.writeTag(FilterTags.LITERAL,
					String.valueOf(myBool.getValue()));
			return xmlBuilder.getXML();
		} else if (filter4Symbol instanceof DoubleConstant) {
			DoubleConstant myDouble = (DoubleConstant) filter4Symbol;
			xmlBuilder.writeTag(FilterTags.LITERAL,
					String.valueOf(myDouble.getValue()));
			return xmlBuilder.getXML();
		} else if (filter4Symbol instanceof NullConstant) {
			xmlBuilder.writeTag(FilterTags.LITERAL, "null");
			return xmlBuilder.getXML();
		}

		for (int i = 0; i < filter4Symbol.getArguments().size(); i++) {
			xmlBuilder.writeRaw(createXMLFromExpression(filter4Symbol
					.getArguments().get(i)));
		}

		xmlBuilder.closeTag();
		return xmlBuilder.getXML();
	}

}
