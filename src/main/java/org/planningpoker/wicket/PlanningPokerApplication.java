package org.planningpoker.wicket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.planningpoker.wicket.pages.EnterNamePage;
import org.planningpoker.wicket.pages.FrontPage;
import org.planningpoker.wicket.pages.PlanningPage;

public class PlanningPokerApplication extends WebApplication {

	public static PlanningPokerApplication get() {
		return (PlanningPokerApplication) WebApplication.get();
	}

	private final List<PlanningSession> planningSessions = new ArrayList<PlanningSession>();

	@Override
	protected void init() {
		mount(new HybridUrlCodingStrategy("/planning", PlanningPage.class,
				false));
		mount(new HybridUrlCodingStrategy("/entername", EnterNamePage.class,
				false));
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return FrontPage.class;
	}

	public PlanningSession createNewPlanningSession(String title,
			String ownerName) {
		return createNewPlanningSession(title, ownerName, Session.get());
	}

	public PlanningSession createNewPlanningSession(String title,
			String ownerName, Session ownerSession) {
		return createNewPlanningSession(title, null, ownerName, ownerSession);
	}

	public PlanningSession createNewPlanningSession(String title,
			String password, String ownerName) {
		return createNewPlanningSession(title, password, ownerName, Session
				.get());
	}

	public PlanningSession createNewPlanningSession(String title,
			String password, String ownerName, Session ownerSession) {
		synchronized (planningSessions) {
			PlanningSession planningSession = new PlanningSession(title,
					password, ownerName, ownerSession);
			planningSessions.add(planningSession);
			return planningSession;
		}
	}

	public List<PlanningSession> getPlanningSessions() {
		return Collections.unmodifiableList(planningSessions);
	}

}
