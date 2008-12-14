package org.planningpoker.wicket.behaviours.ajax.timer;

import java.io.Serializable;

import org.apache.wicket.util.lang.Objects;

/**
 * Implementation of the {@link IObjectState}, which requires the objects to be
 * serializable. It then serializes it and compares the strings to find out if
 * an object has changed.
 */
public final class SerializableObjectState implements IObjectState {
	private static final long serialVersionUID = 1L;

	private transient String state;

	/**
	 * Constructor.
	 */
	public SerializableObjectState() {
		checkState(null);
	}

	public boolean checkState(Object object) {
		if (object != null && object instanceof Serializable == false) {
			throw new IllegalArgumentException("The object must be Serializable: " + object);
		}

		return checkState((Serializable) object);
	}

	/**
	 * @return True if the state of the object has changed
	 */
	private boolean checkState(Serializable object) {
		boolean changed = false;

		String newState = null;
		if (object != null) {
			newState = createStateObject(object);
		} else {
			newState = "";
		}

		if (state == null) {
			changed = true;
		} else if (state.equals(newState) == false) {
			changed = true;
		}

		state = newState;

		return changed;
	}

	private String createStateObject(Serializable object) {
		return new String(Objects.objectToByteArray(object));
	}
}
