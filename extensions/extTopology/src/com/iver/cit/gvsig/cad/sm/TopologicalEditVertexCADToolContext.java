/**
 Finite state machine, generated with fsm tool
 (http://smc.sourceforge.net)
 @author Alvaro Zabala
 */

package com.iver.cit.gvsig.cad.sm;

import java.awt.event.InputEvent;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.cad.TopologicalEditVertexCADTool;

public final class TopologicalEditVertexCADToolContext extends
		statemap.FSMContext {
	// ---------------------------------------------------------------
	// Member methods.
	//

	public TopologicalEditVertexCADToolContext(
			TopologicalEditVertexCADTool owner) {
		super();

		_owner = owner;
		setState(TopologicalEdition.FirstPoint);
		TopologicalEdition.FirstPoint.Entry(this);
	}

	public void addOption(String s) {
		_transition = "addOption";
		getState().addOption(this, s);
		_transition = "";
		return;
	}

	public void addPoint(double pointX, double pointY, InputEvent event) {
		_transition = "addPoint";
		getState().addPoint(this, pointX, pointY, event);
		_transition = "";
		return;
	}

	public void addValue(double d) {
		_transition = "addValue";
		getState().addValue(this, d);
		_transition = "";
		return;
	}

	public TopologicalEditVertexCADToolState getState()
			throws statemap.StateUndefinedException {
		if (_state == null) {
			throw (new statemap.StateUndefinedException());
		}

		return ((TopologicalEditVertexCADToolState) _state);
	}

	protected TopologicalEditVertexCADTool getOwner() {
		return (_owner);
	}

	// ---------------------------------------------------------------
	// Member data.
	//

	transient private TopologicalEditVertexCADTool _owner;

	// ---------------------------------------------------------------
	// Inner classes.
	//

	public static abstract class TopologicalEditVertexCADToolState extends
			statemap.State {
		// -----------------------------------------------------------
		// Member methods.
		//

		protected TopologicalEditVertexCADToolState(String name, int id) {
			super(name, id);
		}

		protected void Entry(TopologicalEditVertexCADToolContext context) {
		}

		protected void Exit(TopologicalEditVertexCADToolContext context) {
		}

		protected void addOption(TopologicalEditVertexCADToolContext context,
				String s) {
			Default(context);
		}

		protected void addPoint(TopologicalEditVertexCADToolContext context,
				double pointX, double pointY, InputEvent event) {
			Default(context);
		}

		protected void addValue(TopologicalEditVertexCADToolContext context,
				double d) {
			Default(context);
		}

		protected void Default(TopologicalEditVertexCADToolContext context) {
			throw (new statemap.TransitionUndefinedException("State: "
					+ context.getState().getName() + ", Transition: "
					+ context.getTransition()));
		}

		// -----------------------------------------------------------
		// Member data.
		//
	}

	/* package */static abstract class TopologicalEdition {
		// -----------------------------------------------------------
		// Member methods.
		//

		// -----------------------------------------------------------
		// Member data.
		//

		// -------------------------------------------------------
		// Statics.
		//
		/* package */static TopologicalEdition_Default.TopologicalEdition_FirstPoint FirstPoint;
		/* package */static TopologicalEdition_Default.TopologicalEdition_WithSelectedFeatures WithSelectedFeatures;
		/* package */static TopologicalEdition_Default.TopologicalEdition_SecondPoint SecondPoint;
		/* package */static TopologicalEdition_Default.TopologicalEdition_WithHandlers WithHandlers;
		private static TopologicalEdition_Default Default;

		static {
			FirstPoint = new TopologicalEdition_Default.TopologicalEdition_FirstPoint(
					"TopologicalEdition.FirstPoint", 2);
			WithSelectedFeatures = new TopologicalEdition_Default.TopologicalEdition_WithSelectedFeatures(
					"TopologicalEdition.WithSelectedFeatures", 3);
			SecondPoint = new TopologicalEdition_Default.TopologicalEdition_SecondPoint(
					"TopologicalEdition.SecondPoint", 4);
			WithHandlers = new TopologicalEdition_Default.TopologicalEdition_WithHandlers(
					"TopologicalEdition.WithHandlers", 5);
			Default = new TopologicalEdition_Default(
					"TopologicalEdition.Default", -1);
		}

	}

	protected static class TopologicalEdition_Default extends
			TopologicalEditVertexCADToolState {
		// -----------------------------------------------------------
		// Member methods.
		//

		protected TopologicalEdition_Default(String name, int id) {
			super(name, id);
		}

		protected void addOption(TopologicalEditVertexCADToolContext context,
				String s) {
			TopologicalEditVertexCADTool ctxt = context.getOwner();

			if (s.equals("")) {
				boolean loopbackFlag = context.getState().getName()
						.equals(TopologicalEdition.FirstPoint.getName());

				if (loopbackFlag == false) {
					(context.getState()).Exit(context);
				}

				context.clearState();
				try {
					ctxt.restorePreviousTool();
					ctxt.setQuestion(PluginServices.getText(this,
							"insert_point_selection"));
					ctxt.setDescription(new String[] { "cancel" });
					ctxt.end();
				} finally {
					context.setState(TopologicalEdition.FirstPoint);

					if (loopbackFlag == false) {
						(context.getState()).Entry(context);
					}

				}
			} else if (s.equals(PluginServices.getText(this, "cancel"))) {
				boolean loopbackFlag = context.getState().getName()
						.equals(TopologicalEdition.FirstPoint.getName());

				if (loopbackFlag == false) {
					(context.getState()).Exit(context);
				}

				context.clearState();
				try {
					ctxt.end();
				} finally {
					context.setState(TopologicalEdition.FirstPoint);

					if (loopbackFlag == false) {
						(context.getState()).Entry(context);
					}

				}
			} else {
				boolean loopbackFlag = context.getState().getName()
						.equals(TopologicalEdition.FirstPoint.getName());

				if (loopbackFlag == false) {
					(context.getState()).Exit(context);
				}

				context.clearState();
				try {
					ctxt.throwOptionException(
							PluginServices.getText(this, "incorrect_option"), s);
				} finally {
					context.setState(TopologicalEdition.FirstPoint);

					if (loopbackFlag == false) {
						(context.getState()).Entry(context);
					}

				}
			}

			return;
		}

		protected void addValue(TopologicalEditVertexCADToolContext context,
				double d) {
			TopologicalEditVertexCADTool ctxt = context.getOwner();

			boolean loopbackFlag = context.getState().getName()
					.equals(TopologicalEdition.FirstPoint.getName());

			if (loopbackFlag == false) {
				(context.getState()).Exit(context);
			}

			context.clearState();
			try {
				ctxt.throwValueException(
						PluginServices.getText(this, "incorrect_value"), d);
			} finally {
				context.setState(TopologicalEdition.FirstPoint);

				if (loopbackFlag == false) {
					(context.getState()).Entry(context);
				}

			}
			return;
		}

		protected void addPoint(TopologicalEditVertexCADToolContext context,
				double pointX, double pointY, InputEvent event) {
			TopologicalEditVertexCADTool ctxt = context.getOwner();

			boolean loopbackFlag = context.getState().getName()
					.equals(TopologicalEdition.FirstPoint.getName());

			if (loopbackFlag == false) {
				(context.getState()).Exit(context);
			}

			context.clearState();
			try {
				ctxt.throwPointException(
						PluginServices.getText(this, "incorrect_point"),
						pointX, pointY);
			} finally {
				context.setState(TopologicalEdition.FirstPoint);

				if (loopbackFlag == false) {
					(context.getState()).Entry(context);
				}

			}
			return;
		}

		// -----------------------------------------------------------
		// Inner classse.
		//

		private static final class TopologicalEdition_FirstPoint extends
				TopologicalEdition_Default {
			// -------------------------------------------------------
			// Member methods.
			//

			private TopologicalEdition_FirstPoint(String name, int id) {
				super(name, id);
			}

			protected void Entry(TopologicalEditVertexCADToolContext context) {
				TopologicalEditVertexCADTool ctxt = context.getOwner();

				ctxt.setQuestion(PluginServices.getText(this,
						"insert_point_selection"));
				ctxt.setDescription(new String[] { "cancel" });
				return;
			}

			protected void addPoint(
					TopologicalEditVertexCADToolContext context, double pointX,
					double pointY, InputEvent event) {
				TopologicalEditVertexCADTool ctxt = context.getOwner();

				if (ctxt.getType().equals(
						PluginServices.getText(this, "simple"))
						&& ctxt.selectFeatures(pointX, pointY, event)
						&& ctxt.getNextState().equals(
								"TopologicalEdition.SecondPoint")) {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"insert_second_point"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
					} finally {
						context.setState(TopologicalEdition.SecondPoint);
						(context.getState()).Entry(context);
					}
				} else if (ctxt.getType().equals(
						PluginServices.getText(this, "simple"))
						&& ctxt.getNextState().equals(
								"TopologicalEdition.WithSelectedFeatures")) {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"select_handlers"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
						ctxt.end();
					} finally {
						context.setState(TopologicalEdition.WithSelectedFeatures);
						(context.getState()).Entry(context);
					}
				} else if (ctxt.getType().equals(
						PluginServices.getText(this, "simple"))
						&& ctxt.getNextState().equals(
								"TopologicalEdition.WithHandlers")) {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"select_handlers"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
						ctxt.refresh();
					} finally {
						context.setState(TopologicalEdition.WithHandlers);
						(context.getState()).Entry(context);
					}
				} else {
					super.addPoint(context, pointX, pointY, event);
				}

				return;
			}

			// -------------------------------------------------------
			// Member data.
			//
		}

		private static final class TopologicalEdition_WithSelectedFeatures
				extends TopologicalEdition_Default {
			// -------------------------------------------------------
			// Member methods.
			//

			private TopologicalEdition_WithSelectedFeatures(String name, int id) {
				super(name, id);
			}

			protected void addOption(
					TopologicalEditVertexCADToolContext context, String s) {
				TopologicalEditVertexCADTool ctxt = context.getOwner();

				(context.getState()).Exit(context);
				context.clearState();
				try {
					ctxt.setQuestion(PluginServices.getText(this,
							"insert_point_selection"));
					ctxt.setDescription(new String[] { "cancel" });
					ctxt.setType(s);
				} finally {
					context.setState(TopologicalEdition.FirstPoint);
					(context.getState()).Entry(context);
				}
				return;
			}

			protected void addPoint(
					TopologicalEditVertexCADToolContext context, double pointX,
					double pointY, InputEvent event) {
				TopologicalEditVertexCADTool ctxt = context.getOwner();

				if (ctxt.selectHandlers(pointX, pointY, event) > 0) {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"insert_destination_point"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
						ctxt.refresh();
					} finally {
						context.setState(TopologicalEdition.WithHandlers);
						(context.getState()).Entry(context);
					}
				} else if (ctxt.selectFeatures(pointX, pointY, event)
						&& ctxt.getNextState().equals(
								"TopologicalEdition.WithSelectedFeatures")) {
					TopologicalEditVertexCADToolState endState = context
							.getState();

					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"select_handlers"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
					} finally {
						context.setState(endState);
					}
				} else {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"insert_point_selection"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
					} finally {
						context.setState(TopologicalEdition.FirstPoint);
						(context.getState()).Entry(context);
					}
				}

				return;
			}

			// -------------------------------------------------------
			// Member data.
			//
		}

		private static final class TopologicalEdition_SecondPoint extends
				TopologicalEdition_Default {
			// -------------------------------------------------------
			// Member methods.
			//

			private TopologicalEdition_SecondPoint(String name, int id) {
				super(name, id);
			}

			protected void addOption(
					TopologicalEditVertexCADToolContext context, String s) {
				TopologicalEditVertexCADTool ctxt = context.getOwner();

				(context.getState()).Exit(context);
				context.clearState();
				try {
					ctxt.setQuestion(PluginServices.getText(this,
							"insert_point_selection"));
					ctxt.setDescription(new String[] { "cancel" });
					ctxt.setType(s);
				} finally {
					context.setState(TopologicalEdition.FirstPoint);
					(context.getState()).Entry(context);
				}
				return;
			}

			protected void addPoint(
					TopologicalEditVertexCADToolContext context, double pointX,
					double pointY, InputEvent event) {
				TopologicalEditVertexCADTool ctxt = context.getOwner();

				if (ctxt.selectWithSecondPoint(pointX, pointY, event) > 0) {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"select_handlers"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
						ctxt.end();
					} finally {
						context.setState(TopologicalEdition.WithSelectedFeatures);
						(context.getState()).Entry(context);
					}
				} else {

					(context.getState()).Exit(context);
					context.clearState();
					try {
						ctxt.setQuestion(PluginServices.getText(this,
								"insert_point_selection"));
						ctxt.setDescription(new String[] { "cancel" });
						ctxt.addPoint(pointX, pointY, event);
					} finally {
						context.setState(TopologicalEdition.FirstPoint);
						(context.getState()).Entry(context);
					}
				}

				return;
			}

			// -------------------------------------------------------
			// Member data.
			//
		}

		private static final class TopologicalEdition_WithHandlers extends
				TopologicalEdition_Default {
			// -------------------------------------------------------
			// Member methods.
			//

			private TopologicalEdition_WithHandlers(String name, int id) {
				super(name, id);
			}

			protected void addPoint(
					TopologicalEditVertexCADToolContext context, double pointX,
					double pointY, InputEvent event) {
				TopologicalEditVertexCADTool ctxt = context.getOwner();

				(context.getState()).Exit(context);
				context.clearState();
				try {
					ctxt.setQuestion(PluginServices.getText(this,
							"select_handlers"));
					ctxt.setDescription(new String[] { "cancel" });
					ctxt.addPoint(pointX, pointY, event);
					ctxt.refresh();
				} finally {
					context.setState(TopologicalEdition.FirstPoint);
					(context.getState()).Entry(context);
				}
				return;
			}

			// -------------------------------------------------------
			// Member data.
			//
		}

		// -----------------------------------------------------------
		// Member data.
		//
	}
}
