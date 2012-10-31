package com.intenso.jira.plugins.condval.issuetabpanel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.jira.web.bean.WorkflowConditionFormatBean;
import com.atlassian.jira.workflow.WorkflowFunctionUtils;
import com.atlassian.jira.workflow.WorkflowManager;
import com.intenso.jira.plugins.condval.descriptor.ConditionDescriptorWrapper;
import com.intenso.jira.plugins.condval.descriptor.ConditionsDescriptorWrapper;
import com.intenso.jira.plugins.condval.descriptor.IConditionDescriptorWrapper;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.TypeResolver;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflowContext;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ConditionsDescriptor;
import com.opensymphony.workflow.spi.WorkflowEntry;

public class ConditionsValidatorAction extends AbstractIssueAction {

	protected final IssueTabPanelModuleDescriptor descriptor;

	protected Timestamp timePerformed;

	protected ActionDescriptor actionDescriptor;

	protected User user;

	protected Issue issue;

	private WorkflowManager workflowManager;

	public ConditionsValidatorAction(Issue issue, User user, IssueTabPanelModuleDescriptor descriptor,
			ActionDescriptor actionDescriptor, WorkflowManager workflowManager) {
		super(descriptor);
		this.descriptor = descriptor;
		this.timePerformed = new Timestamp(Calendar.getInstance().getTimeInMillis());
		this.actionDescriptor = actionDescriptor;
		this.user = user;
		this.issue = issue;
		this.workflowManager = workflowManager;
	}

	@Override
	protected void populateVelocityParams(Map params) {
		ArrayList<ConditionsValidatorResultWrapper> resultList = new ArrayList<ConditionsValidatorResultWrapper>();
		WorkflowConditionFormatBean bean = new WorkflowConditionFormatBean();
		Boolean isActionAvailable = true;
		bean.setPluginType("workflow-condition");
		if (actionDescriptor.getRestriction() != null) {
			IConditionDescriptorWrapper conditionsDescriptorRoot =  new  ConditionsDescriptorWrapper(actionDescriptor.getRestriction().getConditionsDescriptor());
			resultList = getConditionResults(resultList, conditionsDescriptorRoot, bean, 1);
			if (resultList.size() == 1)
				resultList.get(0).setIsOnlyOneCondition(true);
			isActionAvailable = conditionsDescriptorRoot.isConditionPassed();
		}
		params.put("actionAvailable", isActionAvailable);
		params.put("actionDescriptor", this.actionDescriptor.getName());
		params.put("resultList", resultList);
	}

	private ArrayList<ConditionsValidatorResultWrapper> getConditionResults(
			ArrayList<ConditionsValidatorResultWrapper> resultList, IConditionDescriptorWrapper descriptor,
			WorkflowConditionFormatBean bean, int displayLevel) {
		int level = displayLevel * 1;
		List<?> conditions = ((ConditionsDescriptor)descriptor.getSource()).getConditions();
		for (Object desc : conditions) {
			if (desc instanceof ConditionDescriptor) {
				Boolean conditionPass = addStatusDescriptionAndConditionPass(resultList, (ConditionDescriptor) desc, level, bean);
				descriptor.addCondition(new ConditionDescriptorWrapper((ConditionDescriptor)desc, conditionPass));
				addOperatorKeyBetweenConditions(conditions, resultList, desc, level, (ConditionsDescriptor)descriptor.getSource());
			} else if (desc instanceof ConditionsDescriptor) {
				addGroupedConditionsOperatorKey(conditions, resultList, desc, level, (ConditionsDescriptor)descriptor.getSource());
				IConditionDescriptorWrapper descriptorWrapper = new ConditionsDescriptorWrapper((ConditionsDescriptor)desc);
				descriptor.addCondition(descriptorWrapper);
				getConditionResults(resultList, descriptorWrapper, bean, displayLevel + 1);
				addOperatorKeyBetweenConditions(conditions, resultList, desc, level, (ConditionsDescriptor)descriptor.getSource());
			}
		}
		return resultList;
	}

