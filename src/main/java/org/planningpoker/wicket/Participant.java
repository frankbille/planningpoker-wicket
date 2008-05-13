package org.planningpoker.wicket;

import java.util.Date;

import org.apache.wicket.Session;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.IHeartBeat;

public class Participant implements IHeartBeat {
	private static final long serialVersionUID = 1L;

	/**
	 * Health of the participant. This is measured from when the last heartbeat
	 * was registered. If it's too long ago, then Health degrades.
	 */
	public static enum Health {
		GOOD, ILL, SICK, DYING, DEAD
	}

	private final String name;
	private final Session session;
	private transient Date lastPing;

	public Participant(String name, Session session) {
		this.name = name;
		this.session = session;
		beat();
	}

	public String getName() {
		return name;
	}

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
		return new Date().getTime() - lastPing.getTime();
	}

	public void beat() {
		this.lastPing = new Date();
	}
}