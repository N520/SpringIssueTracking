package swt6.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.Project;

@Repository("issueRepo")
public interface IssueRepository extends JpaRepository<Issue, Long>, IssueRepositoryCustom {


	@Query("from Issue i where i.project =:project and i.employee = :employee")
	List<Issue> findForProjectAndEmployee(@Param("project") Project project, @Param("employee") Employee employee);
}
