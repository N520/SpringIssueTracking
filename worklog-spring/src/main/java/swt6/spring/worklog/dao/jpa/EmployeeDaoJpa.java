package swt6.spring.worklog.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.domain.Employee;

@Repository
public class EmployeeDaoJpa implements EmployeeDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void save(Employee entity) {
		Employee persEmpl = em.merge(entity);
		entity.setId(persEmpl.getId());
	}

	@Override
	public Employee merge(Employee entity) {

		return em.merge(entity);
	}

	@Override
	public Employee findById(Long id) {
		return em.find(Employee.class, id);
	}

	@Override
	public List<Employee> findAll() {
		
		return em.createQuery("from Employee", Employee.class).getResultList();
	}

}
