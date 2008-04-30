package org.planningpoker.domain;

import java.util.ArrayList;
import java.util.List;

public class StandardDeck implements IDeck {
	private static final long serialVersionUID = 1L;

	public List<ICard> createDeck() {
		List<ICard> cards = new ArrayList<ICard>();

		cards.add(new QuestionCard());
		cards.add(new NumberCard(0));
		cards.add(new NumberCard(0.5));
		cards.add(new NumberCard(1));
		cards.add(new NumberCard(3));
		cards.add(new NumberCard(5));
		cards.add(new NumberCard(8));
		cards.add(new NumberCard(13));
		cards.add(new NumberCard(20));
		cards.add(new NumberCard(40));
		cards.add(new NumberCard(100));
		cards.add(new InfiniteCard());

		return cards;
	}

}
