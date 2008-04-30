package org.planningpoker.wicket.panels;

import java.text.NumberFormat;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.planningpoker.domain.ICard;
import org.planningpoker.wicket.PlanningRoundResult;
import org.planningpoker.wicket.cardimage.CardImageResourceReference;

public class PlanningRoundResultTable extends Panel<PlanningRoundResult> {
	private static final long serialVersionUID = 1L;

	public PlanningRoundResultTable(String id, IModel<PlanningRoundResult> model) {
		super(id, model);

		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);

		add(new ListView<ICard>("cards", new PropertyModel<List<ICard>>(model,
				"cards")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ICard> item) {
				item.add(new Image<CardImageResourceReference>("card",
						new CardImageResourceReference(0.5, item
								.getModelObject())));

				final PlanningRoundResult planningRoundResult = PlanningRoundResultTable.this
						.getModelObject();

				item.add(new Label<Integer>("count",
						new Model<Integer>(planningRoundResult
								.getCardCount(item.getModelObject()))));

				IModel<String> pctModel = new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						return NumberFormat.getPercentInstance().format(
								planningRoundResult.getCardPercentage(item
										.getModelObject()));
					}
				};

				item.add(new Label<String>("percentage", pctModel));
			}

			@Override
			public boolean isVisible() {
				return PlanningRoundResultTable.this.isEnabled();
			}
		});
	}
}
