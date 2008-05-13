package org.planningpoker.wicket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.planningpoker.wicket.PlanningSession.SessionStatus;
import org.planningpoker.wicket.pages.EnterNamePage;
import org.planningpoker.wicket.pages.FrontPage;
import org.planningpoker.wicket.pages.PlanningPage;
import org.planningpoker.wicket.pages.TerminatedPage;

public class PlanningPokerApplication extends WebApplication {

	public static PlanningPokerApplication get() {
		return (PlanningPokerApplication) WebApplication.get();
	}

	private final List<PlanningSession> planningSessions = new CopyOnWriteArrayList<PlanningSession>();

	@Override
	protected void init() {
		mount(new HybridUrlCodingStrategy("/planning", PlanningPage.class,
				false));
		mount(new HybridUrlCodingStrategy("/entername", EnterNamePage.class,
				false));
		mountBookmarkablePage("/terminated", TerminatedPage.class);
	}

	@Override
	public Class<? extends Page<?>> getHomePage() {
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
		PlanningSession planningSession = new PlanningSession(title, password,
				ownerName, ownerSession);
		planningSessions.add(planningSession);
		return planningSession;
	}

	public List<PlanningSession> getAvailablePlanningSessions() {
		List<PlanningSession> filteredPlanningSessions = new ArrayList<PlanningSession>();

		for (PlanningSession planningSession : planningSessions) {
			if (planningSession.getSessionStatus() == SessionStatus.SETTING_UP) {
				filteredPlanningSessions.add(planningSession);
			}
		}

		return filteredPlanningSessions;
	}

}
