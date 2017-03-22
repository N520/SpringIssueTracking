package swt6.spring.worklog.dao;

import java.util.List;

import swt6.spring.worklog.domain.LogbookEntry;

public interface LogbookEntryDao extends GenericDao<LogbookEntry, Long> {
	List<LogbookEntry> findByEmployee(Long employeeId);
	void deleteById(Long id);
}
