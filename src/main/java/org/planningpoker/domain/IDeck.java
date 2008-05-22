package org.planningpoker.domain;

import java.io.Serializable;
import java.util.List;

/**
 * A deck can create a new set of cards. Normally in planning poker the cards
 * that is available is fixed, like in {@link StandardDeck}, but it's possible
 * to create custom decks.
 */
public interface IDeck extends Serializable {

	/**
	 * Create a new deck of cards. This is normally used to provide a player
	 * with a fresh set of cards.
	 * 
	 * @return A new deck of cards.
	 */
	List<ICard> createDeck();

}
