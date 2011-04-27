package com.hardcode.gdbms.engine.data.edition;

import junit.framework.TestCase;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;


/**
 * test cases for PKTable
 *
 * @author Fernando González Cortés
 */
public class PKDataStructureTest extends TestCase {
	private PKTable pkt;
	private int n;
	private ValueCollection[] pks;

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		pkt = new PKTable();
		n = 10;
		pks = new ValueCollection[n];

		for (int i = 0; i < n; i++) {
			pks[i] = ValueFactory.createValue(new Value[]{ValueFactory.createValue(i)});
			pkt.addPK(i, pks[i], i);
		}
	}

	/**
	 * Tests the deletion of pk's
	 */
	public void testDelete() {
		pkt.deletePK(4);
		pkt.deletePK(4);
		pkt.deletePK(7);

		assertTrue(pkt.getPKCount() == (n - 3));
		assertTrue(pkt.getPK(0) == pks[0]);
		assertTrue(pkt.getPK(1) == pks[1]);
		assertTrue(pkt.getPK(2) == pks[2]);
		assertTrue(pkt.getPK(3) == pks[3]);
		assertTrue(pkt.getPK(4) == pks[6]);
		assertTrue(pkt.getPK(5) == pks[7]);
		assertTrue(pkt.getPK(6) == pks[8]);

		assertTrue(pkt.getDeletedPKCount() == 3);
		assertTrue(pkt.getDeletedPK(0).getPk() == pks[4]);
		assertTrue(pkt.getDeletedPK(1).getPk() == pks[5]);
		assertTrue(pkt.getDeletedPK(2).getPk() == pks[9]);
	}

	/**
	 * Tests the update of the pk's location
	 */
	public void testUpdate() {
		pkt.updatePK(0, 1000);
		pkt.updatePK(1, 1000);
		pkt.updatePK(2, 3000);

		assertTrue(pkt.getIndexLocation(0).getIndex() == 1000);
		assertTrue(pkt.getIndexLocation(1).getIndex() == 1000);
		assertTrue(pkt.getIndexLocation(2).getIndex() == 3000);
		assertTrue(pkt.getIndexLocation(0).getFlag() == PKTable.MODIFIED);
		assertTrue(pkt.getIndexLocation(1).getFlag() == PKTable.MODIFIED);
		assertTrue(pkt.getIndexLocation(2).getFlag() == PKTable.MODIFIED);
	}

	/**
	 * Tests the adition of pks
	 */
	public void testAdds() {
		ValueCollection[] newPk = new ValueCollection[3];
		newPk[0] = ValueFactory.createValue(new Value[]{ValueFactory.createValue(10)});
		newPk[1] = ValueFactory.createValue(new Value[]{ValueFactory.createValue(11)});
		newPk[2] = ValueFactory.createValue(new Value[]{ValueFactory.createValue(12)});
		pkt.addPK(newPk[0], 1000);
		pkt.addPK(newPk[1], 2000);
		pkt.addPK(newPk[2], 3000);

		assertTrue(pkt.getPKCount() == 13);
		assertTrue(pkt.getIndexLocation(10).getIndex() == 1000);
		assertTrue(pkt.getIndexLocation(11).getIndex() == 2000);
		assertTrue(pkt.getIndexLocation(12).getIndex() == 3000);
		assertTrue(pkt.getIndexLocation(10).getFlag() == PKTable.ADDED);
		assertTrue(pkt.getIndexLocation(11).getFlag() == PKTable.ADDED);
		assertTrue(pkt.getIndexLocation(12).getFlag() == PKTable.ADDED);
	}

	/**
	 * Tests the sequential processing of the data structure after some
	 * operations
	 */
	public void testEndTransaction() {
		ValueCollection[] newPk = new ValueCollection[3];
		newPk[0] = ValueFactory.createValue(new Value[]{ValueFactory.createValue(10)});
		newPk[1] = ValueFactory.createValue(new Value[]{ValueFactory.createValue(11)});
		newPk[2] = ValueFactory.createValue(new Value[]{ValueFactory.createValue(12)});
		pkt.deletePK(4);
		pkt.deletePK(4);
		pkt.deletePK(7);
		pkt.updatePK(0, 1000);
		pkt.updatePK(1, 1000);
		pkt.updatePK(2, 3000);
		pkt.addPK(newPk[0], 1000);
		pkt.addPK(newPk[1], 2000);
		pkt.addPK(newPk[2], 3000);

		assertTrue(pkt.getPKCount() == 10);
		assertTrue(pkt.getPK(0) == pks[0]);
		assertTrue(pkt.getPK(1) == pks[1]);
		assertTrue(pkt.getPK(2) == pks[2]);
		assertTrue(pkt.getPK(3) == pks[3]);
		assertTrue(pkt.getPK(4) == pks[6]);
		assertTrue(pkt.getPK(5) == pks[7]);
		assertTrue(pkt.getPK(6) == pks[8]);
		assertTrue(pkt.getPK(7) == newPk[0]);
		assertTrue(pkt.getPK(8) == newPk[1]);
		assertTrue(pkt.getPK(9) == newPk[2]);
	}
}
