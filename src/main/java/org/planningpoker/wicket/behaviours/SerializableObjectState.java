package org.planningpoker.wicket.behaviours;

import java.io.Serializable;

import org.apache.wicket.util.lang.Objects;

public final class SerializableObjectState implements IObjectState {
	private static final long serialVersionUID = 1L;

	private transient String state;

	public SerializableObjectState() {
		checkState(null);
	}

	public boolean checkState(Object object) {
		if (object != null && object instanceof Serializable == false) {
			throw new IllegalArgumentException(
					"The object must be Serializable: " + object);
		}

		return checkState((Serializable) object);
	}

	/**
	 * @return True if the state of the object has changed
	 */
	public boolean checkState(Serializable object) {
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

	public void reset() {
		state = null;
	}

	private String createStateObject(Serializable object) {
		return new String(Objects.objectToByteArray(object));
	}
}
