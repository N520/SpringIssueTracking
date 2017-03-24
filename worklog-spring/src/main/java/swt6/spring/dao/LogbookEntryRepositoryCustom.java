package swt6.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import swt6.spring.domain.Issue;
import swt6.spring.domain.LogbookEntry;

public interface LogbookEntryRepositoryCustom {
	@Query("from LogbookEntry lb where lb.issue = :issue")
	List<LogbookEntry> findForIssue( Issue issue);
}
