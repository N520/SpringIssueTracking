package swt6.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.Module;

@Repository("moduleRepo")
public interface ModuleRepository extends JpaRepository<Module, Long> {

}
