package com.intenso.jira.plugins.condval.component;

import java.util.List;

public interface ConditionsValidatorAllowedGroups {

	public List<String> load();

	public void store(List<String> groups);
}