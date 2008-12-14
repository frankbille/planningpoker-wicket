package org.planningpoker.wicket.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * Model for formatting dates using {@link SimpleDateFormat}.
 */
public class DateFormatModel extends AbstractReadOnlyModel<String> {
	private static final long serialVersionUID = 1L;

	private final IModel<Date> dateModel;
	private final SimpleDateFormat dateFormat;

	/**
	 * Constructor
	 * 
	 * @param dateModel
	 * @param dateFormat
	 */
	public DateFormatModel(IModel<Date> dateModel, String dateFormat) {
		this.dateModel = dateModel;
		this.dateFormat = new SimpleDateFormat(dateFormat);
	}

	@Override
	public String getObject() {
		return dateFormat.format(dateModel.getObject());
	}

}
