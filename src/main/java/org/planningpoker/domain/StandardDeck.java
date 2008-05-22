package org.planningpoker.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A standard deck of cards used in normal planning poker games. The cards are:
 * 
 * <ul>
 * <li>?
 * <li>0
 * <li>&frac12;
 * <li>1
 * <li>3
 * <li>5
 * <li>8
 * <li>13
 * <li>20
 * <li>40
 * <li>100
 * <li>&infin;
 * </ul>
 */
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
