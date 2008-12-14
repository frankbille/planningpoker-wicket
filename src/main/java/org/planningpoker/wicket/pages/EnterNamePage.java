package org.planningpoker.wicket.pages;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.planningpoker.wicket.PlanningPokerApplication;
import org.planningpoker.wicket.PlanningSession;

/**
 * Page for entering a name of participant
 */
public class EnterNamePage extends BasePage {

	private String name;

	/**
	 * Constructor
	 * 
	 * @param planningSession
	 */
	public EnterNamePage(PlanningSession planningSession) {
		if (planningSession == null) {
			throw new RestartResponseAtInterceptPageException(
					PlanningPokerApplication.get().getHomePage());
		}

		if (planningSession.isParticipating()) {
			throw new RestartResponseAtInterceptPageException(new PlanningPage(
					planningSession));
		}

		if (planningSession.isStarted()) {
			throw new RestartResponseAtInterceptPageException(
					PlanningPokerApplication.get().getHomePage());
		}

		Form<PlanningSession> form = new Form<PlanningSession>("form",
				new Model<PlanningSession>(planningSession)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				getModelObject().addParticipant(name);

				getRequestCycle().setResponsePage(
						new PlanningPage(getModelObject()));
			}
		};
		add(form);

		TextField<String> nameField = new TextField<String>("name",
				new PropertyModel<String>(this, "name"));
		nameField.setRequired(true);
		form.add(nameField);

		form.add(new Button("continue", new StringResourceModel("continue",
				this, null)));
	}

	/**
	 * @return The name of the participant
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name of the participant
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected IModel<String> getPageTitle() {
		return new StringResourceModel("enterName", this, null);
	}

}
