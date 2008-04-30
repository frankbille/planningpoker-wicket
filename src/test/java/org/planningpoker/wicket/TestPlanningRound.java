package org.planningpoker.wicket;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.wicket.protocol.http.MockHttpServletRequest;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.planningpoker.domain.ICard;

public class TestPlanningRound {

	@Test
	public void testResult() {
		new WicketTester();

		WebSession ses1 = createSession();
		WebSession ses2 = createSession();
		WebSession ses3 = createSession();
		PlanningSession session = new PlanningSession("Test", "test", "P1",
				ses1);
		session.addParticipant("P2", ses2);
		session.addParticipant("P3", ses3);

		PlanningRound round = session.createNewRound();
		List<ICard> cards = round.getDeck().createDeck();
		ICard selectedCard1 = cards.get(1);
		ICard selectedCard2 = cards.get(2);
		round.selectCard(selectedCard2, ses1);
		round.selectCard(selectedCard1, ses2);
		round.selectCard(selectedCard1, ses3);

		PlanningRoundResult planningRoundResult = round
				.getPlanningRoundResult();
		cards = planningRoundResult.getCards();

		assertThat(cards.size(), is(2));

		ICard card1 = cards.get(0);
		assertThat(card1, is(selectedCard1));

		assertThat(planningRoundResult.getCardCount(card1), is(2));
		assertThat(planningRoundResult.getCardPercentage(card1), is((double) 2
				/ (double) 3));

		ICard card2 = cards.get(1);
		assertThat(card2, is(selectedCard2));

		assertThat(planningRoundResult.getCardCount(card2), is(1));
		assertThat(planningRoundResult.getCardPercentage(card2), is((double) 1
				/ (double) 3));
	}

	private WebSession createSession() {
		return new WebSession(new ServletWebRequest(new MockHttpServletRequest(
				null, null, null)));
	}

}
