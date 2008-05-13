package org.planningpoker.wicket.pages;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.planningpoker.wicket.PlanningPokerApplication;
import org.planningpoker.wicket.PlanningSession;

public class EnterNamePage extends BasePage {

	private String name;

	public EnterNamePage(final PlanningSession planningSession) {
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

		Form<Object> form = new Form<Object>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				planningSession.addParticipant(name);

				getRequestCycle().setResponsePage(
						new PlanningPage(planningSession));
			}
		};
		add(form);

		TextField<String> nameField = new TextField<String>("name",
				new PropertyModel<String>(this, "name"));
		nameField.setRequired(true);
		form.add(nameField);

		form.add(new Button<String>("continue", new StringResourceModel(
				"continue", this, null)));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected IModel<String> getPageTitle() {
		return new StringResourceModel("enterName", this, null);
	}

}
