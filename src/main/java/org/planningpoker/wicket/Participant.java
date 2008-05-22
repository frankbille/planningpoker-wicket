package org.planningpoker.wicket;

import java.util.Date;

import org.apache.wicket.Session;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.IHeartBeat;

/**
 * Participant in a specific planning session. A participant has a heartbeat
 * which beats based on the health of it's client browser.
 */
public class Participant implements IHeartBeat {
	private static final long serialVersionUID = 1L;

	/**
	 * Health of the participant. This is measured from when the last heartbeat
	 * was registered. If it's too long ago, then Health degrades.
	 */
	public static enum Health {
		/**
		 * Good health
		 */
		GOOD,
		/**
		 * Ill
		 */
		ILL,
		/**
		 * Sick
		 */
		SICK,
		/**
		 * Almost out of contact
		 */
		DYING,
		/**
		 * Out of reach
		 */
		DEAD
	}

	private final String name;
	private final Session session;
	private transient Date lastPing;

	/**
	 * Create a new participant. A participant must be associated with the
	 * Wicket {@link Session}, that the user has.
	 * 
	 * @param name
	 *            The name of the participant
	 * @param session
	 *            The Wicket {@link Session} associated with the user that will
	 *            be represented by the participant.
	 */
	Participant(String name, Session session) {
		this.name = name;
		this.session = session;
		beat();
	}

	/**
	 * Get the name of the participant.
	 * 
	 * @return The name of the participant.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the Wicket {@link Session}.
	 * 
	 * @return The Wicket {@link Session}.
	 */
	public Session getSession() {
		return session;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;

		if (obj != null) {
			if (obj instanceof Participant) {
				Participant participant = (Participant) obj;

				// We only compare sessions
				if (session == participant.session) {
					equals = true;
				}
			}
		}

		return equals;
	}

	/**
	 * Get the health of the participant. This is measured from when the last
	 * heart beat was registered. If it's too long ago, then Health degrades.
	 * 
	 * @return The health of the participant.
	 */
	public Health getHealth() {
		long millisSinceLastPing = getMillisSinceLastPing();

		if (millisSinceLastPing <= 2000) {
			return Health.GOOD;
		} else if (millisSinceLastPing <= 6000) {
			return Health.ILL;
		} else if (millisSinceLastPing <= 10000) {
			return Health.SICK;
		} else if (millisSinceLastPing <= 20000) {
			return Health.DYING;
		} else {
			return Health.DEAD;
		}
	}

	private long getMillisSinceLastPing() {
		if (lastPing == null) {
			beat();
		}

		return new Date().getTime() - lastPing.getTime();
	}

	public void beat() {
		this.lastPing = new Date();
	}
}