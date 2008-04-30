package org.planningpoker.wicket.behaviours;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.time.Duration;

/**
 * Compound timed updater, which a lot of components can hook up to and only
 * causes one timed ajax request for all of them. This is very effective if you
 * have a lot of components which should have timed ajax updates at the same
 * interval, because there will not be a AJAX request for all off them, reducing
 * load on server and client as well as lowering the bandwidth.
 * <p>
 * TODO: Refactor all the inner implementations out of this.
 */
public class AjaxCompoundUpdatingTimerBehavior extends
		AbstractAjaxTimerBehavior {
	private static final long serialVersionUID = 1L;

	public static interface IUpdatingListener extends Serializable {
		boolean isEnabled();

		void onHeadRendered(IHeaderResponse response);

		void onUpdated(AjaxRequestTarget target);
	}

	public static interface IHeartBeat extends Serializable {
		void ping();
	}

	private static class HeartBeatUpdatingListener implements IUpdatingListener {
		private static final long serialVersionUID = 1L;

		private final IHeartBeat heartBeat;

		public HeartBeatUpdatingListener(IHeartBeat heartBeat) {
			this.heartBeat = heartBeat;
		}

		public boolean isEnabled() {
			return true;
		}

		public void onHeadRendered(IHeaderResponse response) {
		}

		public void onUpdated(AjaxRequestTarget target) {
			heartBeat.ping();
		}

	}

	public static class ComponentUpdatingListener implements IUpdatingListener {
		private static final long serialVersionUID = 1L;

		private final Component<?> component;
		private final UpdatingComponentState updatingComponentState;

		public ComponentUpdatingListener(Component<?> component,
				IUpdatingComponent updatingComponent) {
			this.component = component;
			this.updatingComponentState = new UpdatingComponentState(
					updatingComponent);

			component.setOutputMarkupId(true);
		}

		public boolean isEnabled() {
			return updatingComponentState.isEnabled(component);
		}

		public void onUpdated(AjaxRequestTarget target) {
			if (updatingComponentState.updateState(component)) {
				target.addComponent(component);
			}
		}

		public void onHeadRendered(IHeaderResponse response) {
			updatingComponentState.updateState(component);
		}

	}

	public static interface IUpdatingComponent extends Serializable {
		boolean isEnabled(Component<?> component);

		Object getStateObject(Component<?> component);
	}

	private static class UpdatingComponentState implements Serializable {
		private static final long serialVersionUID = 1L;

		private ObjectState objectState;
		private final IUpdatingComponent updatingComponent;

		public UpdatingComponentState(IUpdatingComponent updatingComponent) {
			this.updatingComponent = updatingComponent;
		}

		public boolean updateState(Component<?> component) {
			Object stateObject = updatingComponent.getStateObject(component);

			if (objectState == null) {
				objectState = new ObjectState(stateObject);
			}

			return objectState.newState(stateObject);
		}

		public boolean isEnabled(Component<?> component) {
			return updatingComponent.isEnabled(component);
		}
	}

	private final List<IUpdatingListener> updatingListeners = new ArrayList<IUpdatingListener>();

	public AjaxCompoundUpdatingTimerBehavior(Duration updateInterval) {
		super(updateInterval);
	}

	@Override
	protected void onTimer(AjaxRequestTarget target) {
		for (IUpdatingListener updatingListener : updatingListeners) {
			updatingListener.onUpdated(target);
		}

		onPostTimerUpdated(target);
	}

	@Override
	protected void onHeadRendered(IHeaderResponse response) {
		for (IUpdatingListener updatingListener : updatingListeners) {
			updatingListener.onHeadRendered(response);
		}
	}

	protected void onPostTimerUpdated(AjaxRequestTarget target) {
	}

	@Override
	public boolean isEnabled(Component<?> component) {
		boolean enabled = false;

		for (IUpdatingListener updatingListener : updatingListeners) {
			if (updatingListener.isEnabled()) {
				enabled = true;
				break;
			}
		}

		return enabled;
	}

	public void add(IUpdatingListener updatingListener) {
		updatingListeners.add(updatingListener);
	}

	public void add(IHeartBeat heartBeat) {
		updatingListeners.add(new HeartBeatUpdatingListener(heartBeat));
	}

	public void add(Component<?> component) {
		add(component, new IUpdatingComponent() {
			private static final long serialVersionUID = 1L;

			public boolean isEnabled(Component<?> component) {
				return true;
			}

			public Object getStateObject(Component<?> component) {
				return isEnabled(component) && component.isEnabled() ? component
						.getModelObject()
						: null;
			}
		});
	}

	public void add(Component<?> component, IUpdatingComponent updatingComponent) {
		add(new ComponentUpdatingListener(component, updatingComponent));
	}

}
