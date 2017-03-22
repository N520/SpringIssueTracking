package swt6.spring.worklog.logic;

import java.util.List;

import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.dao.LogbookEntryDao;
import swt6.spring.worklog.domain.Employee;

public class WorkLogImpl1 implements WorkLogFacade {

	private EmployeeDao emplDao;
	private LogbookEntryDao entyDao;

	public WorkLogImpl1() {
	}
	

	@Override
	public Employee syncEmployee(Employee employee) {
		return emplDao.merge(employee);
	}

	@Override
	public Employee findEmployeeById(Long id) {
		
		return emplDao.findById(id);
	}

	@Override
	public List<Employee> findAllEmployees() {
		return emplDao.findAll();
	}

	public EmployeeDao getEmplDao() {
		return emplDao;
	}

	public void setEmplDao(EmployeeDao emplDao) {
		this.emplDao = emplDao;
	}

	public LogbookEntryDao getEntyDao() {
		return entyDao;
	}

	public void setEntyDao(LogbookEntryDao entyDao) {
		this.entyDao = entyDao;
	}

}
