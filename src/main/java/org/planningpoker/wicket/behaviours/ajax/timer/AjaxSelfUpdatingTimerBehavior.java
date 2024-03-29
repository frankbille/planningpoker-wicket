/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.planningpoker.wicket.behaviours.ajax.timer;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.time.Duration;

/**
 * AJAX behavior for doing scheduled, repeating updates.
 */
public class AjaxSelfUpdatingTimerBehavior extends AbstractAjaxTimerBehavior {
	private static final long serialVersionUID = 1L;

	private final IObjectState state;

	/**
	 * Constructor. Uses {@link SerializableObjectState}
	 * 
	 * @param updateInterval
	 */
	public AjaxSelfUpdatingTimerBehavior(Duration updateInterval) {
		this(updateInterval, new SerializableObjectState());
	}

	/**
	 * Constructor
	 * 
	 * @param updateInterval
	 * @param objectState
	 */
	public AjaxSelfUpdatingTimerBehavior(Duration updateInterval,
			IObjectState objectState) {
		super(updateInterval);

		state = objectState;
		state.checkState(null);
	}

	@Override
	protected void onBind() {
		super.onBind();

		state.checkState(getStateObject());
	}

	@Override
	protected void onTimer(AjaxRequestTarget target) {
		if (state.checkState(getStateObject())) {
			target.addComponent(getComponent());

			onPostTimerUpdated(target);
		}
	}

	@Override
	protected void onHeadRendered(IHeaderResponse response) {
		state.checkState(getComponent().getDefaultModelObject());
	}

	protected void onPostTimerUpdated(AjaxRequestTarget target) {
	}

	protected Object getStateObject() {
		return getComponent().getDefaultModelObject();
	}

}
