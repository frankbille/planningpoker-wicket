package org.planningpoker.wicket.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.PlanningSession;
import org.planningpoker.wicket.behaviours.ClickConfirmBehavior;
import org.planningpoker.wicket.pages.TerminatedPage;

public class AdministrationPanel extends Panel<PlanningSession> {
	private static final long serialVersionUID = 1L;

	public AdministrationPanel(String id, IModel<PlanningSession> model) {
		super(id, model);

		setOutputMarkupPlaceholderTag(true);

		add(new AjaxLink<PlanningSession>("startSessionLink", model) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				PlanningSession planningSession = getModelObject();
				planningSession.createNewRound();

				target.addComponent(AdministrationPanel.this);

				onNewRoundCreated(target);
			}

			@Override
			public boolean isVisible() {
				return getModelObject().isStarted() == false;
			}
		});

		add(new AjaxLink<PlanningSession>("newRoundLink", model) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				PlanningSession planningSession = getModelObject();
				planningSession.createNewRound();

				target.addComponent(AdministrationPanel.this);

				onNewRoundCreated(target);
			}

			@Override
			public boolean isVisible() {
				PlanningSession modelObject = getModelObject();
				PlanningRound currentPlanningRound = modelObject
						.getCurrentPlanningRound();
				return modelObject.isStarted()
						&& currentPlanningRound.isComplete();
			}
		});

		Link<PlanningSession> terminateLink = new Link<PlanningSession>(
				"terminateSessionLink", model) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				getModelObject().terminate();
				getRequestCycle().setResponsePage(TerminatedPage.class);
			}
		};
		terminateLink.add(new ClickConfirmBehavior(new StringResourceModel(
				"confirmTerminateSession", this, null)));
		add(terminateLink);
	}

	@Override
	public boolean isVisible() {
		return getModelObject().isOwner();
	}

	protected void onNewRoundCreated(AjaxRequestTarget target) {
	}

}
