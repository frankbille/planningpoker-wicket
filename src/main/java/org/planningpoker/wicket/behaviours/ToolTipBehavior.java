package org.planningpoker.wicket.behaviours;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

public class ToolTipBehavior extends AbstractBehavior {
	private static final long serialVersionUID = 1L;

	private final IModel<?> textModel;

	public ToolTipBehavior(IModel<?> textModel) {
		this.textModel = textModel;
	}

	@Override
	public void onComponentTag(Component<?> component, ComponentTag tag) {
		if (tag.isClose() == false) {
			CharSequence escapedModelObject = getEscapedModelObject();

			tag.put("title", escapedModelObject);
		}
	}

	protected CharSequence getEscapedModelObject() {
		Object modelObject = textModel.getObject();

		String modelObjectString = Strings.toString(modelObject);
		CharSequence escapedModelString = Strings.escapeMarkup(modelObjectString);
		return escapedModelString;
	}

}
