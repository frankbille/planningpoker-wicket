package org.planningpoker.domain;

import java.io.Serializable;

/**
 * A card in the deck, which holds a specific value.
 */
public interface ICard extends Serializable {

	/**
	 * Get the value to display to the user.
	 * 
	 * @return The value to display to the user.
	 */
	String getDisplayValue();

	/**
	 * Get an URL safe representation of the the card value.
	 * 
	 * @return An URL safe representation of the the card value.
	 */
	String getUrlValue();

}
