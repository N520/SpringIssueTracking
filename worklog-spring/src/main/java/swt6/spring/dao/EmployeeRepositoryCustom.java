package swt6.spring.dao;

import org.springframework.stereotype.Repository;

import swt6.spring.domain.Employee;


public interface EmployeeRepositoryCustom {
	Employee findById();
}
