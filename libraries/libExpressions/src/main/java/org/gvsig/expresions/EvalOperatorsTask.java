package org.gvsig.expresions;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.gui.EvalExpressionDialog;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;

public class EvalOperatorsTask extends AbstractMonitorableTask{
	private ExtensionPoint extensionPoint;
	private Table table=null;
	private FLyrVect lyr;
	public static final String JYTHON="jython";
	public static BSFManager interpreter=new BSFManager();
	private static ArrayList<IOperator> operators=new ArrayList<IOperator>();
	public EvalOperatorsTask(FLyrVect lyr){
		this.lyr = lyr;
		setInitialStep(0);
		setDeterminatedProcess(true);
		setStatusMessage(PluginServices.getText(this, "charging_operators")+"...");
		ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
		extensionPoint =(ExtensionPoint)extensionPoints.get("ColumnOperatorsExtension");
		setFinalStep(extensionPoint.size()+1);
	}
	public void run() throws Exception {
		NotificationManager.addInfo(PluginServices.getText(this,"charging_operators"));
		long t1=System.currentTimeMillis();
	        Iterator iterator = extensionPoint.keySet().iterator();
	        while (iterator.hasNext()) {
	            try {
	            	if (isCanceled())
						return;
	                IOperator operator = (IOperator)extensionPoint.create((String)iterator.next());

	                operator.eval(interpreter);
	                operators.add(operator);
	                reportStep();
	                setNote(operator.getClass().getName());
	            } catch (InstantiationException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            } catch (ClassCastException e) {
	                e.printStackTrace();
	            }
	        }
	        long t2=System.currentTimeMillis();
	        System.out.println("Tiempo en evaluar y crear del extension point = "+(t2-t1) );
	        long t3=System.currentTimeMillis();
	        System.out.println("Tiempo en añadir los operadores correctos = "+(t3-t2) );
	        reportStep();
	}
	public void finished() {
		if (isCanceled())
			return;
		NotificationManager.addInfo(PluginServices.getText(this,"charged_operators"));
		EvalExpressionDialog eed=new EvalExpressionDialog(lyr,interpreter,operators);
//		EvalExpressionDialog eed=new EvalExpressionDialog(table,interpreter,operators);
        PluginServices.getMDIManager().addWindow(eed);
	}

}
