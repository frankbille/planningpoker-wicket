package org.planningpoker.wicket.pages;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

public class TerminatedPage extends BasePage<Void> {

	public TerminatedPage() {
		add(new BookmarkablePageLink("homepageLink", Application.get()
				.getHomePage()));
	}

	@Override
	protected IModel<String> getPageTitle() {
		return new StringResourceModel("sessionTerminated", this, null);
	}

}
