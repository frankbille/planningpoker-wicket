package org.planningpoker.wicket.panels;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.PlanningSession;
import org.planningpoker.wicket.models.DateFormatModel;

/**
 * Displays the results of the rounds in the session.
 */
public class PlanningSessionResultTable extends Panel<PlanningSession> {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct
	 * 
	 * @param id
	 *            Wicket id
	 * @param model
	 *            Model holding the {@link PlanningSession} object.
	 */
	public PlanningSessionResultTable(String id, IModel<PlanningSession> model) {
		super(id, model);

		IModel<List<PlanningRound>> previousRoundsModel = new LoadableDetachableModel<List<PlanningRound>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PlanningRound> load() {
				PlanningSession planningSession = getModelObject();

				List<PlanningRound> previousRounds = planningSession
						.getPreviousRounds();

				Collections.sort(previousRounds,
						new Comparator<PlanningRound>() {
							public int compare(PlanningRound o1,
									PlanningRound o2) {
								int compare = 0;

								if (o1 != null && o2 != null) {
									compare = o1.getTimestamp().compareTo(
											o2.getTimestamp());
								} else if (o1 != null) {
									compare = -1;
								} else if (o2 != null) {
									compare = 1;
								}

								return compare;
							}
						});

				return previousRounds;
			}
		};

		add(new ListView<PlanningRound>("rounds", previousRoundsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PlanningRound> item) {
				item.add(new Label<Integer>("id", new Model<Integer>(item
						.getIndex() + 1)));

				final AjaxEditableLabel<String> roundTitle = new AjaxEditableLabel<String>(
						"title", new PropertyModel<String>(item.getModel(),
								"title")) {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isEnabled() {
						PlanningSession planningSession = PlanningSessionResultTable.this
								.getModelObject();
						return planningSession.isOwner();
					}

					@Override
					protected String defaultNullLabel() {
						return "No round title";
					}
				};
				roundTitle.add(new AttributeModifier("class", true,
						new AbstractReadOnlyModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							public String getObject() {
								return roundTitle.getModelObject() != null ? null
										: PlanningSessionResultTable.this
												.getModelObject().isOwner() ? "editRoundTitle"
												: "noRoundTitle";
							}
						}));
				item.add(roundTitle);

				item.add(new Label<String>("selectedCard", item
						.getModelObject().getSelectedCard().getDisplayValue()));

				item.add(new Label<String>("timestamp", new DateFormatModel(
						new PropertyModel<Date>(item.getModel(), "timestamp"),
						"HH:mm:ss")));
			}
		});
	}
}
