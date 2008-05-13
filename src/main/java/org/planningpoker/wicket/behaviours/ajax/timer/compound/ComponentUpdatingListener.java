package org.planningpoker.wicket.behaviours.ajax.timer.compound;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.planningpoker.wicket.behaviours.ajax.timer.IObjectState;
import org.planningpoker.wicket.behaviours.ajax.timer.SerializableObjectState;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.AjaxCompoundUpdatingTimerBehavior.IUpdatingListener;

/**
 * A component updating listener, which updates a component everytime the timer
 * makes a callback. The component is only sent to the client though, if the
 * state of the object has changed. This is determined by the
 * {@link IObjectState} implementation used. Per default the
 * {@link SerializableObjectState} will be used, and it will check the model
 * object of the component.
 * <p>
 * TODO: Check if this can't be cleaned up a bit.
 */
public class ComponentUpdatingListener implements IUpdatingListener {
	private static final long serialVersionUID = 1L;

	private static class UpdatingComponentState implements Serializable {
		private static final long serialVersionUID = 1L;

		private final IObjectState objectState;
		private final IUpdatingComponent updatingComponent;

		public UpdatingComponentState(IUpdatingComponent updatingComponent) {
			this(updatingComponent, new SerializableObjectState());
		}

		public UpdatingComponentState(IUpdatingComponent updatingComponent,
				IObjectState objectState) {
			this.updatingComponent = updatingComponent;
			this.objectState = objectState;
		}

		public boolean updateState(Component<?> component) {
			Object stateObject = updatingComponent.getStateObject(component);

			return objectState.checkState(stateObject);
		}
	}

	private final Component<?> component;
	private final UpdatingComponentState updatingComponentState;

	public ComponentUpdatingListener(Component<?> component) {
		this(component, new IUpdatingComponent() {
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

	public ComponentUpdatingListener(Component<?> component,
			IUpdatingComponent updatingComponent) {
		this.component = component;
		this.updatingComponentState = new UpdatingComponentState(
				updatingComponent);

		component.setOutputMarkupId(true);
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