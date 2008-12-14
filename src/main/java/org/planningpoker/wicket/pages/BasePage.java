package org.planningpoker.wicket.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Base page for the pages. Defines the basic layout
 */
public abstract class BasePage extends WebPage {

	/**
	 * Constructor
	 */
	public BasePage() {
		add(new Label("pageTitle", getPageTitle()));
	}

	protected abstract IModel<String> getPageTitle();

}
