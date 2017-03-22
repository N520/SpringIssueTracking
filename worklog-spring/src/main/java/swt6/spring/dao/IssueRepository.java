package swt6.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.Issue;
import swt6.spring.domain.Project;

@Repository("issueRepo")
public interface IssueRepository extends JpaRepository<Issue, Long> {

	@Query("from Issue i where i.project = :project")
	List<Issue> findForProject(@Param("project") Project project);

}
