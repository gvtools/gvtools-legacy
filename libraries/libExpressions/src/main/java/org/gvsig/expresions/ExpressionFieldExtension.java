package org.gvsig.expresions;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.bsf.BSFManager;
import org.gvsig.baseclasses.IOperator;
import org.gvsig.gui.EvalExpressionDialog;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;
/**
 * @author Vicente Caballero Navarro
 */
public class ExpressionFieldExtension {
	//private static Interpreter interpreter=new Interpreter();
//	public static final String JYTHON="jython";
//	public static BSFManager interpreter=new BSFManager();
//	private Table table=null;
//	private static ArrayList<IOperator> operators=new ArrayList<IOperator>();
//	public void initialize() {
//		registerOperations();
//		registerIcons();
//	}

//	public void execute(String actionCommand) {
//		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
//		table=(Table)window;
//		if (operators.isEmpty()) {
//			PluginServices.cancelableBackgroundExecution(new EvalOperatorsTask());
//        }else{
//        	 EvalExpressionDialog eed=new EvalExpressionDialog(table,interpreter,operators);
//		     PluginServices.getMDIManager().addWindow(eed);
//        }
//	}
//	public void postInitialize() {
//
//	}
//
//	public boolean isEnabled() {
//		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
//		if (window instanceof Table) {
//			Table table=(Table)window;
//			BitSet columnSelected = table.getSelectedFieldIndices();
//		    if (!columnSelected.isEmpty() && table.isEditing()) {
//		    	return true;
//		    }
//		}
//		return false;
//	}
//
//	public boolean isVisible() {
//		com.iver.andami.ui.mdiManager.IWindow window = PluginServices.getMDIManager().getActiveWindow();
//		if (window instanceof Table) {
//			return true;
//		}
//		return false;
//	}
//	 private void registerOperations() {
//	    	ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
//
//	    	extensionPoints.add("cad_editing_properties_pages","fieldExpression",FieldExpressionPage.class);
//
//	    	extensionPoints.add("ColumnOperators",Abs.class.toString(),Abs.class);
//	        extensionPoints.add("ColumnOperators",Acos.class.toString(),Acos.class);
//	        extensionPoints.add("ColumnOperators",After.class.toString(),After.class);
//	        extensionPoints.add("ColumnOperators",Area.class.toString(),Area.class);
//	        extensionPoints.add("ColumnOperators",Asin.class.toString(),Asin.class);
//	        extensionPoints.add("ColumnOperators",Atan.class.toString(),Atan.class);
//	        extensionPoints.add("ColumnOperators",Acos.class.toString(),Acos.class);
//	        extensionPoints.add("ColumnOperators",Before.class.toString(),Before.class);
//	        extensionPoints.add("ColumnOperators",Ceil.class.toString(),Ceil.class);
//	        extensionPoints.add("ColumnOperators",Cos.class.toString(),Cos.class);
//	    	extensionPoints.add("ColumnOperators",Distinct.class.toString(),Distinct.class);
//	     	extensionPoints.add("ColumnOperators",Division.class.toString(),Division.class);
//	     	extensionPoints.add("ColumnOperators",E.class.toString(),E.class);
//	     	extensionPoints.add("ColumnOperators",EndsWith.class.toString(),EndsWith.class);
//	     	extensionPoints.add("ColumnOperators",Equal.class.toString(),Equal.class);
//	     	extensionPoints.add("ColumnOperators",Equals.class.toString(),Equals.class);
//	     	extensionPoints.add("ColumnOperators",Exp.class.toString(),Exp.class);
//	     	extensionPoints.add("ColumnOperators",Geometry.class.toString(),Geometry.class);
//	     	extensionPoints.add("ColumnOperators",GetTimeDate.class.toString(),GetTimeDate.class);
//	     	extensionPoints.add("ColumnOperators",IndexOf.class.toString(),IndexOf.class);
//	     	extensionPoints.add("ColumnOperators",IsNumber.class.toString(),IsNumber.class);
//	     	extensionPoints.add("ColumnOperators",LastIndexOf.class.toString(),LastIndexOf.class);
//	     	extensionPoints.add("ColumnOperators",Length.class.toString(),Length.class);
//	     	extensionPoints.add("ColumnOperators",LessEquals.class.toString(),LessEquals.class);
//	     	extensionPoints.add("ColumnOperators",LessThan.class.toString(),LessThan.class);
//	     	extensionPoints.add("ColumnOperators",Log.class.toString(),Log.class);
//	     	extensionPoints.add("ColumnOperators",Max.class.toString(),Max.class);
//	     	extensionPoints.add("ColumnOperators",Min.class.toString(),Min.class);
//	     	extensionPoints.add("ColumnOperators",Minus.class.toString(),Minus.class);
//	     	extensionPoints.add("ColumnOperators",MoreEquals.class.toString(),MoreEquals.class);
//	     	extensionPoints.add("ColumnOperators",MoreThan.class.toString(),MoreThan.class);
//	     	extensionPoints.add("ColumnOperators",Perimeter.class.toString(),Perimeter.class);
//	     	extensionPoints.add("ColumnOperators",Pi.class.toString(),Pi.class);
//	    	extensionPoints.add("ColumnOperators",Plus.class.toString(),Plus.class);
//	     	extensionPoints.add("ColumnOperators",PointX.class.toString(),PointX.class);
//	     	extensionPoints.add("ColumnOperators",PointY.class.toString(),PointY.class);
//	     	extensionPoints.add("ColumnOperators",Pow.class.toString(),Pow.class);
//	     	extensionPoints.add("ColumnOperators",Random.class.toString(),Random.class);
//	     	extensionPoints.add("ColumnOperators",Replace.class.toString(),Replace.class);
//	     	extensionPoints.add("ColumnOperators",Round.class.toString(),Round.class);
//	     	extensionPoints.add("ColumnOperators",SetTimeDate.class.toString(),SetTimeDate.class);
//	     	extensionPoints.add("ColumnOperators",Sin.class.toString(),Sin.class);
//	     	extensionPoints.add("ColumnOperators",Sqrt.class.toString(),Sqrt.class);
//	     	extensionPoints.add("ColumnOperators",StartsWith.class.toString(),StartsWith.class);
//	     	extensionPoints.add("ColumnOperators",SubString.class.toString(),SubString.class);
//	     	extensionPoints.add("ColumnOperators",Tan.class.toString(),Tan.class);
//	    	extensionPoints.add("ColumnOperators",Times.class.toString(),Times.class);
//	    	extensionPoints.add("ColumnOperators",ToDate.class.toString(),ToDate.class);
//	    	extensionPoints.add("ColumnOperators",ToDegrees.class.toString(),ToDegrees.class);
//	    	extensionPoints.add("ColumnOperators",ToLowerCase.class.toString(),ToLowerCase.class);
//	    	extensionPoints.add("ColumnOperators",ToNumber.class.toString(),ToNumber.class);
//	    	extensionPoints.add("ColumnOperators",ToRadians.class.toString(),ToRadians.class);
//	    	extensionPoints.add("ColumnOperators",ToString.class.toString(),ToString.class);
//	    	extensionPoints.add("ColumnOperators",ToUpperCase.class.toString(),ToUpperCase.class);
//	    	extensionPoints.add("ColumnOperators",Trim.class.toString(),Trim.class);
//	 }
//
//	 private void registerIcons(){
//		 PluginServices.getIconTheme().registerDefault(
//					"ext-kcalc",
//					this.getClass().getClassLoader().getResource("images/kcalc.png")
//				);
//
//		 PluginServices.getIconTheme().registerDefault(
//					"field-expression-kcalc",
//					this.getClass().getClassLoader().getResource("images/FieldExpression.png")
//				);
//	 }
//
	
}
