package swt6.spring.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.IssueType;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;

@Component("workLog")
@Transactional
public class WorkLogFacadeImpl implements WorkLogFacade {

	@Autowired
	private IssueTrackingDal dal;

	@Override
	public void listProjects(String strId) {
		if (strId.equals(""))
			dal.findAllProjects().forEach(System.out::println);
		else {
			Long id;
			try {
				id = Long.parseLong(strId);
			} catch (NumberFormatException e) {
				System.err.println("invalid id" + strId);
				return;
			}
			System.out.println(dal.findProjectById(id));
		}
	}

	@Override
	public void addEmployeeToProject(Long employeeId, Long projectId) {
		Employee e = dal.findEmployeeById(employeeId);
		Project p = dal.findProjectById(projectId);
		if (e == null) {
			System.err.println("no employee with id" + employeeId);
			return;
		}
		if (p == null) {
			System.err.println("no employee with id" + projectId);
			return;
		}
		dal.assignEmployeeToProject(e, p);

		System.out.println("employees working on " + p + ":");
		p.getMembers().forEach(System.out::println);
	}

	@Override
	public void listIssuesForProject(Long id, IssueType state) {
		dal.findAllIssuesForPoject(dal.findProjectById(id), state).forEach(System.out::println);

	}

	@Override
	public void listEmployees(String strId) {
		if (strId.equals(""))
			dal.findAllEmployees().forEach(System.out::println);
		else {
			Long id;
			try {
				id = Long.parseLong(strId);
			} catch (NumberFormatException e) {
				System.err.println("invalid id" + strId);
				return;
			}
			System.out.println(dal.findEmployeeById(id));
		}
	}

	@Override
	public Employee saveEmployee(Employee employee) {
		return dal.syncEmployee(employee);

	}

	@Override
	public Project saveProject(Project project) {
		return dal.syncProject(project);
	}

	@Override
	public Issue saveIssue(Issue issue) {
		return dal.syncIssue(issue);
	}

	@Override
	public void removeEmployeeFromProject(Long employeeId, Long projectId) {
		Project p = dal.findProjectById(projectId);
		Employee e = dal.findEmployeeById(employeeId);

		dal.unassignEmployeeFromProject(e, p);
		System.out.println("employees working on " + p + ":");
		p.getMembers().forEach(System.out::println);
	}

	@Override
	public Employee findEmployeeForId(long parseLong) {
		return dal.findEmployeeById(parseLong);
	}

	@Override
	public Issue findIssueForId(long id) {
		return dal.findIssueById(id);
	}

	@Override
	public Project findProjectForId(long id) {

		return dal.findProjectById(id);
	}

	@Override
	@Transactional
	public void assignIssueToLogbookEntry(Issue issue, LogbookEntry entry) {
		dal.addLogbookEntryToIssue(entry, issue);

	}

	@Override
	public void assignIssueToProject(LogbookEntry entry, Project project) {
		dal.addLogbookEntryToProject(entry, project);

	}

	@Override
	public LogbookEntry findLogbookEntryForId(Long id) {
		return dal.findLogbookEntryById(id);
	}

	@Override
	public void listEntries(String strId) {
		if (strId.equals(""))
			dal.findAllLogbookEntries().forEach(System.out::println);
		else {
			Long id;
			try {
				id = Long.parseLong(strId);
			} catch (NumberFormatException e) {
				System.err.println("not a number" + strId);
				return;
			}
			System.out.println(dal.findLogbookEntryById(id));
		}
	}

	@Override
	public void listEmployeesOfProject(Long id) {
		Project project = dal.findProjectById(id);
		if (project == null) {
			System.err.println("no project found with id " + id);
			return;
		}

		System.out.println("lead: " + project.getProjectLeader());
		System.out.println("members:");
		project.getMembers().forEach(System.out::println);
		System.out.println("-----------------------------------------------");

	}

	@Override
	public void listIssuesOfProjectByEmployee(long id, IssueType state) {
		Project project = dal.findProjectById(id);
		if (project == null) {
			System.err.println("no project found with id " + id);
			return;
		}
		System.out.println("lead: " + project.getProjectLeader());
		for (Employee e : project.getMembers()) {
			List<Issue> issues = dal.findIssuesForProjectAndEmployee(project, e, state);
			System.out.println(e + ": ");
			for (Issue i : issues) {
				System.out.println("   " + i);
			}
		}

	}

	@Override
	public void assignEmployeToIssue(Employee employee, Issue issue) {
		dal.assignIssueToEmployee(issue, employee);
	}

	@Override
	@Transactional
	public int showWorktimeForProjectPerEmployee(Project project) {
		int sumEffort = 0;
		int sumEstimatedTime = 0;
		int sumLoggedHours = 0;

		System.out.println("starting calculation");

		for (Employee e : project.getMembers()) {
			List<Issue> issues = dal.findIssuesForProjectAndEmployee(project, e);
			System.out.println(e + " worked on ");
			for (Issue i : issues) {
				sumEffort += i.getEffort();
				sumEstimatedTime = i.getEstimatedTime();
				int loggedHours = 0;
				for (LogbookEntry lb : i.getLogbookEntries()) {
					long secs = (lb.getEndTime().getTime() - lb.getStartTime().getTime()) / 1000;
					loggedHours += (int) (secs / 3600);
				}
				sumLoggedHours += loggedHours;

				System.out.println("  " + i);
				System.out.println("    effort: " + i.getEffort() + " estimatedTime " + i.getEstimatedTime()
						+ " vs. actual Logged Time " + loggedHours);
			}
			System.out.println("--------------------------------");
		}

		System.out.println("overall time spent on project: " + sumEffort);
		System.out.println("overall time estimated on project: " + sumEstimatedTime);
		System.out.println("overall time loged on : " + sumLoggedHours);
		System.out.println("done with calculation");
		return 0;
	}

	@Override
	public void assignLogbookEntryToProject(LogbookEntry entry, Project p) {
		dal.addLogbookEntryToProject(entry, p);

	}

	@Override
	public void saveLogbookEntry(LogbookEntry lb) {
		dal.syncLogbookEntry(lb);

	}

}
