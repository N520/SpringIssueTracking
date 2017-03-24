package swt6.spring.dao;

import java.util.List;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Project;


public interface EmployeeRepositoryCustom {
	List<Employee> findForProject(Project project);
}
