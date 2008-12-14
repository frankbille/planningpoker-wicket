package org.planningpoker.wicket.pages;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Page which is shown when a session has been terminated. There are many
 * reasons why a session can be terminated.
 */
public class TerminatedPage extends BasePage {

	/**
	 * Constructor.
	 */
	public TerminatedPage() {
		add(new BookmarkablePageLink<Void>("homepageLink", Application.get().getHomePage()));
	}

	@Override
	protected IModel<String> getPageTitle() {
		return new StringResourceModel("sessionTerminated", this, null);
	}

}
