package org.planningpoker.wicket.utils;

import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.planningpoker.domain.IDeck;
import org.planningpoker.domain.SmallNumberDeck;
import org.planningpoker.domain.StandardDeck;

/**
 * Renderer for a card {@link IDeck deck}.
 */
public class DeckRenderer implements IChoiceRenderer<IDeck> {
	private static final long serialVersionUID = 1L;

	public Object getDisplayValue(IDeck object) {
		String display = null;

		if (object instanceof StandardDeck) {
			display = new Localizer().getString("Deck.standard", null);
		} else if (object instanceof SmallNumberDeck) {
			display = new Localizer().getString("Deck.smallNumber", null);
		}

		return display;
	}

	public String getIdValue(IDeck object, int index) {
		return "" + object.hashCode();
	}

}
