package it.com.intenso.jira.plugins;

import org.junit.Test;

import com.atlassian.jira.functest.framework.FuncTestCase;

public class ConditionsValidatorTest extends FuncTestCase {

	@Override
	protected void setUpTest() {
		super.setUpTest();
		administration.restoreData("ConditionsValidatorTest.xml");
	}

	@Test
	public void testConditionsValidator() {
		navigation.issue().viewIssue("TEST-1");
		navigation.gotoPage("/browse/TEST-1?page=com.intenso.jira.plugins.condval.intenso-conditions-validator:conditionsValidator-issueTabPanel#issue-tabs");
		text.assertTextPresent("of the issue can execute this transition.");		
	}

}
