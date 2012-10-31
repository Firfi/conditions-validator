package com.intenso.jira.plugins.condval.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.config.properties.PropertiesManager;

public class ConditionsValidatorAllowedGroupsImpl implements ConditionsValidatorAllowedGroups {

	public static final String CONDITIONS_VALIDATOR_CONFIGURATION = "ConditionsValidatorConfiguration";

	private static Object lock = new Object();

	private static List<String> groups;

	public List<String> load() {
		synchronized (lock) {
			if (groups == null) {
				String value = ComponentManager.getComponent(PropertiesManager.class).getPropertySet().getText(
					CONDITIONS_VALIDATOR_CONFIGURATION);
				if (value != null) {
					groups = new ArrayList<String>(Arrays.asList(value.split(";")));
				} else {
					groups = new ArrayList<String>();
				}
			}
			return groups;
		}
	}

	public void store(List<String> groups) {
		synchronized (lock) {
			StringBuilder sb = new StringBuilder();
			for (String element : groups) {
				if (sb.length() > 0) {
					sb.append(";");
				}
				sb.append(element);
			}
			ComponentManager.getComponent(PropertiesManager.class).getPropertySet().setText(
				CONDITIONS_VALIDATOR_CONFIGURATION, sb.toString());
			ConditionsValidatorAllowedGroupsImpl.groups = groups;
		}
	}
}