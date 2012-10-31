package com.intenso.jira.plugins.condval.descriptor;

public interface IConditionDescriptorWrapper {

	boolean isConditionPassed();
	Object getSource();
	void addCondition(IConditionDescriptorWrapper condition);
}
