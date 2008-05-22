package org.planningpoker.wicket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.MockHttpServletRequest;
import org.apache.wicket.protocol.http.MockHttpServletResponse;
import org.apache.wicket.protocol.http.MockHttpSession;
import org.apache.wicket.protocol.http.MockServletContext;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

/**
 * Abstract test case providing a wicket tester
 */
public abstract class WicketTestCase {

	public static interface SessionFactory {
		Session newSession(Request request, Response response);
	}

	protected WicketTester tester;

	protected SessionFactory sessionFactory;

	/**
	 * Create new wicket tester for each test
	 */
	@Before
	public void setup() {
		PlanningPokerApplication application = new PlanningPokerApplication() {
			@Override
			public Session newSession(Request request, Response response) {
				if (sessionFactory != null) {
					return sessionFactory.newSession(request, response);
				} else {
					return super.newSession(request, response);
				}
			}
		};
		tester = new WicketTester(application);
	}

	protected void assertListViewSize(String componentPath, int size) {
		Component<?> component = tester.getComponentFromLastRenderedPage(componentPath);
		assertTrue(component instanceof ListView);
		ListView<?> listView = (ListView<?>) component;
		assertEquals(size, listView.size());
	}

	protected PlanningSession createStartedPlanningSession() {
		PlanningSession planningSession = createPlanningSession();

		planningSession.createNewRound(getOwner(planningSession));

		return planningSession;
	}

	protected Participant getOwner(PlanningSession planningSession) {
		Participant owner = null;

		for (Participant participant : planningSession.getParticipants()) {
			if (planningSession.isOwner(participant)) {
				owner = participant;
				break;
			}
		}

		return owner;
	}

	protected PlanningSession createPlanningSession() {
		PlanningSession planningSession = new PlanningSession("Started session", "Owner",
				createNewSession());

		planningSession.addParticipant("Participant 1", createNewSession());
		planningSession.addParticipant("Participant 2", createNewSession());
		planningSession.addParticipant("Participant 3", createNewSession());
		planningSession.addParticipant("Participant 4", createNewSession());
		return planningSession;
	}

	protected Session createNewSession() {
		WebApplication application = tester.getApplication();
		MockServletContext servletContext = new MockServletContext(application, "/");
		MockHttpSession httpSession = new MockHttpSession(servletContext);
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(application,
				httpSession, servletContext);
		ServletWebRequest request = new ServletWebRequest(httpServletRequest);

		MockHttpServletResponse httpServletResponse = new MockHttpServletResponse(
				httpServletRequest);
		WebResponse response = new WebResponse(httpServletResponse);

		return application.newSession(request, response);
	}
}
