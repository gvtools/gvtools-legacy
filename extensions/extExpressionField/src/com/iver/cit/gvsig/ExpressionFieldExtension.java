package com.iver.cit.gvsig;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.gvsig.gui.beans.incrementabletask.IIncrementable;
import org.gvsig.gui.beans.incrementabletask.IncrementableEvent;
import org.gvsig.gui.beans.incrementabletask.IncrementableListener;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;
import org.gvsig.gui.beans.progresspanel.LogControl;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.gui.FieldExpressionPage;
import com.iver.cit.gvsig.project.documents.table.IOperator;
import com.iver.cit.gvsig.project.documents.table.gui.EvalExpressionDialog;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.table.operators.Abs;
import com.iver.cit.gvsig.project.documents.table.operators.Acos;
import com.iver.cit.gvsig.project.documents.table.operators.After;
import com.iver.cit.gvsig.project.documents.table.operators.Area;
import com.iver.cit.gvsig.project.documents.table.operators.Asin;
import com.iver.cit.gvsig.project.documents.table.operators.Atan;
import com.iver.cit.gvsig.project.documents.table.operators.Before;
import com.iver.cit.gvsig.project.documents.table.operators.Ceil;
import com.iver.cit.gvsig.project.documents.table.operators.Cos;
import com.iver.cit.gvsig.project.documents.table.operators.DateToString;
import com.iver.cit.gvsig.project.documents.table.operators.Distinct;
import com.iver.cit.gvsig.project.documents.table.operators.Division;
import com.iver.cit.gvsig.project.documents.table.operators.E;
import com.iver.cit.gvsig.project.documents.table.operators.EndsWith;
import com.iver.cit.gvsig.project.documents.table.operators.Equal;
import com.iver.cit.gvsig.project.documents.table.operators.Equals;
import com.iver.cit.gvsig.project.documents.table.operators.Exp;
import com.iver.cit.gvsig.project.documents.table.operators.Floor;
import com.iver.cit.gvsig.project.documents.table.operators.Geometry;
import com.iver.cit.gvsig.project.documents.table.operators.GetTimeDate;
import com.iver.cit.gvsig.project.documents.table.operators.IndexOf;
import com.iver.cit.gvsig.project.documents.table.operators.IsNumber;
import com.iver.cit.gvsig.project.documents.table.operators.LastIndexOf;
import com.iver.cit.gvsig.project.documents.table.operators.Length;
import com.iver.cit.gvsig.project.documents.table.operators.LessEquals;
import com.iver.cit.gvsig.project.documents.table.operators.LessThan;
import com.iver.cit.gvsig.project.documents.table.operators.Log;
import com.iver.cit.gvsig.project.documents.table.operators.Max;
import com.iver.cit.gvsig.project.documents.table.operators.Min;
import com.iver.cit.gvsig.project.documents.table.operators.Minus;
import com.iver.cit.gvsig.project.documents.table.operators.MoreEquals;
import com.iver.cit.gvsig.project.documents.table.operators.MoreThan;
import com.iver.cit.gvsig.project.documents.table.operators.Perimeter;
import com.iver.cit.gvsig.project.documents.table.operators.Pi;
import com.iver.cit.gvsig.project.documents.table.operators.Plus;
import com.iver.cit.gvsig.project.documents.table.operators.PointX;
import com.iver.cit.gvsig.project.documents.table.operators.PointY;
import com.iver.cit.gvsig.project.documents.table.operators.Pow;
import com.iver.cit.gvsig.project.documents.table.operators.Random;
import com.iver.cit.gvsig.project.documents.table.operators.Replace;
import com.iver.cit.gvsig.project.documents.table.operators.Round;
import com.iver.cit.gvsig.project.documents.table.operators.SetTimeDate;
import com.iver.cit.gvsig.project.documents.table.operators.Sin;
import com.iver.cit.gvsig.project.documents.table.operators.Sqrt;
import com.iver.cit.gvsig.project.documents.table.operators.StartsWith;
import com.iver.cit.gvsig.project.documents.table.operators.SubString;
import com.iver.cit.gvsig.project.documents.table.operators.Tan;
import com.iver.cit.gvsig.project.documents.table.operators.Times;
import com.iver.cit.gvsig.project.documents.table.operators.ToDate;
import com.iver.cit.gvsig.project.documents.table.operators.ToDegrees;
import com.iver.cit.gvsig.project.documents.table.operators.ToLowerCase;
import com.iver.cit.gvsig.project.documents.table.operators.ToNumber;
import com.iver.cit.gvsig.project.documents.table.operators.ToRadians;
import com.iver.cit.gvsig.project.documents.table.operators.ToString;
import com.iver.cit.gvsig.project.documents.table.operators.ToUpperCase;
import com.iver.cit.gvsig.project.documents.table.operators.Trim;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;
/**
 * @author Vicente Caballero Navarro
 */
