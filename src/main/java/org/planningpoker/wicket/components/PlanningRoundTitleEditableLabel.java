package org.planningpoker.wicket.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.planningpoker.wicket.PlanningRound;
import org.planningpoker.wicket.PlanningSession;

/**
 * Be able to change the title of a round, by clicking on the label.
 */
public class PlanningRoundTitleEditableLabel extends AjaxEditableLabel<String> {
	private static final long serialVersionUID = 1L;

	private final IModel<PlanningRound> planningRoundModel;

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param planningRoundModel
	 */
	public PlanningRoundTitleEditableLabel(String id, final IModel<PlanningRound> planningRoundModel) {
		super(id, new PropertyModel<String>(planningRoundModel, "title"));
		this.planningRoundModel = planningRoundModel;

		add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				String roundTitle = PlanningRoundTitleEditableLabel.this.getModelObject();

				StringBuilder cssClass = new StringBuilder();

				if (planningRoundModel.getObject().getPlanningSession().isOwner()) {
					cssClass.append("editableLabel");
				}

				if (roundTitle == null) {
					if (cssClass.length() > 0) {
						cssClass.append(" ");
					}

					if (planningRoundModel.getObject().getPlanningSession().isOwner()) {
						cssClass.append("editRoundTitle");
					} else {
						cssClass.append("noRoundTitle");
					}
				}

				return cssClass.toString();
			}
		}));
	}

	@Override
	public boolean isEnabled() {
		boolean enabled = false;
		if (planningRoundModel.getObject() != null) {
			PlanningSession planningSession = planningRoundModel.getObject().getPlanningSession();
			enabled = planningSession.isOwner();
		}
		return enabled;
	}

	/**
	 * @return The model object.
	 */
	public String getModelObject() {
		return (String) getDefaultModelObject();
	}

	@Override
	public boolean isVisible() {
		return planningRoundModel.getObject() != null;
	}

	@Override
	protected String defaultNullLabel() {
		return new StringResourceModel("noRoundTitle", this, null).getObject();
	}
}
