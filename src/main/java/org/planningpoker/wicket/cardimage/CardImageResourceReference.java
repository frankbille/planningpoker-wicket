package org.planningpoker.wicket.cardimage;

import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.planningpoker.domain.ICard;

public class CardImageResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;

	private final ICard card;

	private final double scale;

	public CardImageResourceReference(ICard card) {
		this(1, card);
	}

	public CardImageResourceReference(double scale, ICard card) {
		super(CardImageResourceReference.class, card != null ? card
				.getUrlValue()
				+ "_" + scale + ".png" : "default_" + scale + ".png");
		this.scale = scale;
		this.card = card;
	}

	@Override
	protected Resource newResource() {
		return new CardImageResource(scale, card);
	}

}
