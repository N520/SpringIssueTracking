package swt6.spring.client;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.Project;

public interface WorkLogFacade {
	void listProjects(String id);

	void addEmployeeToProject(Long emplId, Long projectId);

	void listIssuesForProject(Long id);

	void listEmployees(String id);

	Employee saveEmployee(Employee employee);

	Project saveProject(Project project);

	Issue saveIssue(Issue issue);
}
