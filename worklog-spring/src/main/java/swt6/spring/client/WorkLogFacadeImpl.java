package swt6.spring.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.Project;
import swt6.spring.logic.IssueTrackingDal;

@Component("workLog")
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
	public void listIssuesForProject(Long id) {
		dal.findAllIssuesForPoject(dal.findProjectById(id)).forEach(System.out::println);

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

}
