package org.planningpoker.wicket.panels;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.planningpoker.domain.ICard;
import org.planningpoker.wicket.Participant;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.PlanningSession;
import org.planningpoker.wicket.Participant.Health;
import org.planningpoker.wicket.PlanningSession.ParticipantStatus;
import org.planningpoker.wicket.behaviours.ClickConfirmBehavior;
import org.planningpoker.wicket.components.GenericPanel;

public class PlanningTable extends GenericPanel<PlanningSession> {
	private static final long serialVersionUID = 1L;

	public PlanningTable(String id, IModel<PlanningSession> model) {
		super(id, model);

		IModel<List<Participant>> listModel = new PropertyModel<List<Participant>>(
				model, "participants");

		add(new ListView<Participant>("participants", listModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Participant> item) {
				WebMarkupContainer userType = new WebMarkupContainer("userType");
				userType.add(new AttributeModifier("class", true,
						new AbstractReadOnlyModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							public String getObject() {
								return PlanningTable.this.getModelObject()
										.isOwner(item.getModelObject()) ? "owner"
										: "player";
							}
						}));
				item.add(userType);

				Label nameLabel = new Label("name", new PropertyModel<String>(
						item.getModel(), "name"));
				nameLabel.add(new AttributeModifier("style", true,
						new AbstractReadOnlyModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							public String getObject() {
								return PlanningTable.this.getModelObject()
										.getParticipant() == item
										.getModelObject() ? "text-decoration: underline"
										: null;
							}
						}));
				item.add(nameLabel);

				// Status
				AbstractReadOnlyModel<ParticipantStatus> statusModel = new AbstractReadOnlyModel<ParticipantStatus>() {
					private static final long serialVersionUID = 1L;

					@Override
					public ParticipantStatus getObject() {
						PlanningSession planningSession = PlanningTable.this
								.getModelObject();
						return planningSession.getParticipantStatus(item
								.getModelObject());
					}
				};
				item.add(new Label("status", new StringResourceModel(
						"status.${name()}", this, statusModel)));

				WebMarkupContainer pingComponent = new WebMarkupContainer(
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

				IModel<String> cardModel = new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
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
									return card.getDisplayValue();
								}
							}
						}

						return "";
					}
				};
				item.add(new Label("card", cardModel));

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
