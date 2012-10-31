package com.intenso.jira.plugins.condval.descriptor;

import com.opensymphony.workflow.loader.ConditionDescriptor;

public class ConditionDescriptorWrapper implements IConditionDescriptorWrapper{

	private ConditionDescriptor source;
	private boolean conditionPassed;
	
	public ConditionDescriptorWrapper(ConditionDescriptor source, boolean passed) {
		this.source = source;
		this.conditionPassed = passed;
	}

	@Override
	public ConditionDescriptor getSource() {
		return source;
	}

	public boolean isConditionPassed() {
		return conditionPassed;
	}

	@Override
	public void addCondition(IConditionDescriptorWrapper condition) {
		throw new UnsupportedOperationException("ConditionDescriptorWrapper is bottom leaf of conditions-descriptors hierarchy.");
	}
}
