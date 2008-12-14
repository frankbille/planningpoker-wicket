/**
 * 
 */
package org.planningpoker.wicket.behaviours.ajax.timer.compound;

import java.io.Serializable;

import org.apache.wicket.Component;

/**
 * Used for component updating listeners.
 * 
 * @param <T>
 *            The component
 */
public interface IUpdatingComponent<T extends Component> extends Serializable {

	/**
	 * Get the state object to use for a component. This can normally be the
	 * model object of the component, but can also be something else.
	 * 
	 * @param component
	 *            The component to get the state object for.
	 * @return The state object.
	 */
	Object getStateObject(T component);
}