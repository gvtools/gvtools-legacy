package com.iver.cit.gvsig.geoprocess.impl.clean.fmap;

import java.util.Comparator;

import com.iver.cit.gvsig.geoprocess.impl.clean.fmap.IntersectionNodeVisitor.LineIntersection;

public class LineIntersectionComparator implements Comparator<LineIntersection>{

	public int compare(LineIntersection o1, LineIntersection o2) {
		if (o1.lenght > o2.lenght)
			return 1;
		else if (o1.lenght < o2.lenght)
			return -1;
		else
			return 0;
	}

}
