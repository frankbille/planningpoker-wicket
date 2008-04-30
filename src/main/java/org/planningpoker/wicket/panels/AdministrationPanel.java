package org.planningpoker.wicket.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.PlanningSession;

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
	}

	@Override
	public boolean isVisible() {
		return getModelObject().isOwner();
	}

	protected void onNewRoundCreated(AjaxRequestTarget target) {
	}

}
