package org.planningpoker.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.Session;
import org.planningpoker.domain.ICard;
import org.planningpoker.domain.IDeck;

/**
 * A planning round is the duration in which the players has to agree on an
 * estimate for one given task. The round is started with all players is given a
 * fresh deck of cards and ends a card is agreed upon for the given task.
 */
public class PlanningRound implements Serializable {
	private static final long serialVersionUID = 1L;

	private static class NullCard implements ICard {
		private static final long serialVersionUID = 1L;

		public String getDisplayValue() {
			return null;
		}

		public String getUrlValue() {
			return null;
		}
	}

	private final Map<Participant, ICard> participantCards = new ConcurrentHashMap<Participant, ICard>();
	private final PlanningSession planningSession;
	private final Date timestamp = new Date();
	private String title;

	/**
	 * Package protected because it should only be {@link PlanningSession} that
	 * should create instances of this
	 */
	PlanningRound(PlanningSession planningSession) {
		for (Participant participant : planningSession.getParticipants()) {
			participantCards.put(participant, new NullCard());
		}

		this.planningSession = planningSession;
	}

	/**
	 * @return Get the deck used in this round. The deck will be used by all
	 *         players.
	 */
	public IDeck getDeck() {
		return planningSession.getDeck();
	}

	/**
	 * @return The time stamp for when this round was created.
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @return Get the planning session for this round.
	 */
	public PlanningSession getPlanningSession() {
		return planningSession;
	}

	/**
	 * @return True if all players have selected a card.
	 */
	public boolean isComplete() {
		boolean complete = true;

		for (Participant participant : participantCards.keySet()) {
			if (hasChosedCard(participant) == false) {
				complete = false;
				break;
			}
		}

		return complete;
	}

	/**
	 * @return True if all players has selected the SAME card.
	 */
	public boolean isFinished() {
		boolean finished = true;

		if (isComplete() == false) {
			finished = false;
		} else {
			ICard card = null;
			for (Participant participant : participantCards.keySet()) {
				if (card == null) {
					card = getCard(participant);
				}

				if (getCard(participant).equals(card) == false) {
					finished = false;
					break;
				}

				card = getCard(participant);
			}
		}

		return finished;
	}

	/**
	 * @return The participants in this round. The list is unmodifiable.
	 */
	public List<Participant> getParticipants() {
		return Collections.unmodifiableList(new ArrayList<Participant>(participantCards.keySet()));
	}

	/**
	 * Check if the participant has selected a card.
	 * 
	 * @param participant
	 *            Check participant to check.
	 * @return True if the participant has selected a card.
	 */
	public boolean hasChosedCard(Participant participant) {
		return participantCards.get(participant) instanceof NullCard == false;
	}

	/**
	 * Get the selected card for a specific participant
	 * 
	 * @param participant
	 *            Get the card that this participant has selected.
	 * @return The card selected by the specific participant.
	 */
	public ICard getCard(Participant participant) {
		return participantCards.get(participant);
	}

	/**
	 * Select a card for the participant, associated with the current Wicket
	 * {@link Session}.
	 * 
	 * @param card
	 *            The card to select.
	 */
	public void selectCard(ICard card) {
		selectCard(card, planningSession.getParticipant());
	}

	/**
	 * Select a card for the participant, associated with the specified Wicket
	 * {@link Session}.
	 * 
	 * @param card
	 *            The card to select
	 * @param session
	 *            The Wicket {@link Session} associated with a participant in
	 *            this round.
	 */
	public void selectCard(ICard card, Session session) {
		selectCard(card, planningSession.getParticipant(session));
	}

	/**
	 * Select a card for a specific participant.
	 * 
	 * @param card
	 *            The card to select.
	 * @param participant
	 *            Select the card for this participant.
	 */
	public void selectCard(ICard card, Participant participant) {
		if (participantCards.containsKey(participant) == false) {
			throw new IllegalArgumentException("Unknown participant: " + participant);
		}

		participantCards.put(participant, card);
	}

	/**
	 * Select a specific card for all the participants in this round.
	 * 
	 * @param card
	 *            The card to select.
	 */
	public void selectCardForAll(ICard card) {
		for (Participant participant : participantCards.keySet()) {
			selectCard(card, participant);
		}
	}

	/**
	 * Get the selected card for all the participants in this round. It only
	 * returns a non-null value if {@link #isFinished()} returns true.
	 * 
	 * @return The card that all the participants agree on.
	 */
	public ICard getSelectedCard() {
		ICard selectedCard = null;

		if (isFinished()) {
			selectedCard = participantCards.values().iterator().next();
		}

		return selectedCard;
	}

	/**
	 * Get the result of this planning round. May only be called if
	 * {@link #isComplete()} returns true.
	 * 
	 * @return The result of this planning round.
	 */
	public synchronized PlanningRoundResult getPlanningRoundResult() {
		return new PlanningRoundResult(this);
	}

	/**
	 * Get the title of this round.
	 * 
	 * @return The title of this round.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of this round.
	 * 
	 * @param title
	 *            The title of this round.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
