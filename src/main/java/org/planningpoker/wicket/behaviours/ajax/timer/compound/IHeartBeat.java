package org.planningpoker.wicket.behaviours.ajax.timer.compound;

import java.io.Serializable;

/**
 * Heart beat callback interface. Use this together with
 * {@link AjaxCompoundUpdatingTimerBehavior} to be notified if the client
 * browser is still alive.
 */
public interface IHeartBeat extends Serializable {

	/**
	 * Invoked everytime the timer on {@link AjaxCompoundUpdatingTimerBehavior}
	 * is called.
	 */
	void beat();
}