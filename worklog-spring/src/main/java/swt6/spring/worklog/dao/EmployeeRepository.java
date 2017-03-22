package swt6.spring.worklog.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import swt6.spring.worklog.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	Employee findByLastName(String lastName);

	@Query("from Employee e where e.lastName like %:substr%")
	List<Employee> findByLastNameContaining(String substr);
	
	@Query("select e from Employee e where e.dateOfBirth > :date")
	List<Employee> findOlderThan(Date date);
}
