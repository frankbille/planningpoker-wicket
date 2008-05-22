package org.planningpoker.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.wicket.Session;
import org.planningpoker.domain.IDeck;
import org.planningpoker.domain.StandardDeck;

/**
 * A planning session is the full duration of a planning poker game. It includes
 * the participant, which cards to use, the current state of the game and who is
 * the owner of the session. In a session participants can currently have two
 * roles:
 * 
 * <ol>
 * <li><b>Dealer</b> - Manage the game, by creating new rounds, removing
 * participants and terminates the session.
 * <li><b>Player</b> - Can bid a card on each turn
 * </ol>
 * 
 * By default, the owner of the session is both <b>Dealer</b> and <b>Player</b>.
 * Every other participants are players.
 * 
 * <p>
 * PLEASE NOTE! This class should not be confused with the wicket
 * {@link Session}
 */
public class PlanningSession implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Status of a participant in connection with the session.
	 */
	public static enum ParticipantStatus {
		/**
		 * The "game" has not started yet, as more participants may join.
		 */
		WAITING_FOR_SESSION_TO_START,
		/**
		 * The player is deciding which card to choose.
		 */
		DECIDING,
		/**
		 * The player has chosen a card.
		 */
		CARD_CHOSEN,
		/**
		 * The session has been terminated.
		 */
		TERMINATED,
		/**
		 * No status could be determined for the participant. This might mean
		 * that the participant is no longer part of the session.
		 */
		UNKNOWN
	}

	/**
	 * The status of the planning session
	 */
	public static enum SessionStatus {
		/**
		 * The session hasn't started yet.
		 */
		SETTING_UP,
		/**
		 * The game is in progress.
		 */
		STARTED,
		/**
		 * The session has terminated.
		 */
		TERMINATED
	}

	private final List<Participant> participants = new CopyOnWriteArrayList<Participant>();
	private final Participant owner;
	private final Stack<PlanningRound> rounds = new Stack<PlanningRound>();
	private final String title;
	private final IDeck deck = new StandardDeck();
	private boolean terminated = false;

	/**
	 * @see #PlanningSession(String, String, Session)
	 * 
	 * @param title
	 * @param ownerName
	 */
	PlanningSession(String title, String ownerName) {
		this(title, ownerName, Session.get());
	}

	/**
	 * Create a new planning session.
	 * 
	 * Only the {@link PlanningPokerApplication} should create new instances
	 * 
	 * @param title
	 * @param ownerName
	 * @param ownerSession
	 */
	PlanningSession(String title, String ownerName, Session ownerSession) {
		this.title = title;
		owner = addParticipant(ownerName, ownerSession);
	}

	/**
	 * @return The {@link IDeck deck} which is being used in the session.
	 */
	public IDeck getDeck() {
		return deck;
	}

	/**
	 * Add a participant to this session. This can only be done while the
	 * session is in {@link SessionStatus#SETTING_UP} state.
	 * <p>
	 * This method is a convenience method for
	 * {@link #addParticipant(String, Session)} and is using the Wicket
	 * {@link Session} from {@link Session#get()}
	 * 
	 * @param name
	 *            The name of the participant
	 * @return The participant object which has been added to the session.
	 */
	public Participant addParticipant(String name) {
		return addParticipant(name, Session.get());
	}

	/**
	 * Add a participant to this session. This can only be done while the
	 * session is in {@link SessionStatus#SETTING_UP} state.
	 * <p>
	 * All participants must be associated with a session, so we can identify
	 * them if they go away from the page and come back again. It's also used as
	 * the unique identifier of a participant.
	 * 
	 * @param name
	 *            The name of the participant
	 * @param session
	 *            The Wicket {@link Session} which the participant is currently
	 *            attached to.
	 * @return The participant object which has been added to the session.
	 */
	public Participant addParticipant(String name, Session session) {
		if (getSessionStatus() != SessionStatus.SETTING_UP) {
			throw new IllegalStateException(
					"Can't add participants when the session is started.");
		}

		Participant participant = new Participant(name, session);
		participants.add(participant);
		return participant;
	}

	/**
	 * Remove a participant from the session. Only regular players can be
	 * removed, not the owner.
	 * 
	 * @param participant
	 *            The participant to remove
	 */
	public void remove(Participant participant) {
		if (owner.equals(participant)) {
			throw new IllegalArgumentException(
					"You cannot remove the owner of the session");
		}

		participants.remove(participant);
	}

	/**
	 * Get a list of all the participants in the session.
	 * 
	 * @return The participants on the form of a {@link Iterable}
	 */
	public Iterable<Participant> getParticipants() {
		return participants;
	}

	/**
	 * @return The number of participants in the session
	 */
	public int getParticipantCount() {
		return participants.size();
	}

	/**
	 * @return True if the "game" has started.
	 */
	public boolean isStarted() {
		return getSessionStatus() == SessionStatus.STARTED;
	}

	/**
	 * @return The title of the session.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Check if the participant with the current Wicket {@link Session} (taken
	 * from {@link Session#get()}) is participating in this planning session.
	 * 
	 * @see #isParticipating(Session)
	 * 
	 * @return True if the participant is participating.
	 */
	public boolean isParticipating() {
		return isParticipating(Session.get());
	}

	/**
	 * Check if the participant with the specified Wicket {@link Session} is
	 * participating in this planning session.
	 * 
	 * @see #getParticipant(Session)
	 * 
	 * @param session
	 *            The Wicket {@link Session} which should be associated with a
	 *            participant.
	 * @return True if the participant associated with the Wicket
	 *         {@link Session} is participating.
	 */
	public boolean isParticipating(Session session) {
		return getParticipant(session) != null;
	}

	/**
	 * Check if the participant if participating in the planning session
	 * 
	 * @param participant
	 *            The participant to check
	 * @return True if the participant if participating
	 */
	public boolean isParticipating(Participant participant) {
		return participants.contains(participant);
	}

	/**
	 * Get the participant which is associated with the Wicket {@link Session}
	 * on the current thread local: {@link Session#get()}.
	 * 
	 * @see #getParticipant(Session)
	 * 
	 * @return The participant. Null if no participant found for the current
	 *         Wicket {@link Session}.
	 */
	public Participant getParticipant() {
		return getParticipant(Session.get());
	}

	/**
	 * Get the participant associated with the specified Wicket {@link Session}.
	 * 
	 * @param session
	 *            The Wicket {@link Session} which the participant should be
	 *            associated with.
	 * @return The participant associated with the specified Wicket
	 *         {@link Session}. Null if no participant found.
	 */
	public Participant getParticipant(Session session) {
		Participant participant = null;

		for (Participant p : participants) {
			if (session == p.getSession()) {
				participant = p;
				break;
			}
		}

		return participant;
	}

	/**
	 * Check if the participant associated with the current Wicket
	 * {@link Session} has the dealer role.
	 * 
	 * @return True if the participant associated with the current Wicket
	 *         {@link Session} has the dealer role.
	 */
	public boolean isDealer() {
		return isOwner();
	}

	/**
	 * Check if the participant has the dealer role.
	 * 
	 * @param participant
	 *            The participant to check
	 * @return True if the participant has the dealer role.
	 */
	public boolean isDealer(Participant participant) {
		return isOwner(participant);
	}

	/**
	 * Check if the participant associated with the current Wicket
	 * {@link Session} has the player role.
	 * 
	 * @return True if the participant associated with the current Wicket
	 *         {@link Session} has the player role.
	 */
	public boolean isPlayer() {
		return isParticipating();
	}

	/**
	 * Check if the participant has the player role.
	 * 
	 * @param participant
	 *            The participant to check
	 * @return True if the participant has the player role.
	 */
	public boolean isPlayer(Participant participant) {
		return isParticipating(participant);
	}

	/**
	 * Check if the participant on the current Wicket {@link Session} is the
	 * owner of the planning session.
	 * 
	 * @see #isOwner(Session)
	 * 
	 * @return True if the participant on the current Wicket {@link Session} is
	 *         the owner of the planning session.
	 */
	public boolean isOwner() {
		return isOwner(Session.get());
	}

	/**
	 * Check if the participant associated with the specified Wicket
	 * {@link Session} is the owner of the planning session.
	 * 
	 * @see #isOwner(Participant)
	 * 
	 * @param session
	 *            The Wicket {@link Session} that the participant should be
	 *            associated with.
	 * @return True if the participant on the specified Wicket {@link Session}
	 *         is the owner of the planning session.
	 */
	public boolean isOwner(Session session) {
		return isOwner(getParticipant(session));
	}

	/**
	 * Check if the specified participant is the owner of the planning session.
	 * 
	 * @param participant
	 *            The participant to check if he is the owner.
	 * @return True if the specified participant is the owner of the planning
	 *         session.
	 */
	public boolean isOwner(Participant participant) {
		return owner.equals(participant);
	}

	/**
	 * Get the current planning round.
	 * 
	 * @return The current planning round.
	 */
	public PlanningRound getCurrentPlanningRound() {
		return rounds.isEmpty() ? null : rounds.peek();
	}

	/**
	 * Create a new planning round. Only the dealer may do that.
	 * 
	 * @return The new planning round
	 */
	public PlanningRound createNewRound() {
		return createNewRound(getParticipant());
	}

	/**
	 * Create a new planning round. Only the dealer may do that.
	 * 
	 * @param participant
	 *            The participant which wants to create a new round. Only the
	 *            dealer may do that
	 * @return The new planning round
	 */
	public synchronized PlanningRound createNewRound(Participant participant) {
		// Only the owner may do this
		if (isDealer(participant) == false) {
			throw new IllegalArgumentException(
					"Only the owner may create a new round");
		}

		if (getCurrentPlanningRound() == null) {
			createRound();
		} else {
			if (getCurrentPlanningRound().isComplete()) {
				createRound();
			}
		}

		return getCurrentPlanningRound();
	}

	/**
	 * Get the current status of the given participant.
	 * 
	 * @param participant
	 *            The participant to get the status for.
	 * @return The status of the participant.
	 */
	public ParticipantStatus getParticipantStatus(Participant participant) {
		ParticipantStatus status = null;

		if (isParticipating(participant)) {
			SessionStatus sessionStatus = getSessionStatus();

			if (sessionStatus == SessionStatus.SETTING_UP) {
				status = ParticipantStatus.WAITING_FOR_SESSION_TO_START;
			} else if (sessionStatus == SessionStatus.STARTED) {
				if (getCurrentPlanningRound().hasChosedCard(participant)) {
					status = ParticipantStatus.CARD_CHOSEN;
				} else {
					status = ParticipantStatus.DECIDING;
				}
			} else if (sessionStatus == SessionStatus.TERMINATED) {
				status = ParticipantStatus.TERMINATED;
			}
		} else {
			status = ParticipantStatus.UNKNOWN;
		}

		return status;
	}

	/**
	 * Get the current session status.
	 * 
	 * @return The current session status.
	 */
	public SessionStatus getSessionStatus() {
		if (terminated == false) {
			if (getCurrentPlanningRound() != null) {
				return SessionStatus.STARTED;
			} else {
				return SessionStatus.SETTING_UP;
			}
		} else {
			return SessionStatus.TERMINATED;
		}
	}

	/**
	 * Terminate the planning session. This can only be done by the dealer.
	 */
	public void terminate() {
		terminate(getParticipant());
	}

	/**
	 * Terminate the planning session. This can only be done by the dealer.
	 * 
	 * @param participant
	 *            The participant who wants to terminate the session.
	 */
	public void terminate(Participant participant) {
		if (isDealer(participant) == false) {
			throw new IllegalArgumentException(
					"Only the dealer may terminate the planning session.");
		}

		terminated = true;
	}

	/**
	 * Get the previous finished rounds.
	 * 
	 * @return The previous finished rounds.
	 */
	public List<PlanningRound> getPreviousRounds() {
		List<PlanningRound> previousRounds = new ArrayList<PlanningRound>();

		previousRounds.addAll(rounds);
		// Remove the last entry, because it's the current round
		if (previousRounds.size() > 0) {
			previousRounds.remove(previousRounds.size() - 1);
		}

		return previousRounds;
	}

	private void createRound() {
		rounds.push(new PlanningRound(this));
	}
}
