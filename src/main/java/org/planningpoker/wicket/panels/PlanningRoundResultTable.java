package org.planningpoker.wicket.panels;

import java.text.NumberFormat;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.planningpoker.domain.ICard;
import org.planningpoker.wicket.PlanningRoundResult;

/**
 * Renders the planning round result.
 */
public class PlanningRoundResultTable extends Panel<PlanningRoundResult> {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct
	 * 
	 * @param id
	 *            Wicket id
	 * @param model
	 *            Model containing the planning round result
	 */
	public PlanningRoundResultTable(String id, IModel<PlanningRoundResult> model) {
		super(id, model);

		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);

		add(new ListView<ICard>("cards", new PropertyModel<List<ICard>>(model, "cards")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ICard> item) {
				item.add(new Label<String>("card", new PropertyModel<String>(item.getModel(),
						"displayValue")));

				final PlanningRoundResult planningRoundResult = PlanningRoundResultTable.this
						.getModelObject();

				item.add(new Label<Integer>("count", new Model<Integer>(planningRoundResult
						.getCardCount(item.getModelObject()))));

				IModel<String> pctModel = new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						return NumberFormat.getPercentInstance().format(
								planningRoundResult.getCardPercentage(item.getModelObject()));
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
