package org.planningpoker.wicket.pages;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class BasePage extends WebPage implements IHeaderContributor {

	public void renderHead(IHeaderResponse response) {
		response.renderCSSReference(new ResourceReference(BasePage.class,
				"style.css"));

	}

	public BasePage() {
		add(new Label<String>("pageTitle", getPageTitle()));
	}

	protected abstract IModel<String> getPageTitle();

}
