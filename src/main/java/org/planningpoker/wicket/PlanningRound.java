package org.planningpoker.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Session;
import org.planningpoker.domain.ICard;
import org.planningpoker.domain.IDeck;

public class PlanningRound implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Map<Participant, ICard> participantCards = new LinkedHashMap<Participant, ICard>();
	private final PlanningSession planningSession;
	private PlanningRoundResult planningRoundResult;

	/**
	 * Package protected because it should only be {@link PlanningSession} that
	 * should create instances of this
	 */
	PlanningRound(PlanningSession planningSession) {
		for (Participant participant : planningSession.getParticipants()) {
			participantCards.put(participant, null);
		}

		this.planningSession = planningSession;
	}

	public IDeck getDeck() {
		return planningSession.getDeck();
	}

	public boolean isComplete() {
		synchronized (participantCards) {
			boolean complete = true;

			for (Participant participant : participantCards.keySet()) {
				if (hasChosedCard(participant) == false) {
					complete = false;
					break;
				}
			}

			return complete;
		}
	}

	public List<Participant> getParticipants() {
		return Collections.unmodifiableList(new ArrayList<Participant>(
				participantCards.keySet()));
	}

	public boolean hasChosedCard(Participant participant) {
		synchronized (participantCards) {
			return participantCards.get(participant) != null;
		}
	}

	public ICard getCard(Participant participant) {
		synchronized (participantCards) {
			return participantCards.get(participant);
		}
	}

	public void selectCard(ICard card) {
		selectCard(card, planningSession.getParticipant());
	}

	public void selectCard(ICard card, Session session) {
		selectCard(card, planningSession.getParticipant(session));
	}

	public void selectCard(ICard card, Participant participant) {
		synchronized (participantCards) {
			if (participantCards.containsKey(participant) == false) {
				throw new IllegalArgumentException("Unknown participant: "
						+ participant);
			}

			participantCards.put(participant, card);
		}
	}

	public synchronized PlanningRoundResult getPlanningRoundResult() {
		if (isComplete() == false) {
			throw new IllegalStateException(
					"Can't get the result until the round is complete.");
		}

		if (planningRoundResult == null) {
			planningRoundResult = new PlanningRoundResult();

			for (Participant participant : participantCards.keySet()) {
				ICard card = participantCards.get(participant);
				planningRoundResult.addCard(card);
			}

		}

		return planningRoundResult;
	}

}
