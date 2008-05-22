package org.planningpoker.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.planningpoker.domain.ICard;

public class PlanningRoundResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private static class CardCount implements Serializable,
			Comparable<CardCount> {
		private static final long serialVersionUID = 1L;

		private final ICard card;
		private int count;

		public CardCount(ICard card) {
			this.card = card;
			count = 0;
		}

		public ICard getCard() {
			return card;
		}

		public int getCount() {
			return count;
		}

		public void yetAnotherCard() {
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

	public PlanningRoundResult(PlanningRound planningRound) {
		this.planningRound = planningRound;
	}

	public void addCard(ICard card) {
		CardCount cardCount = getCardCountForCard(card);

		if (cardCount == null) {
			cardCount = new CardCount(card);
			cards.add(cardCount);
		}

		cardCount.yetAnotherCard();
	}

	public List<ICard> getCards() {
		Collections.sort(cards);

		List<ICard> cardList = new ArrayList<ICard>();

		for (CardCount cardCount : cards) {
			cardList.add(cardCount.getCard());
		}

		return cardList;
	}

	public int getCardCount(ICard card) {
		return getCardCountForCard(card).getCount();
	}

	public int getCardTotals() {
		int total = 0;

		for (CardCount cardCount : cards) {
			total += cardCount.getCount();
		}

		return total;
	}

	public double getCardPercentage(ICard card) {
		int cardCount = getCardCount(card);
		int cardTotals = getCardTotals();

		return (double) cardCount / (double) cardTotals;
	}

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
}
