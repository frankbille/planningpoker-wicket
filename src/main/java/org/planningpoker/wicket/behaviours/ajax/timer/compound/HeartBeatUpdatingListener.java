package org.planningpoker.wicket.behaviours.ajax.timer.compound;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.AjaxCompoundUpdatingTimerBehavior.IUpdatingListener;

/**
 * Heart beat listener.
 * 
 * @param <T>
 *            The heart beat type
 */
public class HeartBeatUpdatingListener<T extends IHeartBeat> implements
		IUpdatingListener {
	private static final long serialVersionUID = 1L;

	private final T heartBeat;

	/**
	 * Use this listener to get the heart beat of a client browser.
	 * 
	 * @param heartBeat
	 *            The heart beat implementation that is called.
	 */
	public HeartBeatUpdatingListener(T heartBeat) {
		this.heartBeat = heartBeat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.planningpoker.wicket.behaviours.ajax.timer.compound.AjaxCompoundUpdatingTimerBehavior.IUpdatingListener#onHeadRendered(org.apache.wicket.markup.html.IHeaderResponse)
	 */
	public void onHeadRendered(IHeaderResponse response) {
		// No need for header contributions
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.planningpoker.wicket.behaviours.ajax.timer.compound.AjaxCompoundUpdatingTimerBehavior.IUpdatingListener#onUpdated(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	public void onUpdated(AjaxRequestTarget target) {
		heartBeat.beat();

		onBeat(target, heartBeat);
	}

	protected void onBeat(AjaxRequestTarget target, T heartBeat) {
	}

}