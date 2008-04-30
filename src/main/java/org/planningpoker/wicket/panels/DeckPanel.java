package org.planningpoker.wicket.panels;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.planningpoker.domain.ICard;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.cardimage.CardImageResourceReference;

public class DeckPanel extends Panel<PlanningRound> {
	private static final long serialVersionUID = 1L;

	public DeckPanel(String id, IModel<PlanningRound> model) {
		super(id, model);

		IModel<List<ICard>> cardsModel = new LoadableDetachableModel<List<ICard>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<ICard> load() {
				PlanningRound planningRound = DeckPanel.this.getModelObject();
				return planningRound.getDeck().createDeck();
			}
		};

		add(new ListView<ICard>("cards", cardsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ICard> item) {
				AjaxLink<ICard> cardLink = new AjaxLink<ICard>("cardLink", item
						.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						PlanningRound planningRound = DeckPanel.this
								.getModelObject();
						planningRound.selectCard(getModelObject());

						target.addComponent(DeckPanel.this);
						onCardChosen(target);
					}
				};
				item.add(cardLink);

				cardLink.add(new Image<CardImageResourceReference>("cardImage",
						new CardImageResourceReference(item.getModelObject())));
			}

			@Override
			public boolean isVisible() {
				return DeckPanel.this.isEnabled();
			}
		});
	}

	protected void onCardChosen(AjaxRequestTarget target) {
	}

	@Override
	public boolean isEnabled() {
		return getModelObject() != null
				&& getModelObject().isComplete() == false;
	}
}
