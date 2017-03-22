package swt6.spring.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import swt6.spring.dao.EmployeeRepository;
import swt6.spring.dao.IssueRepository;
import swt6.spring.dao.LogbookEntryRepository;
import swt6.spring.dao.ModuleRepository;
import swt6.spring.dao.ProjectRepository;
import swt6.spring.domain.Employee;

@Component("issueDal")
public class IssueTrackingDal {

	@Autowired
	private EmployeeRepository emplRepo;

	@Autowired
	private LogbookEntryRepository lbRepo;
	@Autowired
	private ModuleRepository moduleRepo;
	@Autowired
	private ProjectRepository projectRepo;
	@Autowired
	private IssueRepository issueRepo;

	// public void setEmplRepo(EmployeeRepository emplRepo) {
	// this.emplRepo = emplRepo;
	// }

	public List<Employee> findAllEmployees() {
		return emplRepo.findAll();
	}

	public Employee syncEmployee(Employee employee) {
		return emplRepo.save(employee);
	}

	public Employee findEmployeeById(Long id) {
		return emplRepo.findOne(id);
	}

	public void deleteEmployee(Employee employee) {
		lbRepo.findForEmployee(employee).forEach(lbRepo::delete);
		if (projectRepo.findForProjectLead(employee).size() > 0)
			throw new ProjectWithoutLeadExcpetion(
					"cannot delete employee " + employee + " because he is still lead of some projects");
		// TODO issues?
		emplRepo.delete(employee);
	}
}
