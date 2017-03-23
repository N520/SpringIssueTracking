package swt6.spring.test.employee;

import static org.junit.Assert.*;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class IssueTrackingDalEmployeeTest {

	@Autowired
	private EntityManagerFactory emFactory;
	@Autowired
	private Employee empl1;
	@Autowired
	IssueTrackingDal issueDal;
	@Autowired
	private Project project;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
		issueDal.findAllEmployees().forEach(issueDal::deleteEmployee);
	}

	@Test
	public void testSyncEmployee() {
		empl1 = issueDal.syncEmployee(empl1);
		assertNotNull(empl1.getId());
		assertNotNull(issueDal.findAllEmployees().get(0));
	}

	@Test
	public void testfindAllEmployees() {
		assertEquals(issueDal.findAllEmployees().size(), 0);
		empl1 = issueDal.syncEmployee(empl1);
		assertEquals(issueDal.findAllEmployees().size(), 1);
	}

	@Test
	public void testfindEmployeeById() {
		assertNull(issueDal.findEmployeeById(1L));
		empl1 = issueDal.syncEmployee(empl1);
		assertNotNull(issueDal.findEmployeeById(empl1.getId()));

	}

	@Test
	public void testDeleteEmployee() {
		empl1 = issueDal.syncEmployee(empl1);
		assertNotNull(empl1);
		assertNotNull(issueDal.findEmployeeById(empl1.getId()));
		issueDal.deleteEmployee(empl1);
		assertNull(issueDal.findEmployeeById(empl1.getId()));
	}

	@Test
	public void testEmplyoeeByLastname() {
		empl1 = issueDal.syncEmployee(empl1);
		assertNotNull(empl1);
		assertEquals("Jack", issueDal.findEmployeeByLastname("Ni").get(0).getFirstName());
	}
	

}
