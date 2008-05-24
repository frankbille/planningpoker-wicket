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

	public List<Participant> getParticipants() {
		return Collections.unmodifiableList(new ArrayList<Participant>(participantCards.keySet()));
	}

	public boolean hasChosedCard(Participant participant) {
		return participantCards.get(participant) instanceof NullCard == false;
	}

	public ICard getCard(Participant participant) {
		return participantCards.get(participant);
	}

	public void selectCard(ICard card) {
		selectCard(card, planningSession.getParticipant());
	}

	public void selectCard(ICard card, Session session) {
		selectCard(card, planningSession.getParticipant(session));
	}

	public void selectCard(ICard card, Participant participant) {
		if (participantCards.containsKey(participant) == false) {
			throw new IllegalArgumentException("Unknown participant: " + participant);
		}

		participantCards.put(participant, card);
	}

	public void selectCardForAll(ICard card) {
		for (Participant participant : participantCards.keySet()) {
			selectCard(card, participant);
		}
	}

	public synchronized PlanningRoundResult getPlanningRoundResult() {
		if (isComplete() == false) {
			throw new IllegalStateException("Can't get the result until the round is complete.");
		}

		PlanningRoundResult planningRoundResult = new PlanningRoundResult(this);

		for (Participant participant : participantCards.keySet()) {
			ICard card = participantCards.get(participant);
			planningRoundResult.addCard(card);
		}

		return planningRoundResult;
	}

	public ICard getSelectedCard() {
		ICard selectedCard = null;

		if (isFinished()) {
			selectedCard = participantCards.values().iterator().next();
		}

		return selectedCard;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
