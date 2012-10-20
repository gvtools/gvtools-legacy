package com.ermapper.ecw;

import junit.framework.TestCase;
import es.gva.cit.jecwcompress.CompressFormat;
import es.gva.cit.jecwcompress.EcwException;
import es.gva.cit.jecwcompress.JniObject;
import es.gva.cit.jecwcompress.NCSEcwCompressClient;
import es.gva.cit.jecwcompress.ReadCallBack;

public class TestCompressImage extends TestCase {

	private JNCSFile file = null;

	private String fileName = "miniraster30x30.jp2";
	private String outFileName = "compressTestOutput.jp2";
	private String baseDir = "./test-images/";
	private String file1 = baseDir + fileName;
	private String outFile = baseDir + outFileName;

	private NCSEcwCompressClient client = null;
	private Read lectura = null;

	private int numBands = 0;
	private int xSize = 0;
	private int ySize = 0;

	public void start() {
		setUp();
		testStack();
	}

	public void setUp() {
		try {
			file = new JNCSFile();
			file.open(file1, true);
			numBands = file.numBands;
			xSize = file.width;
			ySize = file.height;
		} catch (JNCSException e) {
		}

		try {
			client = new NCSEcwCompressClient();

			client.setOutputFilename(outFile);
			client.setInputFilename(file1);
			client.setTargetCompress(new Double(10.0));
			client.setInOutSizeX(xSize);
			client.setInOutSizeY(ySize);
			client.setInputBands(numBands);

			client.setCompressFormat(CompressFormat.COMPRESS_NONE);

			client.setProjection("WGS84");
			client.setCellIncrementX(file.cellIncrementX);
			client.setCellIncrementY(file.cellIncrementY);
			client.setOriginX(file.originX);
			client.setOriginY(file.originY);
			client.setCellSizeUnits(1);

			lectura = new Read(file, client, 0, ySize, xSize, ySize);
			client.NCSEcwCompressOpen(false);
			client.NCSEcwCompress(lectura);
			client.NCSEcwCompressClose();

		} catch (EcwException e) {
			assertNotNull(client);
		}

	}

	public void testStack() {

	}
}

/**
 * 
 * Para la lectura hay que hacer una clase que extienda de JniObject e
 * implemente ReadCallBack . Esto obliga a crear un método loadBuffer. En el hay
 * que meter la funcionalidad para que llene el buffer. El buffer esta en la
 * clase cliente y tendrá una longitud de ancho de una línea * número de bandas
 */

class Read extends JniObject implements ReadCallBack {

	private JNCSFile miecw = null;
	private NCSEcwCompressClient client = null;
	private int width, height;
	private int ulX, ulY;
	private int[] readBandsFromECW = null;
	private int[] inputRow = null;

	public Read(JNCSFile ecw, NCSEcwCompressClient client, int ulx, int uly,
			int width, int height) {
		this.miecw = ecw;
		this.client = client;
		this.width = width;
		this.height = height;
		this.ulX = ulx;
		this.ulY = uly;
		readBandsFromECW = new int[Math.max(miecw.numBands, 3)];
		inputRow = new int[miecw.width];
	}

	public void loadBuffer() {
		try {
			readBandsFromECW = new int[] { 0, 1, 2 };
			miecw.setView(miecw.numBands, readBandsFromECW, miecw.originX,
					miecw.originY + (miecw.cellIncrementY * nNextLine),
					miecw.originX + (miecw.cellIncrementX * miecw.width),
					miecw.originY + (miecw.cellIncrementY * (nNextLine + 1)),
					miecw.width, 1);

			miecw.readLineRGBA(inputRow);
			for (int col = 0; col < inputRow.length; col++) {
				client.buffer[col + (width * 0)] = (byte) Math
						.abs(((inputRow[col] & 0x00ff0000) >> 16));
				client.buffer[col + (width * 1)] = (byte) Math
						.abs(((inputRow[col] & 0x0000ff00) >> 8));
				client.buffer[col + (width * 2)] = (byte) Math
						.abs((inputRow[col] & 0x000000ff));
			}
		} catch (JNCSFileNotOpenException e) {
		} catch (JNCSInvalidSetViewException e) {
		} catch (JNCSException e) {
		}
	}

	public void updatePercent() {
		System.out.println(client.getPercent() + "%");
	}
}