public class ExpressionFieldExtension extends Extension{
	//private static Interpreter interpreter=new Interpreter();
	public static final String JYTHON="jython";
	private static BSFManager interpreter=new BSFManager();
	private Table table=null;
	private static ArrayList<IOperator> operators=new ArrayList<IOperator>();
	public void initialize() {
		registerOperations();
		registerIcons();
	}

	public void execute(String actionCommand) {
		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
		table=(Table)window;
		if (operators.isEmpty()) {
			PluginServices.cancelableBackgroundExecution(new EvalOperatorsTask());
        }else{
        	 EvalExpressionDialog eed=new EvalExpressionDialog(table,interpreter,operators);
		     PluginServices.getMDIManager().addWindow(eed);
        }
	}
	public void postInitialize() {

	}

	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof Table) {
			Table table=(Table)window;
			BitSet columnSelected = table.getSelectedFieldIndices();
		    if (!columnSelected.isEmpty() && table.isEditing()) {
		    	return true;
		    }
		}
		return false;
	}

	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof Table) {
			return true;
		}
		return false;
	}
	 private void registerOperations() {
	    	ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();

	    	extensionPoints.add("cad_editing_properties_pages","fieldExpression",FieldExpressionPage.class);

	    	extensionPoints.add("ColumnOperators",Abs.class.toString(),Abs.class);
	        extensionPoints.add("ColumnOperators",Acos.class.toString(),Acos.class);
	        extensionPoints.add("ColumnOperators",After.class.toString(),After.class);
	        extensionPoints.add("ColumnOperators",Area.class.toString(),Area.class);
	        extensionPoints.add("ColumnOperators",Asin.class.toString(),Asin.class);
	        extensionPoints.add("ColumnOperators",Atan.class.toString(),Atan.class);
	        extensionPoints.add("ColumnOperators",Acos.class.toString(),Acos.class);
	        extensionPoints.add("ColumnOperators",Before.class.toString(),Before.class);
	        extensionPoints.add("ColumnOperators",Ceil.class.toString(),Ceil.class);
	        extensionPoints.add("ColumnOperators",Cos.class.toString(),Cos.class);
	    	extensionPoints.add("ColumnOperators",DateToString.class.toString(),DateToString.class);
	    	extensionPoints.add("ColumnOperators",Distinct.class.toString(),Distinct.class);
	     	extensionPoints.add("ColumnOperators",Division.class.toString(),Division.class);
	     	extensionPoints.add("ColumnOperators",E.class.toString(),E.class);
	     	extensionPoints.add("ColumnOperators",EndsWith.class.toString(),EndsWith.class);
	     	extensionPoints.add("ColumnOperators",Equal.class.toString(),Equal.class);
	     	extensionPoints.add("ColumnOperators",Equals.class.toString(),Equals.class);
	     	extensionPoints.add("ColumnOperators",Exp.class.toString(),Exp.class);
	        extensionPoints.add("ColumnOperators",Floor.class.toString(),Floor.class);
	     	extensionPoints.add("ColumnOperators",Geometry.class.toString(),Geometry.class);
	     	extensionPoints.add("ColumnOperators",GetTimeDate.class.toString(),GetTimeDate.class);
	     	extensionPoints.add("ColumnOperators",IndexOf.class.toString(),IndexOf.class);
	     	extensionPoints.add("ColumnOperators",IsNumber.class.toString(),IsNumber.class);
	     	extensionPoints.add("ColumnOperators",LastIndexOf.class.toString(),LastIndexOf.class);
	     	extensionPoints.add("ColumnOperators",Length.class.toString(),Length.class);
	     	extensionPoints.add("ColumnOperators",LessEquals.class.toString(),LessEquals.class);
	     	extensionPoints.add("ColumnOperators",LessThan.class.toString(),LessThan.class);
	     	extensionPoints.add("ColumnOperators",Log.class.toString(),Log.class);
	     	extensionPoints.add("ColumnOperators",Max.class.toString(),Max.class);
	     	extensionPoints.add("ColumnOperators",Min.class.toString(),Min.class);
	     	extensionPoints.add("ColumnOperators",Minus.class.toString(),Minus.class);
	     	extensionPoints.add("ColumnOperators",MoreEquals.class.toString(),MoreEquals.class);
	     	extensionPoints.add("ColumnOperators",MoreThan.class.toString(),MoreThan.class);
	     	extensionPoints.add("ColumnOperators",Perimeter.class.toString(),Perimeter.class);
	     	extensionPoints.add("ColumnOperators",Pi.class.toString(),Pi.class);
	    	extensionPoints.add("ColumnOperators",Plus.class.toString(),Plus.class);
	     	extensionPoints.add("ColumnOperators",PointX.class.toString(),PointX.class);
	     	extensionPoints.add("ColumnOperators",PointY.class.toString(),PointY.class);
	     	extensionPoints.add("ColumnOperators",Pow.class.toString(),Pow.class);
	     	extensionPoints.add("ColumnOperators",Random.class.toString(),Random.class);
	     	extensionPoints.add("ColumnOperators",Replace.class.toString(),Replace.class);
	     	extensionPoints.add("ColumnOperators",Round.class.toString(),Round.class);
	     	extensionPoints.add("ColumnOperators",SetTimeDate.class.toString(),SetTimeDate.class);
	     	extensionPoints.add("ColumnOperators",Sin.class.toString(),Sin.class);
	     	extensionPoints.add("ColumnOperators",Sqrt.class.toString(),Sqrt.class);
	     	extensionPoints.add("ColumnOperators",StartsWith.class.toString(),StartsWith.class);
	     	extensionPoints.add("ColumnOperators",SubString.class.toString(),SubString.class);
	     	extensionPoints.add("ColumnOperators",Tan.class.toString(),Tan.class);
	    	extensionPoints.add("ColumnOperators",Times.class.toString(),Times.class);
	    	extensionPoints.add("ColumnOperators",ToDate.class.toString(),ToDate.class);
	    	extensionPoints.add("ColumnOperators",ToDegrees.class.toString(),ToDegrees.class);
	    	extensionPoints.add("ColumnOperators",ToLowerCase.class.toString(),ToLowerCase.class);
	    	extensionPoints.add("ColumnOperators",ToNumber.class.toString(),ToNumber.class);
	    	extensionPoints.add("ColumnOperators",ToRadians.class.toString(),ToRadians.class);
	    	extensionPoints.add("ColumnOperators",ToString.class.toString(),ToString.class);
	    	extensionPoints.add("ColumnOperators",ToUpperCase.class.toString(),ToUpperCase.class);
	    	extensionPoints.add("ColumnOperators",Trim.class.toString(),Trim.class);
	 }

	 private void registerIcons(){
		 PluginServices.getIconTheme().registerDefault(
					"ext-kcalc",
					this.getClass().getClassLoader().getResource("images/kcalc.png")
				);

		 PluginServices.getIconTheme().registerDefault(
					"field-expression-kcalc",
					this.getClass().getClassLoader().getResource("images/FieldExpression.png")
				);
	 }

	 private class EvalOperatorsTask extends AbstractMonitorableTask{
	    	private ExtensionPoint extensionPoint;
	    	public EvalOperatorsTask(){
				setInitialStep(0);
				setDeterminatedProcess(true);
				setStatusMessage(PluginServices.getText(this, "charging_operators")+"...");
				ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
				extensionPoint =(ExtensionPoint)extensionPoints.get("ColumnOperators");
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
				EvalExpressionDialog eed=new EvalExpressionDialog(table,interpreter,operators);
		        PluginServices.getMDIManager().addWindow(eed);
			}

	 }
}
