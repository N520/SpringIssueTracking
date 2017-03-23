package swt6.spring.test.entry;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Phase;
import swt6.spring.domain.PhaseDescriptor;
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class IssueTrackingDalEntryTest {

	@Autowired
	private EntityManagerFactory emFactory;
	@Autowired
	private LogbookEntry entry1;

	@Autowired
	private IssueTrackingDal dal;
	@Autowired
	private Employee empl1;
	@Autowired
	Employee empl2;
	@Autowired
	private Project project;
	@Autowired
	private Issue issue;

	@Before
	public void setUp() throws Exception {
		entry1.setStartTime(new Date());
		dal.syncEmployee(empl1);
	}

	@After
	public void tearDown() {
//		dal.findAllIssues().forEach(dal::deleteIssue);
		dal.findAllLogbookEntries().forEach(dal::deleteLogbookEntry);
	}

	@Test
	public void testSyncLogbookEntry() {
		assertEquals(dal.findAllLogbookEntries().size(), 0);
		entry1 = dal.syncLogbookEntry(entry1);
		assertEquals(dal.findAllLogbookEntries().size(), 1);
		assertNotNull(entry1.getId());

	}

	@Test
	public void testfindAllLogbookEntries() {
		assertEquals(dal.findAllEmployees().size(), 1);
		assertEquals(dal.findAllLogbookEntries().size(), 0);
		entry1 = dal.syncLogbookEntry(entry1);
		assertEquals(dal.findAllLogbookEntries().size(), 1);

	}

	@Test
	public void testfindAllLogbookEntriesForProject() {
		assertEquals(dal.findAllEmployees().size(), 1);
		assertEquals(dal.findAllLogbookEntries().size(), 0);
		project = dal.syncProject(project);
		assertEquals(1, dal.findAllProjects().size());
		issue = dal.syncIssue(issue);
		
//		entry1 = dal.syncLogbookEntry(entry1);
		
		dal.addLogbookEntryToProject(entry1, project);
		dal.assignIssueToProject(issue, project);
		dal.addLogbookEntryToIssue(entry1, issue);
		List<LogbookEntry> l = dal.findAllLogbookEntriesForProject(project);
		assertEquals(1, l.size());
		
		entry1 = dal.createLogbookEntry(new Date(), null, empl1);
		
		dal.addLogbookEntryToProject(entry1, project);
		assertEquals(2, dal.findAllLogbookEntriesForProject(project).size());

	}

	@Test
	public void testassignEmployeeToLogbookEntry() {
		empl2 = dal.syncEmployee(empl2);
		entry1 = dal.syncLogbookEntry(entry1);
		dal.assignEmployeeToLogbookEntry(empl2, entry1);
		entry1 = dal.findLogbookEntryById(entry1.getId());
		assertEquals(empl2.getId(), entry1.getEmployee().getId());
		dal.deleteEmployee(empl2);
	}

	@Test
	public void testcreateLogbookEntry() {
		assertEquals(dal.findAllEmployees().size(), 1);
		assertEquals(dal.findAllLogbookEntries().size(), 0);
		entry1 = dal.createLogbookEntry(new Date(), new Date(), empl1);
		assertEquals(dal.findAllLogbookEntries().size(), 1);
	}

	@Test
	public void testassignLogbookEntryToPhase() {
		Phase p = new Phase(PhaseDescriptor.IMPLEMENTATION);
		assertEquals(dal.findAllEmployees().size(), 1);
		assertEquals(dal.findAllLogbookEntries().size(), 0);
		entry1 = dal.syncLogbookEntry(entry1);

		entry1 = dal.assignLogBookEntryToPhase(entry1, p);
		assertEquals(dal.findAllLogbookEntries().size(), 1);
		assertEquals(entry1.getPhase().getName(), PhaseDescriptor.IMPLEMENTATION);

	}

	@Test
	public void testfindLogbookEntryById() {
		assertEquals(dal.findAllEmployees().size(), 1);
		assertEquals(dal.findAllLogbookEntries().size(), 0);
		entry1 = dal.syncLogbookEntry(entry1);
		assertNotNull(entry1.getId());
		assertNotNull(dal.findLogbookEntryById(entry1.getId()));
	}

	@Test
	public void testdeleteEntry() {
		assertEquals(dal.findAllEmployees().size(), 1);
		assertEquals(dal.findAllLogbookEntries().size(), 0);
		entry1 = dal.syncLogbookEntry(entry1);
		assertEquals(1, dal.findAllLogbookEntries().size());
		dal.deleteLogbookEntry(entry1);
		assertEquals(0, dal.findAllLogbookEntries().size());
		assertEquals(dal.findAllEmployees().size(), 1);
	}

}
