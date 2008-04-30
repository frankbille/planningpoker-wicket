package org.planningpoker.wicket;

import java.util.Date;

import org.apache.wicket.Session;
import org.planningpoker.wicket.behaviours.AjaxCompoundUpdatingTimerBehavior.IHeartBeat;

public class Participant implements IHeartBeat {
	private static final long serialVersionUID = 1L;

	private final String name;
	private final Session session;
	private Date lastPing;

	public Participant(String name, Session session) {
		this.name = name;
		this.session = session;
		ping();
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

	public long getMillisSinceLastPing() {
		return new Date().getTime() - lastPing.getTime();
	}

	public void ping() {
		this.lastPing = new Date();
	}
}