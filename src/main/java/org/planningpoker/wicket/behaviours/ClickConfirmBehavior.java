package org.planningpoker.wicket.behaviours;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.JavascriptUtils;
import org.apache.wicket.util.string.Strings;

/**
 * Displays a java script confirmation dialog, which the user has to confirm
 * before the link is executed.
 */
public class ClickConfirmBehavior extends AbstractBehavior {
	private static final long serialVersionUID = 1L;

	private final IModel<?> textModel;

	/**
	 * Construct.
	 * 
	 * @param textModel
	 *            The model containing the text to display to the user,
	 *            describing why they need to confirm the clicking on the link.
	 */
	public ClickConfirmBehavior(IModel<?> textModel) {
		this.textModel = textModel;
	}

	@Override
	public void onComponentTag(Component<?> component, ComponentTag tag) {
		if (tag.isClose() == false) {
			StringBuilder b = new StringBuilder();

			CharSequence existingOnClickAttribute = tag.getString("onclick");
			CharSequence escapedModelObject = getEscapedModelObject();

			if (Strings.isEmpty(existingOnClickAttribute)) {
				b.append("return confirm('");
				b.append(escapedModelObject);
				b.append("');");
			} else {
				b.append("if (confirm('");
				b.append(escapedModelObject);
				b.append("')) {");
				b.append(existingOnClickAttribute);
				b.append("} else return false;");
			}

			tag.put("onclick", b);
		}
	}

	protected CharSequence getEscapedModelObject() {
		Object modelObject = textModel.getObject();
		String modelObjectString = Strings.toString(modelObject);
		CharSequence escapedModelString = JavascriptUtils.escapeQuotes(modelObjectString);
		return escapedModelString;
	}

}
