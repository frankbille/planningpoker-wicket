package org.planningpoker.wicket.pages;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.time.Duration;
import org.planningpoker.wicket.PlanningPokerApplication;
import org.planningpoker.wicket.PlanningSession;
import org.planningpoker.wicket.behaviours.AjaxSelfUpdatingTimerBehavior;

public class FrontPage extends BasePage {

	private static class NewSession implements Serializable {
		private static final long serialVersionUID = 1L;

		private String title;
		private String password;
		private String name;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public FrontPage() {
		IModel<List<PlanningSession>> planningSessionsModel = new LoadableDetachableModel<List<PlanningSession>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PlanningSession> load() {
				return PlanningPokerApplication.get().getPlanningSessions();
			}
		};

		WebMarkupContainer<List<PlanningSession>> planningSessionsContainer = new WebMarkupContainer<List<PlanningSession>>(
				"planningSessionsContainer", planningSessionsModel);
		add(planningSessionsContainer);
		planningSessionsContainer.add(new AjaxSelfUpdatingTimerBehavior(
				Duration.ONE_SECOND));
		planningSessionsContainer.add(new ListView<PlanningSession>(
				"planningSessions", planningSessionsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PlanningSession> item) {
				item.add(new Label<String>("title", new PropertyModel<String>(
						item.getModel(), "title")));

				item.add(new Label<Integer>("participants",
						new PropertyModel<Integer>(item.getModel(),
								"participantCount")));

				Link<PlanningSession> joinLink = new Link<PlanningSession>(
						"joinLink", item.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						getRequestCycle().setResponsePage(
								new PlanningPage(getModelObject()));
					}

					@Override
					public boolean isEnabled() {
						return getModelObject().isStarted() == false;
					}
				};
				item.add(joinLink);
			}
		});

		// Form
		final NewSession newSession = new NewSession();

		Form<NewSession> form = new Form<NewSession>("form",
				new CompoundPropertyModel<NewSession>(newSession)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				PlanningPokerApplication app = PlanningPokerApplication.get();
				PlanningSession planningSession = app.createNewPlanningSession(
						newSession.getTitle(), newSession.getPassword(),
						newSession.getName());

				getRequestCycle().setResponsePage(
						new PlanningPage(planningSession));
			}
		};
		add(form);

		form.add(new TextField<String>("title").setRequired(true));

		// form.add(new PasswordTextField("password"));

		form.add(new TextField<String>("name").setRequired(true));

		form.add(new Button<String>("createSession", new StringResourceModel(
				"createSession", this, null)));
	}

	@Override
	protected IModel<String> getPageTitle() {
		return null;
	}

}
