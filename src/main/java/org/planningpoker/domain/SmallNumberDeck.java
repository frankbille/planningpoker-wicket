package org.planningpoker.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A deck with small number cards, ranging between 1-12. Also including ? and
 * infinite. This fits with what ScrumWorks suggests.
 */
public class SmallNumberDeck implements IDeck {
	private static final long serialVersionUID = 1L;

	public List<ICard> createDeck() {
		List<ICard> cards = new ArrayList<ICard>();
		cards.add(new QuestionCard());
		for (int i = 1; i <= 12; i++) {
			cards.add(new NumberCard(i));
		}
		cards.add(new InfiniteCard());
		return cards;
	}

}
