package swt6.spring.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import swt6.spring.domain.Issue;
import swt6.spring.domain.LogbookEntry;

public class LogbookEntryRepositoryImpl implements LogbookEntryRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<LogbookEntry> findForIssue(Issue issue) {
		// TODO Auto-generated method stub
		return em.createQuery("from LogbookEntry lb where lb.issue = :issue").setParameter("issue", issue)
				.getResultList();

	}

}
