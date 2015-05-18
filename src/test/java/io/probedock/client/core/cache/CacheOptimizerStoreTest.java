package io.probedock.client.core.cache;

import io.probedock.client.common.config.Configuration;
import io.probedock.client.common.config.ServerConfiguration;
import io.probedock.client.common.model.v1.ModelFactory;
import io.probedock.client.common.model.v1.TestResult;
import io.probedock.client.common.model.v1.TestRun;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * 
 * @author Laurent Prévost, laurent.prevost@lotaris.com
 */
public class CacheOptimizerStoreTest {
	
	private TestRun referencedTestRun;
	private CacheOptimizerStore cacheOptimizerStore;
	
	@Mock
	private Configuration configuration;
	@Mock
	private ServerConfiguration serverConfiguration;

	@Before
	public void setUp() {
		Map<String, String> data = new HashMap<>();
		data.put("dataKey", "dataValue");
		
		referencedTestRun = ModelFactory.createTestRun("projectAbcd", "version", 10L,
			Arrays.asList(
				ModelFactory.createTestResult("key", "name", "category", 10L, "message", true, true,
					new HashSet<>(Arrays.asList(new String[]{"tag1", "tag2"})),
					new HashSet<>(Arrays.asList(new String[]{"ticket1", "ticket2"})),
					data
				)
			),
			null
		);

		MockitoAnnotations.initMocks(this);

		when(configuration.getOptimizerCacheDir()).thenReturn("/tmp/rox-cache");
		when(configuration.getServerConfiguration()).thenReturn(serverConfiguration);
		when(serverConfiguration.getBaseUrlFootprint()).thenReturn("footprint");
		
		cacheOptimizerStore = new CacheOptimizerStore();
		cacheOptimizerStore.start(configuration);
		cacheOptimizerStore.cleanCaches();
	}
	
	@Test
	public void TestRunShouldBeCompleteWhenNotCached() {
		TestRun optiOne = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		TestResult testResult = optiOne.getTestResults().get(0);
		
		assertNotNull("Name must be filled", testResult.getName());
		assertNotNull("Category must be filled", testResult.getCategory());
		assertFalse("Tags must be filled", testResult.getTags().isEmpty());
		assertFalse("Tickets must be filled", testResult.getTickets().isEmpty());
		assertFalse("Data must be filled", testResult.getData().isEmpty());
	}
	
	@Test
	public void TestRunShouldBeCleanedWhenCached() {
		referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		TestResult testResult = optiTwo.getTestResults().get(0);
		
		assertNull("Name must be null", testResult.getName());
		assertNull("Category must be null", testResult.getCategory());
		assertNull("Tags must be empty", testResult.getTags());
		assertNull("Tickets must be empty", testResult.getTickets());
		assertNull("Data must be empty", testResult.getData());
	}
	
	@Test
	public void TestRunShouldBeCompleteWhenNameChanged() {
		TestRun optiOne = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		referencedTestRun.getTestResults().get(0).setName("different name");
		String optiOneStr = optiOne.getTestResults().get(0).toString();
		
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		String optiTwoStr = optiTwo.getTestResults().get(0).toString();
		
		assertNotNull("Name must be filled", optiTwo.getTestResults().get(0).getName());
		assertFalse("The test toString should be different", optiOneStr.equals(optiTwoStr));
	}
	
	@Test
	public void TestRunShouldBeCompleteWhenCategoryChanged() {
		TestRun optiOne = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		referencedTestRun.getTestResults().get(0).setCategory("different category");
		String optiOneStr = optiOne.getTestResults().get(0).toString();
		
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		String optiTwoStr = optiTwo.getTestResults().get(0).toString();
		
		assertNotNull("Category must be filled", optiTwo.getTestResults().get(0).getCategory());
		assertFalse("The test toString should be different", optiOneStr.equals(optiTwoStr));
	}
	
	@Test
	public void TestRunShouldBeCompleteWhenProjectChanged() {
		referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		referencedTestRun.setProjectId("different project");
		
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		assertNotNull("Name must be filled", optiTwo.getTestResults().get(0).getName());
		//assertFalse("The test toString should be different", optiOneStr.equals(optiTwoStr));
	}

	@Test
	public void TestRunShouldBeCompleteWhenVersionChanged() {
		referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		referencedTestRun.setProjectVersion("different version");
		
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		assertNotNull("Name must be filled", optiTwo.getTestResults().get(0).getName());
	}

	@Test
	public void TestRunShouldBeCompleteWhenTagsChanged() {
		TestRun optiOne = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		referencedTestRun.getTestResults().get(0).getTags().add("new tag");
		String optiOneStr = optiOne.getTestResults().get(0).toString();
		
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		String optiTwoStr = optiTwo.getTestResults().get(0).toString();
		
		assertFalse("Tags must be filled", optiTwo.getTestResults().get(0).getTags().isEmpty());
		assertFalse("The test toString should be different", optiOneStr.equals(optiTwoStr));
	}

	@Test
	public void TestRunShouldBeCompleteWhenTicketsChanged() {
		TestRun optiOne = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		referencedTestRun.getTestResults().get(0).getTickets().add("new ticket");
		String optiOneStr = optiOne.getTestResults().get(0).toString();
		
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		String optiTwoStr = optiTwo.getTestResults().get(0).toString();
		
		assertFalse("Tickets must be filled", optiTwo.getTestResults().get(0).getTickets().isEmpty());
		assertFalse("The test toString should be different", optiOneStr.equals(optiTwoStr));
	}
	
	@Test
	public void TestRunShouldBeCompleteWhenDataChanged() {
		TestRun optiOne = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		
		referencedTestRun.getTestResults().get(0).getData().put("new key", "new value");
		String optiOneStr = optiOne.getTestResults().get(0).toString();
		
		TestRun optiTwo = (TestRun) referencedTestRun.getOptimizer().optimize(cacheOptimizerStore, referencedTestRun);
		String optiTwoStr = optiTwo.getTestResults().get(0).toString();
		
		assertFalse("Data must be filled", optiTwo.getTestResults().get(0).getData().isEmpty());
		assertFalse("The test toString should be different", optiOneStr.equals(optiTwoStr));
	}
}
