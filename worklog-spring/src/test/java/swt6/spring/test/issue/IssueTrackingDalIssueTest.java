package swt6.spring.test.issue;

import static org.junit.Assert.*;

import java.util.Date;

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
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class IssueTrackingDalIssueTest {

	@Autowired
	private EntityManagerFactory emFactory;
	@Autowired
	private LogbookEntry entry1;

	@Autowired
	private IssueTrackingDal dal;
	@Autowired
	private Employee empl1;
	@Autowired
	private Employee empl2;
	@Autowired
	private Project project;
	@Autowired
	private Issue issue;

	@Before
	public void setUp() throws Exception {
		empl1 = dal.syncEmployee(empl1);
		project = dal.syncProject(project);
		entry1.setStartTime(new Date());
		entry1 = dal.syncLogbookEntry(entry1);
	}

	@After
	public void tearDown() {
//		 dal.deleteProject(project);
		dal.deleteIssue(issue);
		dal.findAllLogbookEntries().forEach(dal::deleteLogbookEntry);

//		 dal.deleteEmployee(empl1);
	}

	@Test
	public void testFindAllIssuesForProject() {
		issue = dal.syncIssue(issue);
		project = dal.syncProject(new Project("awesoem", empl1));
		assertEquals(dal.findAllIssuesForPoject(project).size(), 0);

		dal.assignIssueToProject(issue, project);

		assertEquals(dal.findAllIssuesForPoject(project).size(), 1);
	}

	@Test
	public void testSyncIssue() {
		assertEquals(dal.findAllIssues().size(), 0);
		issue = dal.syncIssue(issue);
		assertEquals(dal.findAllIssues().size(), 1);
		assertNotNull(dal.findIssueById(issue.getId()));

	}

	@Test
	public void testdeleteIssue() {
		assertEquals(dal.findAllIssues().size(), 0);
		issue = dal.syncIssue(issue);
		dal.deleteIssue(issue);
		assertNull(dal.findIssueById(issue.getId()));
	}

	@Test
	public void testfindIssueById() {
		assertEquals(dal.findAllIssues().size(), 0);
		issue = dal.syncIssue(issue);
		assertNotNull(dal.findIssueById(issue.getId()));
	}

	@Test
	public void testfindAllIssues() {
		assertEquals(dal.findAllIssues().size(), 0);
		issue = dal.syncIssue(issue);
		assertEquals(dal.findAllIssues().size(), 1);
	}

	@Test
	public void testaddLogbookEntryToProject() {
		issue = dal.syncIssue(issue);
		// entry1 = dal.syncLogbookEntry(entry1);
		dal.addLogbookEntryToProject(entry1, project);
		assertEquals(1, dal.findAllLogbookEntriesForProject(project).size());

	}

	@Test
	public void testaddLogbookEntryToIssue() {
		issue = dal.syncIssue(issue);
		dal.addLogbookEntryToIssue(entry1, issue);
		entry1 = dal.findLogbookEntryById(entry1.getId());
		issue = dal.findIssueById(issue.getId());
		assertEquals(dal.findLogbookEntriesForIssue(issue).stream().findFirst().get().getId(), entry1.getId());

	}

	@Test
	public void testassignIssueToProject() {
		issue = dal.syncIssue(issue);

		dal.assignIssueToProject(issue, project);

		assertEquals(dal.findProjectById(project.getId()).getIssues().stream().findFirst().get().getId(),
				issue.getId());
		issue = dal.findIssueById(issue.getId());
	}

	@Test
	public void testassignIssueToEmployee() {
		// issue = dal.syncIssue(issue);
		// dal.assignIssueToEmployee(issue, empl2);
		// issue = dal.findIssueById(issue.getId());

	}

}
