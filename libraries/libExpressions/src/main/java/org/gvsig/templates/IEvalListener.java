package org.gvsig.templates;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;

public interface IEvalListener {
	
	public void saveEdits(int numRows) throws ReadDriverException, InitializeWriterException, StopWriterVisitorException;

}
