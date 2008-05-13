package org.planningpoker.wicket.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class BasePage<T> extends WebPage<T> {

	public BasePage() {
		add(new Label<String>("pageTitle", getPageTitle()));
	}

	protected abstract IModel<String> getPageTitle();

}
