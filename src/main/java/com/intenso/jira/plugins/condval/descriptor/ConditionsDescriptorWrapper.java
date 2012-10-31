package com.intenso.jira.plugins.condval.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.workflow.loader.ConditionsDescriptor;

public class ConditionsDescriptorWrapper implements IConditionDescriptorWrapper {

	private ConditionsDescriptor source;
	private List<IConditionDescriptorWrapper> conditions;
	
	public ConditionsDescriptorWrapper(ConditionsDescriptor source) {
		this.source = source;
	}

	public ConditionsDescriptor getSource() {
		return source;
	}

	@Override
	public boolean isConditionPassed() {
		boolean isAnd = "AND".equalsIgnoreCase(source.getType());
		
		if(conditions != null) {
			for(IConditionDescriptorWrapper c : conditions) {
				if(isAnd && !c.isConditionPassed()) {
					return false;
				}else if(!isAnd && c.isConditionPassed()) {
					return true;
				}
			}
		}
		return isAnd;
	}

	@Override
	public void addCondition(IConditionDescriptorWrapper condition) {
		if(conditions == null) {
			conditions = new ArrayList<IConditionDescriptorWrapper>();
		}
		conditions.add(condition);
	}

}
