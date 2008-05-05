package org.planningpoker.wicket.panels;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.planningpoker.wicket.Participant.Health;
import org.planningpoker.wicket.PlanningSession.Status;
import org.planningpoker.wicket.behaviours.ClickConfirmBehavior;
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

				WebMarkupContainer<Object> pingComponent = new WebMarkupContainer<Object>(
						"ping");
				AbstractReadOnlyModel<String> cssClassModel = new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						Participant participant = item.getModelObject();
						Health health = participant.getHealth();

						String cssClass = null;

						if (health == Health.GOOD) {
							cssClass = "participantStatusGood";
						} else if (health == Health.ILL) {
							cssClass = "participantStatusIll";
						} else if (health == Health.SICK) {
							cssClass = "participantStatusSick";
						} else if (health == Health.DYING) {
							cssClass = "participantStatusDying";
						} else {
							cssClass = "participantStatusDead";
						}

						return cssClass;
					}
				};
				pingComponent.add(new AttributeModifier("class", true,
						cssClassModel));
				item.add(pingComponent);

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

				// Remove link
				AjaxLink<Participant> removeLink = new AjaxLink<Participant>(
						"removeLink", item.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						PlanningSession planningSession = PlanningTable.this
								.getModelObject();
						planningSession.remove(getModelObject());
						target.addComponent(PlanningTable.this);
					}

					@Override
					public boolean isVisible() {
						return PlanningTable.this.getModelObject().isOwner()
								&& PlanningTable.this.getModelObject()
										.getParticipant().equals(
												getModelObject()) == false;
					}
				};
				removeLink.add(new ClickConfirmBehavior(
						new StringResourceModel("confirmRemoveParticipant",
								this, item.getModel())));
				item.add(removeLink);
			}
		});
	}
}
