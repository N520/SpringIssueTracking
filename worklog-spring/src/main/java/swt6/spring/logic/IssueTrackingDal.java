package swt6.spring.logic;

import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import swt6.spring.dao.EmployeeRepository;
import swt6.spring.dao.IssueRepository;
import swt6.spring.dao.LogbookEntryRepository;
import swt6.spring.dao.ProjectRepository;
import swt6.spring.domain.Employee;
import swt6.spring.domain.Issue;
import swt6.spring.domain.IssueType;
import swt6.spring.domain.LogbookEntry;
import swt6.spring.domain.Phase;
import swt6.spring.domain.Project;
import swt6.util.JpaUtil;

@Component("issueDal")
@Transactional
public class IssueTrackingDal {

	@Autowired
	private EmployeeRepository emplRepo;

	@Autowired
	private LogbookEntryRepository lbRepo;

	@Autowired
	private ProjectRepository projectRepo;
	@Autowired
	private IssueRepository issueRepo;

	@Autowired
	private EntityManagerFactory emFactory;

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

		projectRepo.delete(project);

	}

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

		issue.moveToProject(null);
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
	// project ( use addLogbookEntryToProject(Projcet p, module m))
	// if adding lb to issue and issue has project also add lb to project/module
	@Transactional
	public void addLogbookEntryToProject(LogbookEntry lb, Project project) {
		Issue issue = lb.getIssue();
		if (issue != null) {
			lb.removeIssue();
			syncIssue(issue);
		}

		project.addLogBookEntry(lb);

		syncLogbookEntry(lb);

	}

	@Transactional
	public void addLogbookEntryToIssue(LogbookEntry lb, Issue issue) {
		if (issue.getProject() == null)
			throw new IllegalStateException("cannot add logbookentry to unassigned issue");
		issue.addLogbookEntry(lb);

		issue.getProject().addLogBookEntry(lb);

		syncIssue(issue);
		syncLogbookEntry(lb);
	}
	// TODO moveIssueToProject
	// handeled in Issue.moveToProject

	/**
	 * assigns an employee to the issue and also allows to update the issues
	 * state with it. if updatedState == null it won't be updatet
	 * 
	 * @param issue
	 * @param employee
	 * @param updatedState
	 * @throws {@link
	 *             IllegalStateException} if the employee has already worked on
	 *             the issue
	 */
	@Transactional
	public void assignIssueToEmployee(Issue issue, Employee employee, IssueType updatedState) {
		if (issue.getEmployee() != null && issue.getLogbookEntries().size() > 0)
			throw new IllegalStateException("cannot move Employee from issue on which he has already worked on");
		employee.addIssue(issue);
		if (updatedState != null)
			issue.setState(updatedState);
		emplRepo.save(employee);
		issueRepo.save(issue);
	}

	/**
	 * assigns an employee to the issue
	 * 
	 * @param issue
	 * @param employee
	 * @throws {@link
	 *             IllegalStateException} if the employee has already worked on
	 *             the issue
	 */
	public void assignIssueToEmployee(Issue issue, Employee employee) {
		assignIssueToEmployee(issue, employee, null);
	}
	// END Issue methods
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
		lb.setEmployee(employee);
		return syncLogbookEntry(lb);
	}

	@Transactional
	public LogbookEntry createLogbookEntry(Date startTime, Date endTime, Employee employee) {
		LogbookEntry lb = new LogbookEntry(startTime, endTime);
		lb.setEmployee(employee);
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
