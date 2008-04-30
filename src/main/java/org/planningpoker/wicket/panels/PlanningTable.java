package org.planningpoker.wicket.panels;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.planningpoker.domain.ICard;
import org.planningpoker.wicket.Participant;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.PlanningSession;
import org.planningpoker.wicket.PlanningSession.Status;
import org.planningpoker.wicket.cardimage.CardImageResourceReference;

public class PlanningTable extends Panel<PlanningSession> {
	private static final long serialVersionUID = 1L;

	public PlanningTable(String id, IModel<PlanningSession> model) {
		super(id, model);

		IModel<List<Participant>> listModel = new PropertyModel<List<Participant>>(
				model, "participants");

		add(new ListView<Participant>("participants", listModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Participant> item) {
				item.add(new Label<String>("name", new PropertyModel<String>(
						item.getModel(), "name")));

				// Status
				AbstractReadOnlyModel<Status> statusModel = new AbstractReadOnlyModel<Status>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Status getObject() {
						PlanningSession planningSession = PlanningTable.this
								.getModelObject();
						return planningSession.getParticipantStatus(item
								.getModelObject());
					}
				};
				item.add(new Label<String>("status", new StringResourceModel(
						"status.${name()}", this, statusModel)));

				item.add(new Label<Long>("ping", new PropertyModel<Long>(item
						.getModel(), "millisSinceLastPing")));

				IModel<CardImageResourceReference> cardModel = new AbstractReadOnlyModel<CardImageResourceReference>() {
					private static final long serialVersionUID = 1L;

					@Override
					public CardImageResourceReference getObject() {
						PlanningSession planningSession = PlanningTable.this
								.getModelObject();
						PlanningRound currentPlanningTurn = planningSession
								.getCurrentPlanningRound();
						if (currentPlanningTurn != null) {
							Participant participant = item.getModelObject();
							if (currentPlanningTurn.hasChosedCard(participant)) {
								if (currentPlanningTurn.isComplete()
										|| planningSession.getParticipant() == participant) {
									ICard card = currentPlanningTurn
											.getCard(participant);
									return new CardImageResourceReference(0.5,
											card);
								}
							}
						}

						return new CardImageResourceReference(0.5, null);
					}
				};
				item.add(new Image<CardImageResourceReference>("card",
						cardModel));
			}
		});
	}
}
