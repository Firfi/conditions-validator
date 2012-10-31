package com.intenso.jira.plugins.condval.issuetabpanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.LocaleManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.workflow.WorkflowException;
import com.atlassian.jira.workflow.WorkflowManager;
import com.intenso.jira.plugins.condval.component.ConditionsValidatorAllowedGroups;
import com.opensymphony.workflow.loader.ActionDescriptor;

public class ConditionsValidatorIssueTabPanel extends AbstractIssueTabPanel {

	protected IssueTabPanelModuleDescriptor descriptor;

	private I18nBean i18nBean;

	private I18nHelper i18nHelper;

	private ConditionsValidatorAllowedGroups conditionsValidatorAllowedGroups;

	public ConditionsValidatorIssueTabPanel(
			ConditionsValidatorAllowedGroups conditionsValidatorAllowedGroups,
			JiraAuthenticationContext authenticationContext, LocaleManager localeManager) {
		this.conditionsValidatorAllowedGroups = conditionsValidatorAllowedGroups;
		i18nBean = new I18nBean(localeManager.getLocale(authenticationContext.getLocale().getLanguage()));
	}

	public void init(IssueTabPanelModuleDescriptor descriptor) {
		this.descriptor = descriptor;
		i18nHelper = ComponentManager.getInstance().getJiraAuthenticationContext().getI18nHelper();
	}

	public List getActions(Issue issue, User user) {
		List<IssueAction> actionList = new ArrayList<IssueAction>();

		WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager();
		try {
			List<?> actions = workflowManager.getWorkflow(issue).getLinkedStep(
				issue.getStatusObject().getGenericValue()).getActions();
			for (Object object : actions) {
				ActionDescriptor actionDescriptor = (ActionDescriptor) object;
				actionList.add(new ConditionsValidatorAction(issue, user, descriptor, actionDescriptor,
						workflowManager));
			}

			List<?> globalActions = workflowManager.getWorkflow(issue).getDescriptor().getGlobalActions();
			for (Object object : globalActions) {
				ActionDescriptor actionDescriptor = (ActionDescriptor) object;
				actionList.add(new ConditionsValidatorAction(issue, user, descriptor, actionDescriptor,
						workflowManager));
			}
		} catch (WorkflowException e) {
			e.printStackTrace();
		}

		if (actionList.isEmpty()) {
			GenericMessageAction genericMessageAction = new GenericMessageAction(
					i18nHelper.getText("conditions-validator.noConditions"));
			actionList.add(genericMessageAction);
		}

		return actionList;
	}

	public boolean showPanel(Issue issue, User user) {
		return validateUserGroupPermissions(user);
	}

	private boolean validateUserGroupPermissions(User user) {
		Collection<String> userGroups = ComponentAccessor.getGroupManager().getGroupNamesForUser(user);
		List<String> allowedGroups = conditionsValidatorAllowedGroups.load();
		if (allowedGroups == null || allowedGroups.size() == 0) {
			return true;
		} else {
			for (String allowedGroup : allowedGroups) {
				for (String userGroup : userGroups) {
					if (userGroup.equals(allowedGroup))
						return true;
				}
			}
			return false;
		}
	}
}