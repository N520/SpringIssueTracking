package swt6.spring.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Project;

public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Employee> findForProject(Project project) {

		return em.createQuery("from Employee e where e.projects = :project").setParameter("project", project).getResultList();
	}

}
