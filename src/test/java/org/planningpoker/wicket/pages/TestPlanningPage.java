package org.planningpoker.wicket.pages;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.util.tester.ITestPageSource;
import org.junit.Test;
import org.planningpoker.wicket.PlanningSession;
import org.planningpoker.wicket.WicketTestCase;

/**
 * {@link PlanningPage} tests
 */
public class TestPlanningPage extends WicketTestCase {

	/**
	 * Test that the page renders also when different situations occur
	 */
	@Test
	public void testRoundtrip() {
		final PlanningSession planningSession = createPlanningSession();
		final Session ownerSession = getOwner(planningSession).getSession();

		sessionFactory = new SessionFactory() {
			public Session newSession(Request request, Response response) {
				return ownerSession;
			}
		};

		tester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public PlanningPage getTestPage() {
				return new PlanningPage(planningSession);
			}
		});

		tester.assertRenderedPage(PlanningPage.class);

		// Test removing a participant
		assertListViewSize("planningTable:participants", 5);
		tester.clickLink("planningTable:participants:3:removeLink");
		assertListViewSize("planningTable:participants", 4);

		// Test starting the session
		tester.clickLink("administrationPanel:startSessionLink");
		tester.assertRenderedPage(PlanningPage.class);

		// Select a card
		tester.clickLink("deckPanel:cards:3:cardLink");
		tester.assertRenderedPage(PlanningPage.class);

		// Remove another participant, now that we are playing
		assertListViewSize("planningTable:participants", 4);
		tester.clickLink("planningTable:participants:3:removeLink");
		assertListViewSize("planningTable:participants", 3);

		// Terminate the session
		tester.clickLink("administrationPanel:terminateSessionLink");
		tester.assertRenderedPage(TerminatedPage.class);
	}
}
