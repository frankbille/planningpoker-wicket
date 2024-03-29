package org.planningpoker.wicket.pages;

import java.util.List;

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
import org.planningpoker.wicket.PlanningSession.ParticipantStatus;
import org.planningpoker.wicket.PlanningSession.SessionStatus;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.AjaxCompoundUpdatingTimerBehavior;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.ComponentUpdatingListener;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.HeartBeatUpdatingListener;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.IUpdatingComponent;
import org.planningpoker.wicket.behaviours.ajax.timer.compound.AjaxCompoundUpdatingTimerBehavior.IUpdatingListener;
import org.planningpoker.wicket.components.PlanningRoundTitleEditableLabel;
import org.planningpoker.wicket.panels.AdministrationPanel;
import org.planningpoker.wicket.panels.DeckPanel;
import org.planningpoker.wicket.panels.PlanningRoundResultTable;
import org.planningpoker.wicket.panels.PlanningSessionResultTable;
import org.planningpoker.wicket.panels.PlanningTable;

/**
 * Planning page, where the planning session is "played".
 */
public class PlanningPage extends BasePage {

	private AdministrationPanel administrationPanel;
	private DeckPanel deckPanel;
	private PlanningRoundResultTable planningRoundResultTable;
	private PlanningTable planningTable;
	private PlanningSessionResultTable planningSessionResultTable;
	private PlanningRoundTitleEditableLabel roundTitle;
	private AjaxCompoundUpdatingTimerBehavior updatingBehavior;

	/**
	 * Constructor. Used because the page should be bookmarkable, but if it's
	 * accessed directly, it should redirect to the frontpage.
	 */
	public PlanningPage() {
		throw new RestartResponseAtInterceptPageException(PlanningPokerApplication.get().getHomePage());
	}

	/**
	 * Constructor. Start the page with a planning session.
	 * 
	 * @param planningSession
	 */
	public PlanningPage(PlanningSession planningSession) {
		if (planningSession == null) {
			throw new RestartResponseAtInterceptPageException(PlanningPokerApplication.get().getHomePage());
		}

		if (planningSession.isParticipating() == false) {
			throw new RestartResponseAtInterceptPageException(new EnterNamePage(planningSession));
		}

		if (planningSession.isStarted()) {
			throw new RestartResponseAtInterceptPageException(PlanningPokerApplication.get().getHomePage());
		}

		if (planningSession.getSessionStatus() == SessionStatus.TERMINATED) {
			throw new RestartResponseAtInterceptPageException(TerminatedPage.class);
		}

		setDefaultModel(new Model<PlanningSession>(planningSession));

		updatingBehavior = new AjaxCompoundUpdatingTimerBehavior(Duration.ONE_SECOND);
		add(updatingBehavior);

		// Get participant so he can get a heart beat.
		addHeartBeat();

		// Planning session title
		addSessionTitle();

		// Planning round title
		addRoundTitle();

		// Planning round result model
		IModel<PlanningRound> planningRoundModel = new PropertyModel<PlanningRound>(planningSession,
				"currentPlanningRound");

		/*
		 * UI ELEMENTS
		 */

		// Administration panel
		addAdministrationPanel();

		// Planning round result table
		addPlanningRoundResultTable(planningRoundModel);

		// Planning round table
		addPlanningTable(planningSession);

		// Deck panel
		addDeckPanel(planningRoundModel);

		// Planning session result table
		addPlanningSessionResultTable();
	}

	/**
	 * @return The model.
	 */
	@SuppressWarnings("unchecked")
	public IModel<PlanningSession> getModel() {
		return (IModel<PlanningSession>) getDefaultModel();
	}

	/**
	 * @return The model object.
	 */
	public PlanningSession getModelObject() {
		return (PlanningSession) getDefaultModelObject();
	}

	private void addPlanningSessionResultTable() {
		planningSessionResultTable = new PlanningSessionResultTable("planningSessionResultTable", getModel());
		updatingBehavior.add(new ComponentUpdatingListener<PlanningSessionResultTable>(planningSessionResultTable,
				new IUpdatingComponent<PlanningSessionResultTable>() {
					private static final long serialVersionUID = 1L;

					public Object getStateObject(PlanningSessionResultTable component) {
						StringBuilder b = new StringBuilder();

						List<PlanningRound> previousRounds = getModelObject().getPreviousRounds();

						for (PlanningRound planningRound : previousRounds) {
							b.append(planningRound.getTitle());
						}

						return b;
					}
				}));
		add(planningSessionResultTable);
	}

	private void addAdministrationPanel() {
		administrationPanel = new AdministrationPanel("administrationPanel", getModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onNewRoundCreated(AjaxRequestTarget target) {
				target.addComponent(planningTable);
				target.addComponent(deckPanel);
				target.addComponent(planningRoundResultTable);
				target.addComponent(planningSessionResultTable);
				target.addComponent(roundTitle);
			}
		};
		updatingBehavior.add(new ComponentUpdatingListener<AdministrationPanel>(administrationPanel,
				new IUpdatingComponent<AdministrationPanel>() {
					private static final long serialVersionUID = 1L;

					public Object getStateObject(AdministrationPanel component) {
						PlanningSession modelObject = component.getModelObject();
						PlanningRound currentPlanningRound = modelObject.getCurrentPlanningRound();
						return modelObject.isStarted() && currentPlanningRound.isFinished() == false;
					}
				}));
		add(administrationPanel);
	}

