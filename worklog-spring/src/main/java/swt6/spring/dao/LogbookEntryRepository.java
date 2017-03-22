package swt6.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.LogbookEntry;

@Repository("lbRepo")
public interface LogbookEntryRepository extends JpaRepository<LogbookEntry, Long> {
	@Query("from LogbookEntry lb where lb.employee.id = :employeeId")
	List<LogbookEntry> findForEmployee(@Param("employeeId") Long employeeId);
}
