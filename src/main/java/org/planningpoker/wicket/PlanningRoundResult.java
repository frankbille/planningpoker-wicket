package org.planningpoker.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.planningpoker.domain.ICard;
import org.planningpoker.domain.NumberCard;

/**
 * The result of a specific planning round. This class returns statistics of a
 * planning round, such as how many selected a given card etc.
 */
public class PlanningRoundResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private static class CardCount implements Serializable, Comparable<CardCount> {
		private static final long serialVersionUID = 1L;

		private final ICard card;
		private int count;

		CardCount(ICard card) {
			this.card = card;
			count = 0;
		}

		ICard getCard() {
			return card;
		}

		int getCount() {
			return count;
		}

		void yetAnotherCard() {
			count++;
		}

		@Override
		public boolean equals(Object obj) {
			boolean equals = false;

			if (obj != null) {
				if (obj instanceof CardCount) {
					CardCount cardCount = (CardCount) obj;

					if (card.equals(cardCount.card) && count == cardCount.count) {
						equals = true;
					}
				} else if (obj instanceof ICard) {
					ICard card = (ICard) obj;

					if (this.card.equals(card)) {
						equals = true;
					}
				}
			}

			return equals;
		}

		public int compareTo(CardCount o) {
			int compare = 0;

			if (o != null) {
				// Reverse the order so the top score is first
				compare = new Integer(count).compareTo(o.count) * -1;
			} else {
				compare = -1;
			}

			return compare;
		}
	}

	private final List<CardCount> cards = new ArrayList<CardCount>();
	private final PlanningRound planningRound;

	/**
	 * Construct
	 * 
	 * @param planningRound
	 *            The planning round to create statistics from
	 */
	public PlanningRoundResult(PlanningRound planningRound) {
		if (planningRound.isComplete() == false) {
			throw new IllegalStateException("Can't get the result until the round is complete.");
		}

		this.planningRound = planningRound;

		for (Participant participant : planningRound.getParticipants()) {
			ICard card = planningRound.getCard(participant);
			addCard(card);
		}
	}

	/**
	 * Get all the unique cards in the round. This means that if 4 participants
	 * had selected the same card it will only be returns once in this method.
	 * It is possible to get the card count by using the
	 * {@link #getCardCount(ICard)} method.
	 * 
	 * @return A list of the unique cards in the round.
	 */
	public List<ICard> getCards() {
		Collections.sort(cards);

		List<ICard> cardList = new ArrayList<ICard>();

		for (CardCount cardCount : cards) {
			cardList.add(cardCount.getCard());
		}

		return cardList;
	}

	/**
	 * Get the number of participants that had selected a specific card.
	 * 
	 * @param card
	 *            The card to get the count for.
	 * @return The number of participants that had selected the specified card.
	 */
	public int getCardCount(ICard card) {
		return getCardCountForCard(card).getCount();
	}

	/**
	 * Get the number of total selected cards. This number should always be the
	 * same as the number of participants in the round.
	 * 
	 * @return The total number of cards in the round.
	 */
	public int getCardTotals() {
		int total = 0;

		for (CardCount cardCount : cards) {
			total += cardCount.getCount();
		}

		return total;
	}

	/**
	 * Simple average based on cardvalues/cardtotals Only number cards are used!
	 * 
	 * @return average of round score
	 */
	public float getCardAverage() {
		float total = 0;

		for (ICard card : getCards()) {
			if (NumberCard.class.isInstance(card)) {
				NumberCard n = (NumberCard) card;
				total += n.getNumber();
			}
		}
		if (total != 0 && getCardTotals() != 0) {
			total = total / getCardTotals();
		}
		return total;

	}

	/**
	 * Get how many participants had selected the specified card as a percentage
	 * of the total number of cards selected.
	 * 
	 * @param card
	 *            The card to get the percentage of the total number of cards
	 *            selected for.
	 * @return The percentage of how many selected this card out of all the
	 *         cards selected.
	 */
	public double getCardPercentage(ICard card) {
		int cardCount = getCardCount(card);
		int cardTotals = getCardTotals();

		return (double) cardCount / (double) cardTotals;
	}

	/**
	 * @return The planning round.
	 */
	public PlanningRound getPlanningRound() {
		return planningRound;
	}

	private CardCount getCardCountForCard(ICard card) {
		CardCount cardCount = null;

		for (CardCount cc : cards) {
			if (cc.equals(card)) {
				cardCount = cc;
				break;
			}
		}

		return cardCount;
	}

	private void addCard(ICard card) {
		CardCount cardCount = getCardCountForCard(card);

		if (cardCount == null) {
			cardCount = new CardCount(card);
			cards.add(cardCount);
		}

		cardCount.yetAnotherCard();
	}
}