	protected Map<Boolean, String> checkIfConditionPasses(ConditionDescriptor desc) {
		try {
			String message = null;
			Condition condition = TypeResolver.getResolver().getCondition(desc.getType(), desc.getArgs());
			Map<String, Object> transientVars = new HashMap<String, Object>();
			populateTransientMap(transientVars);

			boolean passed = false;
			try {
				passed = condition.passesCondition(transientVars, desc.getArgs(), null);
			} catch (Exception iae) {
				message = iae.getMessage();
			}

			if (desc.isNegate()) {
				passed = !passed;
			}

			return MapBuilder.newBuilder(passed, message).toMap();

		} catch (WorkflowException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void populateTransientMap(Map<String, Object> transientVars) throws StoreException {
		WorkflowContext context = new BasicWorkflowContext(user.getName());

		Long workflowEntryId = issue.getWorkflowId();
		WorkflowEntry entry = workflowManager.getStore().findEntry(workflowEntryId);

		transientVars.put("context", context);
		transientVars.put("entry", entry);
		transientVars.put("store", workflowManager.getStore());
		transientVars.put("configuration", DefaultConfiguration.INSTANCE);
		transientVars.put("descriptor", workflowManager.getWorkflow(issue).getDescriptor());
		transientVars.put("actionId", actionDescriptor.getId());
		transientVars.put("currentSteps",
			new ArrayList<Object>(workflowManager.getStore().findCurrentSteps(entry.getId())));

		/** FIX https://studio.plugins.atlassian.com/browse/CONDVAL-10 **/
		try {
			GenericValue originalIssueGV = com.atlassian.jira.component.ComponentAccessor.getIssueManager().getIssueObject(
				issue.getId()).getGenericValue();
			MutableIssue originalIssue = com.atlassian.jira.component.ComponentAccessor.getIssueFactory().getIssue(
				originalIssueGV);
			transientVars.put(WorkflowFunctionUtils.ORIGINAL_ISSUE_KEY, originalIssue);
		} catch (Throwable e) {
			// ComponentAccessor - class is only available above version 4.3
			e.printStackTrace();
			//
		}

		transientVars.put("issue", issue);
	}

	protected Boolean addStatusDescriptionAndConditionPass(ArrayList<ConditionsValidatorResultWrapper> resultList,
			ConditionDescriptor desc, int level, WorkflowConditionFormatBean bean) {
		Map<Boolean, String> resultWithMessage = checkIfConditionPasses(desc);
		Boolean result = resultWithMessage.keySet().iterator().next();
		resultList.add(new ConditionsValidatorResultWrapper(level,
				ConditionsValidatorResultType.STATUS_DESCRIPTION_AND_CONDITION_PASS, bean.formatDescriptor(desc)
						.getDescription(), result, resultWithMessage.values().iterator().next()));
		return result;
	}

	protected void addOperatorKeyBetweenConditions(List<?> conditions,
			ArrayList<ConditionsValidatorResultWrapper> resultList, Object desc, int level,
			ConditionsDescriptor descriptor) {
		if (conditions.size() > 1 && conditions.indexOf(desc) != conditions.size() - 1) {
			resultList.add(new ConditionsValidatorResultWrapper(level,
					ConditionsValidatorResultType.OPERATOR, descriptor.getType()));
		}
	}

	protected void addGroupedConditionsOperatorKey(List<?> conditions,
			ArrayList<ConditionsValidatorResultWrapper> resultList, Object desc, int level,
			ConditionsDescriptor descriptor) {
		if (!resultList.isEmpty()
				&& !resultList.get(resultList.size() - 1).getResultType().equals(
					ConditionsValidatorResultType.OPERATOR)) {
			resultList.add(new ConditionsValidatorResultWrapper(level,
					ConditionsValidatorResultType.OPERATOR, descriptor.getType()));
		}
	}

	@Override
	public Date getTimePerformed() {
		return this.timePerformed;
	}

	@Override
	public boolean isDisplayActionAllTab() {
		return false;
	}
}