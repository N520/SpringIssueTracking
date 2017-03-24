package swt6.spring.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.IssueType;
import swt6.spring.domain.Project;

public class IssueRepositoryImpl implements IssueRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Transactional
	@Override
	public List<Issue> findForProject(Project project, IssueType state) {
		if (state != null)
			em.unwrap(Session.class).enableFilter("ISSUE_STATE_FILTER").setParameter("state", state.toString());
		Query query = em.createQuery("from Issue i where i.project = :project", Issue.class);
		query.setParameter("project", project);

		return query.getResultList();
	}

	@Override
	public List<Issue> findForProjectAndEmployee(Project project, Employee employee, IssueType state) {

		em.unwrap(Session.class).enableFilter("ISSUE_STATE_FILTER").setParameter("state", state.toString());

		return em.createQuery("from Issue i where i.project =:project and i.employee = :employee")
				.setParameter("project", project).setParameter("employee", employee).getResultList();
	}

}
