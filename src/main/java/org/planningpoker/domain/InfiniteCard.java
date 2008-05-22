package org.planningpoker.domain;

/**
 * Card representing an infinite value.
 */
public class InfiniteCard implements ICard {
	private static final long serialVersionUID = 1L;

	private static final String INFINITE = "\u221E";

	public String getDisplayValue() {
		return INFINITE;
	}

	public String getUrlValue() {
		return "infinite";
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof InfiniteCard;
	}

	@Override
	public String toString() {
		return getDisplayValue();
	}

}
