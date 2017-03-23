package swt6.spring.test.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;
import swt6.util.JpaUtil;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class IssueTrackingDalProjectTest {

	@Autowired
	private EntityManagerFactory emFactory;
	@Autowired
	private Employee empl1;
	@Autowired
	private IssueTrackingDal dal;
	@Autowired
	private Employee empl2;
	@Autowired
	private Project project;
	@Autowired
	private Issue issue;

	@Before
	public void setUp() throws Exception {
		empl1 = dal.syncEmployee(empl1);
	}

	@After
	public void tearDown() {
		dal.deleteProject(project);
	}

	@Test
	public void testsyncProject() {
		assertEquals(0, dal.findAllProjects().size());
		project = dal.syncProject(project);
		assertEquals(1, dal.findAllProjects().size());
	}

	@Test
	public void testassignEmployeeToProject() {
		assertEquals(0, dal.findAllProjects().size());
		project = dal.syncProject(project);
		dal.assignEmployeeToProject(empl1, project);
		project = dal.findProjectById(project.getId());
		JpaUtil.executeInOpenEntityManager(emFactory, () -> assertEquals(1, project.getMembers().size()));
		
		dal.unassignEmployeeFromProject(empl1, project);
		project = dal.findProjectById(project.getId());
		assertEquals(0, project.getMembers().size());
	}

	@Test
	public void testfindProjectById() {
		assertEquals(0, dal.findAllProjects().size());
		project = dal.syncProject(project);
		assertEquals(project.getId(), dal.findProjectById(project.getId()).getId());

	}

	@Test
	public void testfindAllProjects() {
		assertEquals(0, dal.findAllProjects().size());
		project = dal.syncProject(project);
		assertEquals(1, dal.findAllProjects().size());

	}

	@Test
	public void testdeleteProject() {
		assertEquals(0, dal.findAllProjects().size());
		project = dal.syncProject(project);
		assertEquals(1, dal.findAllProjects().size());
		dal.deleteProject(project);
		assertEquals(0, dal.findAllProjects().size());

	}

}
