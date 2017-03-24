package swt6.spring.dao;

import java.util.List;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.IssueType;
import swt6.spring.domain.Project;

public interface IssueRepositoryCustom {
	List<Issue> findForProject(Project project, IssueType state);
	List<Issue> findForProjectAndEmployee(Project project, Employee employee, IssueType state);

}
