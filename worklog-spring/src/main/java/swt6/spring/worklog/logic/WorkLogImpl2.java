package swt6.spring.worklog.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.dao.EmployeeRepository;
import swt6.spring.worklog.dao.LogbookEntryDao;
import swt6.spring.worklog.dao.LogbookEntryRepository;
import swt6.spring.worklog.domain.Employee;

@Component("workLog")
@Transactional
public class WorkLogImpl2 implements WorkLogFacade {

	@Autowired
	private EmployeeRepository emplDao;
	@Autowired
	private LogbookEntryRepository entyDao;

	public WorkLogImpl2() {
	}

//	@Transactional()
	@Override
	public Employee syncEmployee(Employee employee) {
		return getEmplDao().saveAndFlush(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public Employee findEmployeeById(Long id) {

		return getEmplDao().findOne(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Employee> findAllEmployees() {
		return getEmplDao().findAll();
	}

	public LogbookEntryRepository getEntyDao() {
		return entyDao;
	}

	public void setEntyDao(LogbookEntryRepository entyDao) {
		this.entyDao = entyDao;
	}

	public EmployeeRepository getEmplDao() {
		return emplDao;
	}

	public void setEmplDao(EmployeeRepository emplDao) {
		this.emplDao = emplDao;
	}

}