	private void addDeckPanel(final IModel<PlanningRound> planningRoundModel) {
		deckPanel = new DeckPanel("deckPanel", planningRoundModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onCardChosen(AjaxRequestTarget target) {
				target.addComponent(planningTable);
				target.addComponent(planningRoundResultTable);
				target.addComponent(administrationPanel);
			}
		};
		updatingBehavior.add(new ComponentUpdatingListener<DeckPanel>(deckPanel, new IUpdatingComponent<DeckPanel>() {
			private static final long serialVersionUID = 1L;

			public Object getStateObject(DeckPanel component) {
				return component.isEnabled();
			}
		}));
		add(deckPanel);
	}

	private void addPlanningTable(PlanningSession planningSession) {
		planningTable = new PlanningTable("planningTable", new Model<PlanningSession>(planningSession));
		updatingBehavior.add(new ComponentUpdatingListener<PlanningTable>(planningTable,
				new IUpdatingComponent<PlanningTable>() {
					private static final long serialVersionUID = 1L;

					public Object getStateObject(PlanningTable component) {
						StringBuilder state = new StringBuilder();
						for (Participant participant : component.getModelObject().getParticipants()) {
							state.append(participant.getName());
							state.append(participant.getHealth());

							PlanningRound currentPlanningRound = component.getModelObject().getCurrentPlanningRound();
							if (currentPlanningRound != null) {
								state.append(currentPlanningRound.getCard(participant));
							}
						}
						return state;
					}
				}));
		add(planningTable);
	}

	private void addPlanningRoundResultTable(final IModel<PlanningRound> planningRoundModel) {
		planningRoundResultTable = new PlanningRoundResultTable("planningRoundResultTable",
				new PropertyModel<PlanningRoundResult>(planningRoundModel, "planningRoundResult")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return isVisible();
			}

			@Override
			public boolean isVisible() {
				PlanningRound planningRound = planningRoundModel.getObject();
				boolean enabled = planningRound != null && planningRound.isComplete();
				return enabled;
			}
		};
		planningRoundResultTable.setOutputMarkupPlaceholderTag(true);
		updatingBehavior.add(new ComponentUpdatingListener<PlanningRoundResultTable>(planningRoundResultTable));
		add(planningRoundResultTable);
	}

	private void addRoundTitle() {
		IModel<PlanningRound> roundModel = new PropertyModel<PlanningRound>(getModel(), "currentPlanningRound");

		roundTitle = new PlanningRoundTitleEditableLabel("roundTitle", roundModel);
		roundTitle.setOutputMarkupPlaceholderTag(true);
		updatingBehavior.add(new ComponentUpdatingListener<PlanningRoundTitleEditableLabel>(roundTitle,
				new IUpdatingComponent<PlanningRoundTitleEditableLabel>() {
					private static final long serialVersionUID = 1L;

					public Object getStateObject(PlanningRoundTitleEditableLabel component) {
						StringBuilder b = new StringBuilder();
						b.append(component.isEnabled());
						b.append(component.isVisible());
						if (component.getDefaultModelObject() != null) {
							b.append(component.getDefaultModelObject());
						}
						return b;
					}
				}));
		add(roundTitle);
	}

	private void addSessionTitle() {
		add(new Label("sessionTitle", new StringResourceModel("planningSessionTitle", this, getModel())));
	}

	private void addHeartBeat() {
		// Redirect to terminated page if the session gets terminated
		updatingBehavior.add(new IUpdatingListener() {
			private static final long serialVersionUID = 1L;

			public void onHeadRendered(IHeaderResponse response) {
			}

			public void onUpdated(AjaxRequestTarget target) {
				if (getModelObject().getSessionStatus() == SessionStatus.TERMINATED) {
					getRequestCycle().setResponsePage(TerminatedPage.class);
				}
			}
		});

		// Add participant heart beat
		Participant participant = getModelObject().getParticipant();
		updatingBehavior.add(new HeartBeatUpdatingListener<Participant>(participant) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeat(AjaxRequestTarget target, Participant participant) {
				PlanningSession planningSession = getModelObject();
				ParticipantStatus participantStatus = planningSession.getParticipantStatus(participant);

				if (participantStatus == ParticipantStatus.TERMINATED || participantStatus == ParticipantStatus.UNKNOWN) {
					throw new RestartResponseAtInterceptPageException(TerminatedPage.class);
				}
			}
		});
	}

	@Override
	protected IModel<String> getPageTitle() {
		return new StringResourceModel("planningPoker", this, null);
	}

}
