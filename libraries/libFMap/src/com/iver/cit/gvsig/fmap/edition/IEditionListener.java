package com.iver.cit.gvsig.fmap.edition;

import com.iver.cit.gvsig.fmap.core.IRow;

public interface IEditionListener {

	void processEvent(EditionEvent e);

	void beforeRowEditEvent(IRow feat,BeforeRowEditEvent e);

	void afterRowEditEvent(IRow feat, AfterRowEditEvent e);
	
	void beforeFieldEditEvent(BeforeFieldEditEvent e);
	void afterFieldEditEvent(AfterFieldEditEvent e);

}
