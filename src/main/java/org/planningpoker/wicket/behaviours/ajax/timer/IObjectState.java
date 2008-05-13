package org.planningpoker.wicket.behaviours.ajax.timer;

import java.io.Serializable;

public interface IObjectState extends Serializable {

	/**
	 * Check if the stateObject has changed. The following must be true:
	 * 
	 * <ol>
	 * <li>IObjectState objectState = new IObjectState(){};
	 * <li>assertTrue(objectState.checkState("Hello World"));
	 * <li>assertFalse(objectState.checkState("Hello World"));
	 * <li>assertTrue(objectState.checkState(null));
	 * <li>assertFasle(objectState.checkState(null));
	 * <li>assertTrue(objectState.checkState("Hello World"));
	 * <li>assertTrue(objectState.checkState("Hello Again"));
	 * </ol>
	 * 
	 * @param stateObject
	 *            The object which should be checked if it has been changed.
	 * @return
	 */
	boolean checkState(Object stateObject);

}
