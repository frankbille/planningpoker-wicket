package org.planningpoker.domain;

public class QuestionCard implements ICard {
	private static final long serialVersionUID = 1L;

	private static final String QUESTION_MARK = "\u003F";

	public String getDisplayValue() {
		return QUESTION_MARK;
	}

	public String getUrlValue() {
		return "question";
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof QuestionCard;
	}

	@Override
	public String toString() {
		return getDisplayValue();
	}

}
