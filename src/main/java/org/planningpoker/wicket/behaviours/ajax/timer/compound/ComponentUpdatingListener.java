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
public class ComponentUpdatingListener<T extends Component<?>> implements IUpdatingListener {
	private static final long serialVersionUID = 1L;

	private static class UpdatingComponentState<T extends Component<?>> implements Serializable {
		private static final long serialVersionUID = 1L;

		private final IObjectState objectState;
		private final IUpdatingComponent<T> updatingComponent;

		public UpdatingComponentState(IUpdatingComponent<T> updatingComponent) {
			this(updatingComponent, new SerializableObjectState());
		}

		public UpdatingComponentState(IUpdatingComponent<T> updatingComponent,
				IObjectState objectState) {
			this.updatingComponent = updatingComponent;
			this.objectState = objectState;
		}

		public boolean updateState(T component) {
			Object stateObject = updatingComponent.getStateObject(component);

			return objectState.checkState(stateObject);
		}
	}

	private final T component;
	private final UpdatingComponentState<T> updatingComponentState;

	public ComponentUpdatingListener(T component) {
		this(component, new IUpdatingComponent<T>() {
			private static final long serialVersionUID = 1L;

			public boolean isEnabled(T component) {
				return true;
			}

			public Object getStateObject(T component) {
				return isEnabled(component) && component.isEnabled() ? component.getModelObject()
						: null;
			}
		});
	}

	public ComponentUpdatingListener(T component, IUpdatingComponent<T> updatingComponent) {
		this.component = component;
		this.updatingComponentState = new UpdatingComponentState<T>(updatingComponent);

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