package org.planningpoker.domain;

import java.text.DecimalFormat;

public class NumberCard implements ICard {
	private static final long serialVersionUID = 1L;

	private static final String ONE_OVER_TWO = "\u00BD";
	private static final String ONE_OVER_FOUR = "\u00BC";
	private static final String THREE_OVER_FOUR = "\u00BE";

	private final double number;

	public NumberCard(double number) {
		this.number = number;
	}

	public String getDisplayValue() {
		if (number == 0) {
			return "0";
		}
		// If no decimals
		else if (Math.round(number) == new Double(number).longValue()) {
			return "" + new Double(number).longValue();
		} else if (number == 0.5) {
			// 1/2
			return ONE_OVER_TWO;
		} else if (number == 0.25) {
			// 1/4
			return ONE_OVER_FOUR;
		} else if (number == 0.75) {
			// 3/4
			return THREE_OVER_FOUR;
		} else {
			return new DecimalFormat("#.#").format(number);
		}
	}

	public String getUrlValue() {
		if (number == 0) {
			return "0";
		}
		// If no decimals
		else if (Math.round(number) == new Double(number).longValue()) {
			return "" + new Double(number).longValue();
		} else {
			return new DecimalFormat("#.#").format(number);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof NumberCard
				&& ((NumberCard) obj).number == number;
	}

	@Override
	public String toString() {
		return getDisplayValue();
	}

}
