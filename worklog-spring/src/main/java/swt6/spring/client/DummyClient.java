package swt6.spring.client;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.domain.Address;
import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.PermanentEmployee;
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;
import swt6.util.JpaUtil;

public class DummyClient {

	public static void main(String[] args) {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/applicationContext.xml")) {
			IssueTrackingDal dal = factory.getBean("issueDal", IssueTrackingDal.class);

			Employee empl1 = dal.syncEmployee(
					new PermanentEmployee("some", "name", new Date(), new Address("4300", "sdas", "1231"), 20000));
			Project project = dal.syncProject(new Project("project 1", empl1));
			Issue issue = new Issue(project);
			LogbookEntry lb = new LogbookEntry();
			empl1 = dal.findEmployeeByLastname("name").stream().findFirst().get();

			project = dal.syncProject(project);

			lb = dal.createLogbookEntry(new Date(), null, empl1);

			lb = dal.createLogbookEntry(new Date(), null, empl1);

			dal.findAllEmployees().forEach(System.out::println);

			// dal.findAllLogbookEntries().forEach(dal::deleteLogbookEntry);

			// projectRepo.findForProjectLead(empl1).forEach(projectRepo::delete);

			dal.assignEmployeeToProject(empl1, project);
			empl1 = dal.findEmployeeById(empl1.getId());
			project = dal.findProjectById(project.getId());

			issue = dal.syncIssue(issue);
			System.out.println("=========== DELETING ISSUE ===========");
			dal.deleteIssue(issue);
			System.out.println("=========== DONE DELETING ISSUE ===========");

			Project p2 = dal.syncProject(new Project("project2", empl1));
			p2 = dal.syncProject(p2);

			lb = dal.findAllLogbookEntries().iterator().next();

			// issue.addLogbookEntry(lb);
			issue = dal.syncIssue(new Issue(project));
			LogbookEntry lb2 = dal.createLogbookEntry(new Date(), new Date(), empl1);
			Issue issue2 = issue;
			dal.addLogbookEntryToIssue(lb2, issue2);

			System.out.println("PRINTING ISSUE ---------------------------------");

			lb = dal.findLogbookEntryById(4L);
			dal.addLogbookEntryToProject(lb, project);
			List<LogbookEntry> l = dal.findAllLogbookEntriesForProject(project);
			System.out.println(l.size());
			System.out.println("PRINTING ISSUE ---------------------------------");

			issue.getProject().getEntries().forEach(System.out::println);

		}

	}

}
