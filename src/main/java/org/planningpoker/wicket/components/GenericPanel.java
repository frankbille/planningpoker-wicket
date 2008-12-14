package org.planningpoker.wicket.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Generic panel
 * 
 * @param <T>
 *            Model object
 */
public class GenericPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public GenericPanel(String id) {
		super(id);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param model
	 */
	public GenericPanel(String id, IModel<T> model) {
		super(id, model);
	}

	/**
	 * @return The generic model. This is the same way it worked in 1.3
	 */
	@SuppressWarnings("unchecked")
	public IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	/**
	 * @return The model object. This is the same way it worked in 1.3
	 */
	@SuppressWarnings("unchecked")
	public T getModelObject() {
		return (T) getDefaultModelObject();
	}

}
