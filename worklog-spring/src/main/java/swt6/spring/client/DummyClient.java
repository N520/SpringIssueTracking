package swt6.spring.client;

import java.util.Date;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.domain.Address;
import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Module;
import swt6.spring.domain.PermanentEmployee;
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;

public class DummyClient {

	public static void main(String[] args) {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/applicationContext.xml")) {
			IssueTrackingDal dal = factory.getBean("issueDal", IssueTrackingDal.class);

			Employee empl1 = dal.syncEmployee(
					new PermanentEmployee("some", "name", new Date(), new Address("4300", "sdas", "1231"), 20000));
			Module module = dal.syncModule(new Module("m1"));
			Project project = dal.syncProject(new Project("project 1", empl1));
			Issue issue = new Issue(project);
			LogbookEntry lb = new LogbookEntry();
			empl1 = dal.findEmployeeByLastname("name").stream().findFirst().get();

			project.addModule(module);

			project = dal.syncProject(project);

			lb = dal.createLogbookEntry(new Date(), null, empl1, module);

			lb = dal.createLogbookEntry(new Date(), null, empl1, module);

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
			Module module2 = dal.syncModule(new Module("module"));
			p2.addModule(module2);
			p2 = dal.syncProject(p2);
			module2 = p2.getModules().stream().findFirst().get();

			lb = dal.findAllLogbookEntries().iterator().next();

			issue.addLogbookEntry(lb);

			issue.moveToProject(p2, module2);

			issue = dal.syncIssue(issue);

			// issue.moveToProject(project, module);
			issue = dal.syncIssue(issue);

			dal.assignIssueToEmployee(issue, empl1);

			empl1 = dal.findEmployeeById(empl1.getId());

			 dal.findAllLogbookEntriesForProject(project).forEach(dal::deleteLogbookEntry);
			 dal.deleteProject(project);
			 dal.deleteIssue(issue);

			 dal.deleteModule(module);
//			dal.findAllProjects().forEach(dal::deleteProject);

			// dal.deleteEmployee(empl1);

			// dal.deleteEmployee(empl1);

		}

	}

}
