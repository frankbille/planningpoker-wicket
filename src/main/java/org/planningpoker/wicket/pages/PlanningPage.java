package org.planningpoker.wicket.pages;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.time.Duration;
import org.planningpoker.wicket.Participant;
import org.planningpoker.wicket.PlanningPokerApplication;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.PlanningRoundResult;
import org.planningpoker.wicket.PlanningSession;
import org.planningpoker.wicket.PlanningSession.SessionStatus;
import org.planningpoker.wicket.behaviours.AjaxCompoundUpdatingTimerBehavior;
import org.planningpoker.wicket.behaviours.AjaxCompoundUpdatingTimerBehavior.IUpdatingComponent;
import org.planningpoker.wicket.behaviours.AjaxCompoundUpdatingTimerBehavior.IUpdatingListener;
import org.planningpoker.wicket.panels.AdministrationPanel;
import org.planningpoker.wicket.panels.DeckPanel;
import org.planningpoker.wicket.panels.PlanningRoundResultTable;
import org.planningpoker.wicket.panels.PlanningTable;

public class PlanningPage extends BasePage {

	private AdministrationPanel administrationPanel;
	private DeckPanel deckPanel;
	private PlanningRoundResultTable planningRoundResultTable;
	private PlanningTable planningTable;

	public PlanningPage() {
		throw new RestartResponseAtInterceptPageException(
				PlanningPokerApplication.get().getHomePage());
	}

	public PlanningPage(final PlanningSession planningSession) {
		if (planningSession == null) {
			throw new RestartResponseAtInterceptPageException(
					PlanningPokerApplication.get().getHomePage());
		}

		if (planningSession.isParticipating() == false) {
			throw new RestartResponseAtInterceptPageException(
					new EnterNamePage(planningSession));
		}

		if (planningSession.isStarted()) {
			throw new RestartResponseAtInterceptPageException(
					PlanningPokerApplication.get().getHomePage());
		}

		if (planningSession.getSessionStatus() == SessionStatus.TERMINATED) {
			throw new RestartResponseAtInterceptPageException(
					TerminatedPage.class);
		}

		// Title
		add(new Label<String>("sessionTitle", planningSession.getTitle()));

		// Updating queue behaviour
		AjaxCompoundUpdatingTimerBehavior updatingBehavior = new AjaxCompoundUpdatingTimerBehavior(
				Duration.ONE_SECOND);
		add(updatingBehavior);

		// Get participant so he can get a heartbeat.
		Participant participant = planningSession.getParticipant();
		updatingBehavior.add(participant);

		// Redirect to terminated page if the session gets terminated
		updatingBehavior.add(new IUpdatingListener() {
			private static final long serialVersionUID = 1L;

			public boolean isEnabled() {
				return true;
			}

			public void onHeadRendered(IHeaderResponse response) {
			}

			public void onUpdated(AjaxRequestTarget target) {
				if (planningSession.getSessionStatus() == SessionStatus.TERMINATED) {
					getRequestCycle().setResponsePage(TerminatedPage.class);
				}
			}
		});

		// Planning round result
		final IModel<PlanningRound> planningRoundModel = new PropertyModel<PlanningRound>(
				planningSession, "currentPlanningRound");

		planningRoundResultTable = new PlanningRoundResultTable(
				"planningRoundResultTable",
				new PropertyModel<PlanningRoundResult>(planningRoundModel,
						"planningRoundResult")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return isVisible();
			}

			@Override
			public boolean isVisible() {
				PlanningRound planningRound = planningRoundModel.getObject();
				boolean enabled = planningRound != null
						&& planningRound.isComplete();
				return enabled;
			}
		};
		planningRoundResultTable.setOutputMarkupPlaceholderTag(true);
		updatingBehavior.add(planningRoundResultTable);
		add(planningRoundResultTable);

		planningTable = new PlanningTable("planningTable",
				new Model<PlanningSession>(planningSession));
		updatingBehavior.add(planningTable, new IUpdatingComponent() {
			private static final long serialVersionUID = 1L;

			public Object getStateObject(Component<?> component) {
				StringBuilder state = new StringBuilder();
				for (Participant participant : planningSession
						.getParticipants()) {
					state.append(participant.getName());
					state.append(participant.getHealth());

					PlanningRound currentPlanningRound = planningSession
							.getCurrentPlanningRound();
					if (currentPlanningRound != null) {
						state.append(currentPlanningRound.getCard(participant));
					}
				}
				return state;
			}

			public boolean isEnabled(Component<?> component) {
				return true;
			}
		});
		add(planningTable);

		deckPanel = new DeckPanel("deckPanel", planningRoundModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onCardChosen(AjaxRequestTarget target) {
				target.addComponent(planningTable);
				target.addComponent(planningRoundResultTable);
				target.addComponent(administrationPanel);
			}
		};
		updatingBehavior.add(deckPanel, new IUpdatingComponent() {
			private static final long serialVersionUID = 1L;

			public Object getStateObject(Component<?> component) {
				return deckPanel.isEnabled();
			}

			public boolean isEnabled(Component<?> component) {
				return true;
			}
		});
		add(deckPanel);

		administrationPanel = new AdministrationPanel("administrationPanel",
				new Model<PlanningSession>(planningSession)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onNewRoundCreated(AjaxRequestTarget target) {
				target.addComponent(planningTable);
				target.addComponent(deckPanel);
				target.addComponent(planningRoundResultTable);
			}
		};
		updatingBehavior.add(administrationPanel, new IUpdatingComponent() {
			private static final long serialVersionUID = 1L;

			public boolean isEnabled(Component<?> component) {
				PlanningSession modelObject = (PlanningSession) component
						.getModelObject();
				PlanningRound currentPlanningRound = modelObject
						.getCurrentPlanningRound();
				return modelObject.isStarted()
						&& currentPlanningRound.isComplete() == false;
			}

			public Object getStateObject(Component<?> component) {
				return isEnabled(component);
			}
		});
		add(administrationPanel);

	}

	@Override
	protected IModel<String> getPageTitle() {
		return new StringResourceModel("planningPoker", this, null);
	}

}
