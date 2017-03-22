package swt6.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.Module;
import swt6.spring.domain.Project;

@Repository("moduleRepo")
public interface ModuleRepository extends JpaRepository<Module, Long> {

	@Query("from Module m where m.project = :project")
	List<Module> findForProject(@Param("project") Project project);

}
