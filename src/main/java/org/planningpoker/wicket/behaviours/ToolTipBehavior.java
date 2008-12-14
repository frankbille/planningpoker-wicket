package org.planningpoker.wicket.behaviours;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 * Adds a tool tip to a component. Right now the "title" attribute is used.
 */
public class ToolTipBehavior extends AbstractBehavior {
	private static final long serialVersionUID = 1L;

	private final IModel<?> textModel;

	/**
	 * Construct.
	 * 
	 * @param textModel
	 *            The tool tip to show for the component.
	 */
	public ToolTipBehavior(IModel<?> textModel) {
		this.textModel = textModel;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		if (tag.isClose() == false) {
			CharSequence escapedModelObject = getEscapedModelObject();

			tag.put("title", escapedModelObject);
		}
	}

	protected CharSequence getEscapedModelObject() {
		Object modelObject = textModel.getObject();

		String modelObjectString = Strings.toString(modelObject);
		CharSequence escapedModelString = Strings
				.escapeMarkup(modelObjectString);
		return escapedModelString;
	}

}
