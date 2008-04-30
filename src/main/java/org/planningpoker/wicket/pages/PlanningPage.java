package org.planningpoker.wicket.pages;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import org.planningpoker.wicket.behaviours.AjaxCompoundUpdatingTimerBehavior;
import org.planningpoker.wicket.behaviours.AjaxCompoundUpdatingTimerBehavior.IUpdatingComponent;
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

	public PlanningPage(PlanningSession planningSession) {
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

		// Updating queue behaviour
		AjaxCompoundUpdatingTimerBehavior updatingBehavior = new AjaxCompoundUpdatingTimerBehavior(
				Duration.ONE_SECOND);
		add(updatingBehavior);

		// Get participant so he can get a heartbeat.
		Participant participant = planningSession.getParticipant();
		updatingBehavior.add(participant);

		final IModel<PlanningRound> planningRoundModel = new PropertyModel<PlanningRound>(
				planningSession, "currentPlanningRound");

		planningRoundResultTable = new PlanningRoundResultTable(
				"planningRoundResultTable",
				new PropertyModel<PlanningRoundResult>(planningRoundModel,
						"planningRoundResult")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				PlanningRound planningRound = planningRoundModel.getObject();
				boolean enabled = planningRound != null
						&& planningRound.isComplete();
				return enabled;
			}
		};
		updatingBehavior.add(planningRoundResultTable);
		add(planningRoundResultTable);

		planningTable = new PlanningTable("planningTable",
				new Model<PlanningSession>(planningSession));
		updatingBehavior.add(planningTable);
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
