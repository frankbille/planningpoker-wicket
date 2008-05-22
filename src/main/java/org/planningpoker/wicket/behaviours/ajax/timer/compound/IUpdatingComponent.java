/**
 * 
 */
package org.planningpoker.wicket.behaviours.ajax.timer.compound;

import java.io.Serializable;

import org.apache.wicket.Component;

public interface IUpdatingComponent<T extends Component<?>> extends
		Serializable {
	Object getStateObject(T component);
}