package com.intenso.jira.plugins.condval.issuetabpanel;

public class ConditionsValidatorResultWrapper {

	private int indentLevel;
	
	private ConditionsValidatorResultType resultType;

	private String description;

	private Boolean conditionPass;

	private String operator;
	
	private Boolean isOnlyOneCondition = false;
	
	private String errorMessage;


	public ConditionsValidatorResultWrapper(int indentLevel,ConditionsValidatorResultType resultType, String operator) {
		this.indentLevel = indentLevel;
		this.resultType = resultType;
		this.operator = operator;
	}

	/**
	 * 
	 * @param indentLevel
	 * @param resultType
	 * @param description
	 * @param conditionPass
	 * @param errorMessage Message that occurs during validation condition
	 */
	public ConditionsValidatorResultWrapper(int indentLevel, ConditionsValidatorResultType resultType, String description, Boolean conditionPass, String errorMessage) {
		this.indentLevel = indentLevel;
		this.resultType = resultType;
		this.description = description;
		this.conditionPass = conditionPass;
		this.errorMessage = errorMessage;
	}

	public int getIndentLevel() {
		return indentLevel;
	}

	public ConditionsValidatorResultType getResultType() {
		return resultType;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getConditionPass() {
		return conditionPass;
	}

	public String getOperator() {
		return operator;
	}

	public Boolean getIsOnlyOneCondition() {
		return isOnlyOneCondition;
	}

	public void setIsOnlyOneCondition(Boolean isOnlyOneCondition) {
		this.isOnlyOneCondition = isOnlyOneCondition;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}