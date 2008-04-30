package org.planningpoker.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Session;
import org.planningpoker.domain.IDeck;
import org.planningpoker.domain.StandardDeck;

public class PlanningSession implements Serializable {
	private static final long serialVersionUID = 1L;

	public static enum Status {
		/**
		 * 
		 */
		WAITING_FOR_SESSION_TO_START,
		/**
		 * 
		 */
		DECIDING,
		/**
		 * 
		 */
		CARD_CHOSEN
	}

	private final List<Participant> participants = new ArrayList<Participant>();
	private final Participant owner;
	private PlanningRound currentRound;
	private final String title;
	private final String password;
	private final IDeck deck = new StandardDeck();

	PlanningSession(String title, String password, String ownerName) {
		this(title, password, ownerName, Session.get());
	}

	PlanningSession(String title, String password, String ownerName,
			Session ownerSession) {
		this.title = title;
		this.password = password;
		owner = addParticipant(ownerName, ownerSession);
	}

	public IDeck getDeck() {
		return deck;
	}

	public Participant addParticipant(String name) {
		return addParticipant(name, Session.get());
	}

	public Participant addParticipant(String name, Session session) {
		if (isStarted()) {
			throw new IllegalStateException(
					"Can't add participants when the session is started.");
		}

		synchronized (participants) {
			Participant participant = new Participant(name, session);
			participants.add(participant);
			return participant;
		}
	}

	public List<Participant> getParticipants() {
		return Collections.unmodifiableList(participants);
	}

	public int getParticipantCount() {
		return participants.size();
	}

	public boolean isStarted() {
		return currentRound != null;
	}

	public String getTitle() {
		return title;
	}

	public boolean isPasswordProtected() {
		return password != null;
	}

	public boolean verifyPassword(String password) {
		boolean valid = false;

		if (isPasswordProtected() == false) {
			valid = true;
		} else {
			valid = this.password.equals(password);
		}

		return valid;
	}

	public boolean isParticipating() {
		return isParticipating(Session.get());
	}

	public boolean isParticipating(Session session) {
		return getParticipant(session) != null;
	}

	public Participant getParticipant() {
		return getParticipant(Session.get());
	}

	public Participant getParticipant(Session session) {
		synchronized (participants) {
			Participant participant = null;

			for (Participant p : participants) {
				if (session == p.getSession()) {
					participant = p;
					break;
				}
			}

			return participant;
		}
	}

	public boolean isOwner() {
		return isOwner(Session.get());
	}

	public boolean isOwner(Session session) {
		return isOwner(getParticipant(session));
	}

	public boolean isOwner(Participant participant) {
		return owner.equals(participant);
	}

	public PlanningRound getCurrentPlanningRound() {
		return currentRound;
	}

	public synchronized PlanningRound createNewRound() {
		if (currentRound == null) {
			createRound();
		} else {
			if (currentRound.isComplete()) {
				createRound();
			}
		}

		return currentRound;
	}

	public Status getParticipantStatus(Participant participant) {
		Status status = null;

		if (isStarted() == false) {
			status = Status.WAITING_FOR_SESSION_TO_START;
		} else {
			if (currentRound.hasChosedCard(participant)) {
				status = Status.CARD_CHOSEN;
			} else {
				status = Status.DECIDING;
			}
		}

		return status;
	}

	private void createRound() {
		currentRound = new PlanningRound(this);
	}
}
