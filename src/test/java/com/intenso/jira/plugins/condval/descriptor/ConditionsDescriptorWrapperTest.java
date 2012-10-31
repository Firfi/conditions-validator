package com.intenso.jira.plugins.condval.descriptor;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.intenso.jira.plugins.condval.descriptor.ConditionDescriptorWrapper;
import com.intenso.jira.plugins.condval.descriptor.ConditionsDescriptorWrapper;
import com.intenso.jira.plugins.condval.descriptor.IConditionDescriptorWrapper;
import com.opensymphony.workflow.loader.ConditionsDescriptor;

@RunWith(value = Parameterized.class)
public class ConditionsDescriptorWrapperTest {

	private Boolean expected;
	private IConditionDescriptorWrapper conditionsDescriptorRoot;

	public ConditionsDescriptorWrapperTest(Boolean expected, IConditionDescriptorWrapper root) {
		this.expected = expected;
		this.conditionsDescriptorRoot = root;
	}
	
	@Parameters
	public static Collection<Object[]> data() {
		
		// 1) one condition pass
		IConditionDescriptorWrapper root1 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		root1.addCondition(wrapperFactory(true));		

		// 2) one condition failed
		IConditionDescriptorWrapper root2 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		root2.addCondition(wrapperFactory(false));
		
		// 3) two conditions AND pass
		IConditionDescriptorWrapper root3 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		root3.addCondition(wrapperFactory(true));
		root3.addCondition(wrapperFactory(true));

		// 4) two conditions AND failed
		IConditionDescriptorWrapper root4 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		root4.addCondition(wrapperFactory(true));
		root4.addCondition(wrapperFactory(false));
		
		// 5) two conditions OR pass
		IConditionDescriptorWrapper root5 = new ConditionsDescriptorWrapper(descriptorOrFactory());
		root5.addCondition(wrapperFactory(true));
		root5.addCondition(wrapperFactory(false));		
		
		// 6) three conditions AND pass
		IConditionDescriptorWrapper root6 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		root6.addCondition(wrapperFactory(true));
		root6.addCondition(wrapperFactory(true));
		root6.addCondition(wrapperFactory(true));

		// 7) three conditions OR pass
		IConditionDescriptorWrapper root7 = new ConditionsDescriptorWrapper(descriptorOrFactory());
		root7.addCondition(wrapperFactory(false));
		root7.addCondition(wrapperFactory(false));
		root7.addCondition(wrapperFactory(true));
		
		// 8) nested 1-level conditions mix pass
		IConditionDescriptorWrapper root8 = new ConditionsDescriptorWrapper(descriptorOrFactory());
		root8.addCondition(wrapperFactory(false));
		root8.addCondition(wrapperFactory(false));
		IConditionDescriptorWrapper nested81 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		nested81.addCondition(wrapperFactory(true));
		nested81.addCondition(wrapperFactory(true));
		root8.addCondition(nested81);
		
		// 9) nested 1-level conditions mix failed
		IConditionDescriptorWrapper root9 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		root9.addCondition(wrapperFactory(true));
		root9.addCondition(wrapperFactory(true));
		IConditionDescriptorWrapper nested91 = new ConditionsDescriptorWrapper(descriptorAndFactory());
		nested91.addCondition(wrapperFactory(true));
		nested91.addCondition(wrapperFactory(false));
		root9.addCondition(nested91);
		
		// 10) nested 1-level conditions mix pass
		IConditionDescriptorWrapper root10 = new ConditionsDescriptorWrapper(descriptorAndFactory());		
		IConditionDescriptorWrapper nested101 = new ConditionsDescriptorWrapper(descriptorAndFactory());		
		nested101.addCondition(wrapperFactory(true));
		nested101.addCondition(wrapperFactory(true));
		IConditionDescriptorWrapper nested102 = new ConditionsDescriptorWrapper(descriptorOrFactory());		
		nested102.addCondition(wrapperFactory(false));
		nested102.addCondition(wrapperFactory(true));
		root10.addCondition(nested101);
		root10.addCondition(nested102);
		
		return Arrays.asList(new Object[][] { 
				{Boolean.TRUE, root1},
				{Boolean.FALSE, root2},
				{Boolean.TRUE, root3},
				{Boolean.FALSE, root4},
				{Boolean.TRUE, root5},
				{Boolean.TRUE, root6},
				{Boolean.TRUE, root7},
				{Boolean.TRUE, root8},
				{Boolean.FALSE, root9},
				{Boolean.TRUE, root10}});
	}
	
	@Test
	public void checkActionAvailability() throws Exception {
		assertEquals(expected, conditionsDescriptorRoot.isConditionPassed());
	}
	
	private static ConditionsDescriptor descriptorAndFactory(){
		ConditionsDescriptor mock = createMock(ConditionsDescriptor.class);
		expect(mock.getType()).andReturn("AND");
		replay(mock);
		return mock;
	}
	
	private static ConditionsDescriptor descriptorOrFactory(){
		ConditionsDescriptor mock = createMock(ConditionsDescriptor.class);
		expect(mock.getType()).andReturn("OR");
		replay(mock);
		return mock;
	}
	
	private static ConditionDescriptorWrapper wrapperFactory(boolean passed){
		return new ConditionDescriptorWrapper(null, passed);
	}
		
}
