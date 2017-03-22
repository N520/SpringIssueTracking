package swt6.spring.worklog.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import swt6.spring.worklog.dao.LogbookEntryDao;
import swt6.spring.worklog.domain.LogbookEntry;

@Repository
public class LogbookEntryDaoJpa implements LogbookEntryDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void save(LogbookEntry entity) {
		LogbookEntry persEntry = em.merge(entity);
		entity.setId(persEntry.getId());
	}

	@Override
	public LogbookEntry merge(LogbookEntry entity) {
		return em.merge(entity);
	}

	@Override
	public LogbookEntry findById(Long id) {
		return em.find(LogbookEntry.class, id);
	}

	@Override
	public List<LogbookEntry> findAll() {
		return em.createQuery("from LogbookEntry", LogbookEntry.class).getResultList();
	}

	@Override
	public List<LogbookEntry> findByEmployee(Long employeeId) {
		return em.createQuery("from LogbookEntry lb where lb.employee.id =:id", LogbookEntry.class)
				.setParameter("id", employeeId).getResultList();
	}

	@Override
	public void deleteById(Long id) {
		LogbookEntry lb = em.find(LogbookEntry.class, id);
		lb.detachEmployee();
		em.remove(lb);
	}

}
