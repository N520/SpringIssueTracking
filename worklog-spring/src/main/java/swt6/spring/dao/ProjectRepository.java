package swt6.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.Employee;
import swt6.spring.domain.Project;

@Repository("projectRepo")
public interface ProjectRepository extends JpaRepository<Project, Long> {
	@Query("from Project where projectLead = :lead")
	List<Project> findForProjectLead(@Param("lead") Employee lead);
}
