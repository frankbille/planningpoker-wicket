package org.planningpoker.wicket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.planningpoker.domain.IDeck;
import org.planningpoker.wicket.PlanningSession.SessionStatus;
import org.planningpoker.wicket.pages.EnterNamePage;
import org.planningpoker.wicket.pages.FrontPage;
import org.planningpoker.wicket.pages.PlanningPage;
import org.planningpoker.wicket.pages.TerminatedPage;

/**
 * Wicket application
 */
public class PlanningPokerApplication extends WebApplication {

	public static PlanningPokerApplication get() {
		return (PlanningPokerApplication) WebApplication.get();
	}

	private final List<PlanningSession> planningSessions = new CopyOnWriteArrayList<PlanningSession>();

	@Override
	protected void init() {
		mount(new HybridUrlCodingStrategy("/planning", PlanningPage.class, false));
		mount(new HybridUrlCodingStrategy("/entername", EnterNamePage.class, false));
		mountBookmarkablePage("/terminated", TerminatedPage.class);
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return FrontPage.class;
	}

	/**
	 * Create a new planing poker session. This is a convenience method, which
	 * takes the current Wicket {@link Session} and uses it for the owner of the
	 * planning session.
	 * 
	 * @param title
	 *            The title of the planning session.
	 * @param ownerName
	 *            The name of the owner of the planning session.
	 * @param deck
	 *            The deck to use in this session for all participants
	 * @return The new planning session.
	 */
	public PlanningSession createNewPlanningSession(String title, String ownerName, IDeck deck) {
		return createNewPlanningSession(title, ownerName, deck, Session.get());
	}

	/**
	 * Create a new planing poker session.
	 * 
	 * @param title
	 *            The title of the planning session.
	 * @param ownerName
	 *            The name of the owner of the planning session.
	 * @param deck
	 *            The deck to use in this session for all participants
	 * @param ownerSession
	 *            The Wicket {@link Session} of the owner.
	 * @return The new planning session.
	 */
	public PlanningSession createNewPlanningSession(String title, String ownerName, IDeck deck, Session ownerSession) {
		PlanningSession planningSession = new PlanningSession(title, ownerName, deck, ownerSession);
		planningSessions.add(planningSession);
		return planningSession;
	}

	/**
	 * Get all the available planning sessions. A planning session is available
	 * if it's status is {@link SessionStatus#SETTING_UP}.
	 * 
	 * @return All the available planning sessions.
	 */
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
