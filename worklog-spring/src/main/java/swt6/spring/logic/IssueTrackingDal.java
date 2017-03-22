package swt6.spring.logic;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import swt6.spring.dao.EmployeeRepository;
import swt6.spring.dao.IssueRepository;
import swt6.spring.dao.LogbookEntryRepository;
import swt6.spring.dao.ModuleRepository;
import swt6.spring.dao.ProjectRepository;
import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Module;
import swt6.spring.domain.Phase;
import swt6.spring.domain.Project;

@Component("issueDal")
@Transactional
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

	// Employee methods
	// -------------------------------------------------------------------------------------------------------------
	@Transactional(readOnly = true)
	public List<Employee> findAllEmployees() {
		return emplRepo.findAll();
	}

	@Transactional
	public Employee syncEmployee(Employee employee) {
		return emplRepo.save(employee);
	}

	@Transactional(readOnly = true)
	public Employee findEmployeeById(Long id) {
		return emplRepo.findOne(id);
	}

	@Transactional
	public void deleteEmployee(Employee employee) {
		lbRepo.findForEmployee(employee).forEach(lbRepo::delete);
		if (projectRepo.findForProjectLead(employee).size() > 0)
			throw new ProjectWithoutLeadExcpetion(
					"cannot delete employee " + employee + " because he is still lead of some projects");
		// TODO issues?
		emplRepo.delete(employee);
	}

	@Transactional(readOnly = true)
	public List<Employee> findEmployeeByLastname(String lastName) {
		return emplRepo.findByLastNameContaining(lastName);
	}

	@Transactional
	public void assignIssueToEmployee(Issue issue, Employee employee) {
		employee.addIssue(issue);
		emplRepo.save(employee);
		issueRepo.save(issue);
	}

	// END Employee methods
	// -------------------------------------------------------------------------------------------------------------

	// Project methods
	// -------------------------------------------------------------------------------------------------------------
	@Transactional
	public Project syncProject(Project project) {
		return projectRepo.save(project);
	}

	@Transactional
	public void assignEmployeeToProject(Employee employee, Project project) {
		project.addMember(employee);
		emplRepo.save(employee);
		projectRepo.save(project);
	}

	@Transactional
	public void unassignEmployeeFromProject(Employee employee, Project project) {
		project.removeMember(employee);
		emplRepo.save(employee);
		projectRepo.save(project);
	}

	@Transactional(readOnly = true)
	public Project findProjectById(Long id) {
		return projectRepo.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<Project> findAllProjects() {
		return projectRepo.findAll();
	}

	@Transactional
	public void deleteProject(Project project) {

		findAllLogbookEntriesForProject(project).forEach(this::deleteLogbookEntry);
		findAllIssuesForPoject(project).forEach(this::deleteIssue);
		findAllModulesForProject(project).forEach(this::deleteModule);
		
		projectRepo.delete(project);

	}

	// TODO addModuleToProject

	// END project methods
	// -------------------------------------------------------------------------------------------------------------

	// Issue methods
	// -------------------------------------------------------------------------------------------------------------
	@Transactional(readOnly = true)
	public List<Issue> findAllIssuesForPoject(Project project) {
		return issueRepo.findForProject(project);
	}

	@Transactional
	public Issue syncIssue(Issue issue) {
		return issueRepo.save(issue);
	}

	@Transactional
	public void deleteIssue(Issue issue) {

		issue.getLogbookEntries().forEach(this::deleteLogbookEntry);

		issue.moveToProject(null, null);
		issue.detachEmployee();
		issueRepo.delete(issue);
	}

	@Transactional(readOnly = true)
	public Issue findIssueById(Long id) {
		return issueRepo.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<Issue> findAllIssues() {
		return issueRepo.findAll();
	}

	// TODO addLogbookEntryToIssue throw exception if issue not assigned to
	// project (addLogbookEntryToProject(Projcet p, module m))
	// if adding lb to issue and issue has project also add lb to project/module
	// TODO moveIssueToProject
	// handeled in Issue.moveToProject

	// END Issue methods
	// -------------------------------------------------------------------------------------------------------------

	// Module methods
	// -------------------------------------------------------------------------------------------------------------

	@Transactional
	public Module syncModule(Module module) {
		return moduleRepo.save(module);
	}

	@Transactional(readOnly = true)
	public List<Module> findAllModules() {
		return moduleRepo.findAll();
	}

	@Transactional
	public void deleteModule(Module m) {
		m.setProject(null);
		m = syncModule(m);
		moduleRepo.delete(m);
	}
	
	@Transactional(readOnly=true)
	public List<Module> findAllModulesForProject(Project project) {
		return moduleRepo.findForProject(project);
	}

	// END module methods
	// -------------------------------------------------------------------------------------------------------------

	// Logbook methods
	// -------------------------------------------------------------------------------------------------------------
	@Transactional
	public LogbookEntry syncLogbookEntry(LogbookEntry lb) {
		return lbRepo.save(lb);
	}

	@Transactional(readOnly = true)
	public Iterable<LogbookEntry> findAllLogbookEntries() {
		return lbRepo.findAll();
	}

	@Transactional(readOnly = true)
	public List<LogbookEntry> findAllLogbookEntriesForProject(Project project) {
		return lbRepo.findForProject(project);
	}

	@Transactional
	public void deleteLogbookEntry(LogbookEntry entry) {
		// entry.setModule(null);
		// entry = syncLogbookEntry(entry);
		lbRepo.delete(entry);
		System.out.println(entry);
	}

	@Transactional
	public LogbookEntry assignEmployeeToLogbookEntry(Employee employee, LogbookEntry lb) {
		if (lb.getModule() == null)
			throw new IllegalStateException("cannot create LogbookEntry like this, use createLogbookEntry instead");
		lb.setEmployee(employee);
		return syncLogbookEntry(lb);
	}

	@Transactional
	public LogbookEntry assignLogbookEntryToModule(LogbookEntry lb, Module m) {
		if (lb.getEmployee() == null)
			throw new IllegalStateException("cannot create LogbookEntry like this, use createLogbookEntry instead");
		lb.setModule(m);
		return syncLogbookEntry(lb);
	}

	@Transactional
	public LogbookEntry createLogbookEntry(Date startTime, Date endTime, Employee employee, Module module) {
		LogbookEntry lb = new LogbookEntry(startTime, endTime);
		lb.setEmployee(employee);
		lb.setModule(module);
		return syncLogbookEntry(lb);
	}

	@Transactional
	public LogbookEntry assignLogBookEntryToPhase(LogbookEntry lb, Phase phase) {
		lb.setPhase(phase);
		return lb;

	}

	// END Logbookentries methods
	// -------------------------------------------------------------------------------------------------------------

}
