package org.planningpoker.wicket.behaviours.ajax.timer.compound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.time.Duration;
import org.planningpoker.wicket.behaviours.ajax.timer.AbstractAjaxTimerBehavior;

/**
 * Compound timed updater, which a lot of components can hook up to and only
 * causes one timed ajax request for all of them. This is very effective if you
 * have a lot of components which should have timed ajax updates at the same
 * interval, because there will not be a AJAX request for all off them, reducing
 * load on server and client as well as lowering the bandwidth.
 */
public class AjaxCompoundUpdatingTimerBehavior extends
		AbstractAjaxTimerBehavior {
	private static final long serialVersionUID = 1L;

	/**
	 * Updating listener, which is used to add a listener to the updating, which
	 * this behavior is doing.
	 */
	public static interface IUpdatingListener extends Serializable {
		/**
		 * Allow the listener to contribute to the headers.
		 * 
		 * @param response
		 *            The header response which can be used to contribute to
		 *            headers with.
		 */
		void onHeadRendered(IHeaderResponse response);

		/**
		 * Called everytime the timer does a callback.
		 * 
		 * @param target
		 *            The {@link AjaxRequestTarget} which can be used to
		 *            contribute to the UI.
		 */
		void onUpdated(AjaxRequestTarget target);
	}

	private final List<IUpdatingListener> updatingListeners = new ArrayList<IUpdatingListener>();

	/**
	 * Contructor.
	 * 
	 * @param updateInterval
	 *            Duration between AJAX callbacks.
	 */
	public AjaxCompoundUpdatingTimerBehavior(Duration updateInterval) {
		super(updateInterval);
	}

	@Override
	protected void onTimer(AjaxRequestTarget target) {
		for (IUpdatingListener updatingListener : updatingListeners) {
			updatingListener.onUpdated(target);
		}
	}

	@Override
	protected void onHeadRendered(IHeaderResponse response) {
		for (IUpdatingListener updatingListener : updatingListeners) {
			updatingListener.onHeadRendered(response);
		}
	}

	/**
	 * Add an updating listener to this timed ajax behavior.
	 * 
	 * @param updatingListener
	 *            The updating listener to add.
	 */
	public void add(IUpdatingListener updatingListener) {
		updatingListeners.add(updatingListener);
	}

}
