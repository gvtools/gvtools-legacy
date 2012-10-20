package com.hardcode.gdbms.control;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.SAXException;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.customQuery.QueryManager;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.instruction.Adapter;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.engine.instruction.Utilities;
import com.hardcode.gdbms.engine.strategies.SumQuery;
import com.hardcode.gdbms.gui.Frame;
import com.hardcode.gdbms.gui.GDBMSAdapterTreeModel;
import com.hardcode.gdbms.gui.GDBMSParseTreeModel;
import com.hardcode.gdbms.gui.GDBMSTableModel;
import com.hardcode.gdbms.gui.ParseTreeFrame;
import com.hardcode.gdbms.parser.Node;
import com.hardcode.gdbms.parser.ParseException;
import com.hardcode.gdbms.parser.SQLEngine;
import com.hardcode.gdbms.parser.SimpleNode;
import com.hardcode.gdbms.parser.TokenMgrError;

/**
 * Aplicación principal
 * 
 * @author Fernando González Cortés
 */
public class GDBMSMain {
	private DataSourceFactory ds;

	/**
	 * Inicializa los subsistemas
	 * 
	 * @throws InstantiationException
	 *             Si se produce algún error al inicializar los drivers
	 * @throws IllegalAccessException
	 *             Si se produce algún error al inicializar los drivers
	 * @throws ClassNotFoundException
	 *             Si se produce algún error al inicializar los drivers
	 * @throws DriverLoadException
	 */
	private void setup() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, DriverLoadException {
		// Setup del log4j
		PropertyConfigurator.configure(this.getClass().getClassLoader()
				.getResource("log4j.properties"));

		// Setup del factory de DataSources
		ds = new DataSourceFactory();

		// Setup de las tablas
		ds.addFileDataSource("gdbms dbf driver", "vias",
				"C:\\Documents and Settings\\fernando\\Mis documentos\\province.dbf");
		ds.addFileDataSource("gdbms dbf driver", "vias",
				"/root/cartografia/win/vias.dbf");

		// addDataSource(new FooReadDriver(), "foo");
		/*
		 * addDBDataSource("mysql", "192.168.0.1", -1, "root", "root", "mysql",
		 * new String[]{"*"}, "user", null, "mysql");
		 */
		ds.addDBDataSourceByTable("pb", "127.0.0.1", 5432, "root", "",
				"sigusal", "pbedifihist", "postgresql");
		ds.addDBDataSourceByTable("person", "www.freesql.org", 3306,
				"fergonco", "fergonco", "fergonco", "person", "mysql");
		ds.addDBDataSourceByTable("fernando", "www.freesql.org", 3306,
				"fergonco", "fergonco", "fergonco", "person", "mysql");
		ds.addDBDataSourceBySQL(
				"ages",
				"www.freesql.org",
				3306,
				"elfernan",
				"elfernan",
				"elfernan",
				"select 3*p1.age as tripleage from person p1, person p2, person p3, person p4, person p5",
				"mysql");

		// CustomQueries
		QueryManager.registerQuery(new SumQuery());
	}

	/**
	 * Muestra por la consola el contenido de un DataSource
	 * 
	 * @param ds
	 *            DataSource a mostrar
	 * @throws ReadDriverException
	 *             TODO
	 */
	private void mostrar(DataSource ds) throws ReadDriverException {
		// Se muestra la tabla
		ds.start();

		ds.getRowCount();

		Frame f = new Frame();
		f.setTableModel(new GDBMSTableModel(ds));
		f.show();

		/*
		 * StringBuffer aux = new StringBuffer(); int fc = ds.getFieldCount();
		 * int rc = ds.getRowCount();
		 * 
		 * for (int i = 0; i < fc; i++) {
		 * aux.append(ds.getFieldName(i)).append("\t"); }
		 * System.out.println(aux); try { for (int row = 0; row < rc; row++) {
		 * aux.setLength(0); for (int j = 0; j < fc; j++) {
		 * aux.append(ds.getFieldValue(row, j)).append("\t"); }
		 * //System.out.println(aux); } } catch (Throwable t) {
		 * t.printStackTrace(); } finally { ds.close(); }
		 */
	}

	/**
	 * Bucle principal
	 * 
	 * @throws DriverLoadException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws ReadDriverException
	 *             TODO
	 * @throws IOException
	 *             TODO
	 */
	private void run() throws DriverLoadException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			ReadDriverException, IOException {
		setup();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String sql = "";
		System.out.print("> ");
		sql = in.readLine();

		while (!sql.equals("quit")) {
			long t1 = System.currentTimeMillis();
			ByteArrayInputStream bytes = new ByteArrayInputStream(
					sql.getBytes());
			SQLEngine parser = new SQLEngine(bytes);

			try {
				parser.SQLStatement();

				Node root = parser.getRootNode();
				Adapter rootAdapter = Utilities.buildTree(root.jjtGetChild(0),
						sql, ds);

				ParseTreeFrame parseTree = new ParseTreeFrame();
				parseTree.setTreeModel(new GDBMSParseTreeModel(
						(SimpleNode) root));
				parseTree.show();

				ParseTreeFrame parseTree2 = new ParseTreeFrame();
				parseTree2.setTreeModel(new GDBMSAdapterTreeModel(rootAdapter));
				parseTree2.show();
				Utilities.simplify(rootAdapter);

				/*
				 * ParseTreeFrame parseTree3 = new ParseTreeFrame();
				 * parseTree3.setTreeModel(new
				 * GDBMSAdapterTreeModel(rootAdapter)); parseTree3.show();
				 */
				/*
				 * obtenerXML(root); InstructionHandler handler = new
				 * InstructionHandler(); SemanticParser sp = new
				 * SemanticParser(); sp.setContentHandler(handler);
				 * sp.parse(root); SelectInstruction s = handler.getInstr();
				 */
				DataSource result = null;
				long t2 = System.currentTimeMillis();

				result = ds
						.executeSQL(sql, DataSourceFactory.AUTOMATIC_OPENING);

				if (result != null) {
					mostrar(result);
				}

				long t3 = System.currentTimeMillis();

				System.out.println("Tiempo de parseado: "
						+ ((t2 - t1) / 1000.0) + " segundos");
				System.out.println("Tiempo de ejecución: "
						+ ((t3 - t2) / 1000.0) + " segundos");
				System.out.println("Tiempo de parseado y ejecución: "
						+ ((t3 - t1) / 1000.0) + " segundos");
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (TokenMgrError e) {
				e.printStackTrace();
			} catch (SemanticException e1) {
				e1.printStackTrace();
			} catch (DriverLoadException e) {
				e.printStackTrace();
			} catch (EvaluationException e) {
				e.printStackTrace();
			}

			System.out.print("> ");
			sql = in.readLine();
		}
	}

	/**
	 * Hace que el visitor sp visite el nodo actual y cada uno de sus hijos
	 * 
	 * @param root
	 *            nodo raiz que se va a visitar
	 * @param sp
	 *            visitante
	 */
	private void parse(Node root, SemanticParser sp) {
		root.jjtAccept(sp, null);

		for (int i = 0; i < root.jjtGetNumChildren(); i++) {
			parse(root.jjtGetChild(i), sp);
		}
	}

	/**
	 * metodo de entrada
	 * 
	 * @param args
	 *            Si se produce algún error al inicializar los drivers
	 * @throws SAXException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws DriverLoadException
	 * @throws ReadDriverException
	 *             TODO
	 */
	public static void main(String[] args) throws SAXException, IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, DriverLoadException, ReadDriverException {
		GDBMSMain db = new GDBMSMain();
		db.run();
	}
}
