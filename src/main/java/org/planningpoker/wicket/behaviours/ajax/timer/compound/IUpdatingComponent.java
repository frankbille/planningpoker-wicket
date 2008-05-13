/**
 * 
 */
package org.planningpoker.wicket.behaviours.ajax.timer.compound;

import java.io.Serializable;

import org.apache.wicket.Component;

public interface IUpdatingComponent extends Serializable {
	Object getStateObject(Component<?> component);
}