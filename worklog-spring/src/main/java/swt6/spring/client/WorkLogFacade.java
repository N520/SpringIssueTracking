package swt6.spring.client;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.IssueType;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Project;

public interface WorkLogFacade {
	void listProjects(String id);

	void addEmployeeToProject(Long emplId, Long projectId);

	void listIssuesForProject(Long id, IssueType state);

	void listEmployees(String id);

	Employee saveEmployee(Employee employee);

	Project saveProject(Project project);

	Issue saveIssue(Issue issue);

	void removeEmployeeFromProject(Long employeeId, Long projectId);

	Employee findEmployeeForId(long parseLong);

	Issue findIssueForId(long parseLong);

	Project findProjectForId(long parseLong);

	void assignIssueToLogbookEntry(Issue issue, LogbookEntry entry);

	void assignIssueToProject(LogbookEntry entry, Project project);

	LogbookEntry findLogbookEntryForId(Long id);

	void listEntries(String strId);
}
