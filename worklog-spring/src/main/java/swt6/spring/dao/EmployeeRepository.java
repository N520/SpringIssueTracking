package swt6.spring.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.Employee;

@Repository("emplRepo")
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	Employee findByLastName(String lastName);

	@Query("from Employee e where e.lastName like %:substr%")
	List<Employee> findByLastNameContaining(@Param("substr") String substr);

	@Query("select e from Employee e where e.dateOfBirth > :date")
	List<Employee> findOlderThan(@Param("date")Date date);
	
}
