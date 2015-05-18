package io.probedock.client.utils;

import io.probedock.client.annotations.ProbeTest;
import io.probedock.client.annotations.ProbeTestClass;
import io.probedock.client.test.utils.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * TestResult class for {@link CollectionHelper}
 * 
 * @author Laurent Prevost <laurent.prevost@probe-dock.io>
 */
public class CollectionHelperTest {
	@Mock
	private Logger LOGGER = LoggerFactory.getLogger(CollectionHelper.class);;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		try {
			TestHelper.setFinalStatic(CollectionHelper.class.getDeclaredField("LOGGER"), LOGGER);
		}
		catch (Exception e) {}
	}
	
	@After
	public void tearDown() {
		try {
			TestHelper.setFinalStatic(CollectionHelper.class.getDeclaredField("LOGGER"), LoggerFactory.getLogger(CollectionHelper.class));
		}
		catch (Exception e) {}
	}
	
	@Test
	public void whenNoParameterAreGivenToGetTheTagsItShouldReturnEmptyTags() {
		assertTrue("The tags must be emtpy", CollectionHelper.getTags(null, null, null).isEmpty());
	}
	
	@Test
	public void whenNoTagsAreAvailableTheTagsMustBeEmpty() {
		assertTrue("The tags must be empty", CollectionHelper.getTags(new HashSet<String>(), null, null).isEmpty());
	}
	
	@Test
	public void whenTagsAreSpecifiedOnlyInTheConfigurationOnlyTheseMustBeReturned() {
		Set<String> tags = CollectionHelper.getTags(new HashSet<String>(Arrays.asList(new String[]{"tag1", "tag2"})), null, null);
		
		assertEquals("The tags should contains two tags", 2, tags.size());
		assertTrue("[tag1] must be present in the tags", tags.contains("tag1"));
		assertTrue("[tag2] must be present in the tags", tags.contains("tag2"));
	}
	
	@Test
	public void theTagsShouldRespectCertainFormat() {
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertTrue("The message should contains [The tag *ç%&/( does not respect the following pattern]",
					((String) invocation.getArguments()[0]).contains("The tag *ç%&/( does not respect the following pattern"));
				return null;
			}
		}).when(LOGGER).warn(any(String.class));
		
		Set<String> tags = CollectionHelper.getTags(new HashSet<String>(Arrays.asList(new String[]{"*ç%&/(", "tagValid"})), null, null);
		
		assertEquals("The tags should contains one tag", 1, tags.size());
		assertTrue("The tags should contains [tagValid]", tags.contains("tagValid"));
	}
	
	@Test
	public void whenTagsAreSpecifiedInTheConfigurationAndTheClassAnnotationTheCompiledTagsMustBeReturned() {
		ProbeTestClass classAnnotation = mock(ProbeTestClass.class);
		
		when(classAnnotation.tags()).thenReturn(new String[]{"tag3", "tag4"});
		
		Set<String> tags = CollectionHelper.getTags(new HashSet<String>(Arrays.asList(new String[]{"tag1", "tag2"})), null, classAnnotation);
		
		assertEquals("The tags should contains four tags", 4, tags.size());
		assertTrue("[tag1] must be present in the tags", tags.contains("tag1"));
		assertTrue("[tag2] must be present in the tags", tags.contains("tag2"));
		assertTrue("[tag3] must be present in the tags", tags.contains("tag3"));
		assertTrue("[tag4] must be present in the tags", tags.contains("tag4"));
	}

	@Test
	public void whenTagsAreSpecifiedInTheConfigurationAndAnnotationsTheCompiledTagsMustBeReturned() {
		ProbeTestClass classAnnotation = mock(ProbeTestClass.class);
		when(classAnnotation.tags()).thenReturn(new String[]{"tag3", "tag4"});

		ProbeTest methodAnnotation = mock(ProbeTest.class);
		when(methodAnnotation.tags()).thenReturn(new String[]{"tag5", "tag6"});
		
		Set<String> tags = CollectionHelper.getTags(new HashSet<String>(Arrays.asList(new String[]{"tag1", "tag2"})), methodAnnotation, classAnnotation);
		
		assertEquals("The tags should contains six tags", 6, tags.size());
		assertTrue("[tag1] must be present in the tags", tags.contains("tag1"));
		assertTrue("[tag2] must be present in the tags", tags.contains("tag2"));
		assertTrue("[tag3] must be present in the tags", tags.contains("tag3"));
		assertTrue("[tag4] must be present in the tags", tags.contains("tag4"));
		assertTrue("[tag5] must be present in the tags", tags.contains("tag5"));
		assertTrue("[tag6] must be present in the tags", tags.contains("tag6"));
	}
	
	@Test
	public void addingMultipleTimesTheSameTagShouldOnlyKeepOne() {
		ProbeTestClass classAnnotation = mock(ProbeTestClass.class);
		when(classAnnotation.tags()).thenReturn(new String[]{"tag1", "tag2"});

		ProbeTest methodAnnotation = mock(ProbeTest.class);
		when(methodAnnotation.tags()).thenReturn(new String[]{"tag1", "tag2"});
		
		Set<String> tags = CollectionHelper.getTags(new HashSet<String>(Arrays.asList(new String[]{"tag1", "tag2"})), methodAnnotation, classAnnotation);
		
		assertEquals("The tags should contains six tags", 2, tags.size());
		assertTrue("[tag1] must be present in the tags", tags.contains("tag1"));
		assertTrue("[tag2] must be present in the tags", tags.contains("tag2"));
	}

	@Test
	public void whenNoParameterAreGivenToGetTheTicketsItShouldReturnEmptyTickets() {
		assertTrue("The tickets must be emtpy", CollectionHelper.getTickets(null, null, null).isEmpty());
	}
	
	@Test
	public void whenNoTicketsAreAvailableTheTicketsMustBeEmpty() {
		assertTrue("The tickets must be empty", CollectionHelper.getTickets(new HashSet<String>(), null, null).isEmpty());
	}
	
	@Test
	public void whenTicketsAreSpecifiedOnlyInTheConfigurationOnlyTheseMustBeReturned() {
		Set<String> tickets = CollectionHelper.getTickets(new HashSet<String>(Arrays.asList(new String[]{"ticket-1", "ticket-2"})), null, null);
		
		assertEquals("The tickets should contains two tickets", 2, tickets.size());
		assertTrue("[ticket-1] must be present in the tickets", tickets.contains("ticket-1"));
		assertTrue("[ticket-2] must be present in the tickets", tickets.contains("ticket-2"));
	}
	
	@Test
	public void whenTicketsAreSpecifiedInTheConfigurationAndTheClassAnnotationTheCompiledTicketsMustBeReturned() {
		ProbeTestClass classAnnotation = mock(ProbeTestClass.class);
		
		when(classAnnotation.tickets()).thenReturn(new String[]{"ticket-3", "ticket-4"});
		
		Set<String> tickets = CollectionHelper.getTickets(new HashSet<String>(Arrays.asList(new String[]{"ticket-1", "ticket-2"})), null, classAnnotation);
		
		assertEquals("The tickets should contains four tickets", 4, tickets.size());
		assertTrue("[ticket-1] must be present in the tickets", tickets.contains("ticket-1"));
		assertTrue("[ticket-2] must be present in the tickets", tickets.contains("ticket-2"));
		assertTrue("[ticket-3] must be present in the tickets", tickets.contains("ticket-3"));
		assertTrue("[ticket-4] must be present in the tickets", tickets.contains("ticket-4"));
	}

	@Test
	public void whenTicketsAreSpecifiedInTheConfigurationAndAnnotationsTheCompiledTicketsMustBeReturned() {
		ProbeTestClass classAnnotation = mock(ProbeTestClass.class);
		when(classAnnotation.tickets()).thenReturn(new String[]{"ticket-3", "ticket-4"});

		ProbeTest methodAnnotation = mock(ProbeTest.class);
		when(methodAnnotation.tickets()).thenReturn(new String[]{"ticket-5", "ticket-6"});
		
		Set<String> tickets = CollectionHelper.getTickets(new HashSet<String>(Arrays.asList(new String[]{"ticket-1", "ticket-2"})), methodAnnotation, classAnnotation);
		
		assertEquals("The tickets should contains six tickets", 6, tickets.size());
		assertTrue("[ticket-1] must be present in the tickets", tickets.contains("ticket-1"));
		assertTrue("[ticket-2] must be present in the tickets", tickets.contains("ticket-2"));
		assertTrue("[ticket-3] must be present in the tickets", tickets.contains("ticket-3"));
		assertTrue("[ticket-4] must be present in the tickets", tickets.contains("ticket-4"));
		assertTrue("[ticket-5] must be present in the tickets", tickets.contains("ticket-5"));
		assertTrue("[ticket-6] must be present in the tickets", tickets.contains("ticket-6"));
	}	

	@Test
	public void addingMultipleTimesTheSameTicketShouldOnlyKeepOne() {
		ProbeTestClass classAnnotation = mock(ProbeTestClass.class);
		when(classAnnotation.tickets()).thenReturn(new String[]{"ticket-1", "ticket-2"});

		ProbeTest methodAnnotation = mock(ProbeTest.class);
		when(methodAnnotation.tickets()).thenReturn(new String[]{"ticket-1", "ticket-2"});
		
		Set<String> tickets = CollectionHelper.getTickets(new HashSet<String>(Arrays.asList(new String[]{"ticket-1", "ticket-2"})), methodAnnotation, classAnnotation);
		
		assertEquals("The tickets should contains six tickets", 2, tickets.size());
		assertTrue("[ticket-1] must be present in the tickets", tickets.contains("ticket-1"));
		assertTrue("[ticket-2] must be present in the tickets", tickets.contains("ticket-2"));
	}	
}
