package com.intenso.jira.plugins.condval.action;

import java.util.List;

import com.atlassian.jira.config.LocaleManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.web.bean.I18nBean;
import com.intenso.jira.plugins.condval.component.ConditionsValidatorAllowedGroups;

public class Configuration extends JiraWebActionSupport {

	private static final long serialVersionUID = 3095515965674276737L;

	private String group;

	private ConditionsValidatorAllowedGroups conditionsValidatorAllowedGroups;

	private I18nBean i18nBean;

	public Configuration(ConditionsValidatorAllowedGroups conditionsValidatorAllowedGroups, JiraAuthenticationContext authenticationContext, LocaleManager localeManager) {
		this.conditionsValidatorAllowedGroups = conditionsValidatorAllowedGroups;
		i18nBean = new I18nBean(localeManager.getLocale(authenticationContext.getLocale().getLanguage()));
	}

	public String doAddGroup() throws Exception {
		List<String> groups = conditionsValidatorAllowedGroups.load();
		if (!groups.contains(group) && group.isEmpty() == false) {
			groups.add(group);
			conditionsValidatorAllowedGroups.store(groups);
		}
		return SUCCESS;
	}

	public String doRemoveGroup() throws Exception {
		List<String> groups = conditionsValidatorAllowedGroups.load();
		if (groups.contains(group)) {
			groups.remove(group);
			conditionsValidatorAllowedGroups.store(groups);
		}
		return SUCCESS;
	}

	public List<String> getGroups() {
		return conditionsValidatorAllowedGroups.load();
	}

	public void setGroup(String group) {
		this.group = group;
	}
}