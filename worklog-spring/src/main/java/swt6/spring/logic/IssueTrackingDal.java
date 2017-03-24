package swt6.spring.logic;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
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
		if (employee == null)
			throw new IllegalArgumentException("employee must not be null");

		employee.getProjects().add(project);
		Set<Employee> members = project.getMembers();
//		Hibernate.initialize(members);
		members.add(employee);
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
		findAllIssuesForPoject(project, null).forEach(this::deleteIssue);

		projectRepo.delete(project);

	}

	// END project methods
	// -------------------------------------------------------------------------------------------------------------

	// Issue methods
	// -------------------------------------------------------------------------------------------------------------
	@Transactional(readOnly = true)
	public List<Issue> findAllIssuesForPoject(Project project, IssueType state) {
		return issueRepo.findForProject(project, state);
	}

	@Transactional
	public Issue syncIssue(Issue issue) {
		return issueRepo.save(issue);
	}

	@Transactional
	public void deleteIssue(Issue issue) {

		findLogbookEntriesForIssue(issue).forEach(this::deleteLogbookEntry);

		issue.moveToProject(null);
		issue.detachEmployee();
		issueRepo.delete(issue);
	}

	@Transactional(readOnly = true)
	public List<Issue> findIssuesForProjectAndEmployee(Project project, Employee employee) {
		return issueRepo.findForProjectAndEmployee(project, employee);
	}

	@Transactional(readOnly = true)
	public List<Issue> findIssuesForProjectAndEmployee(Project project, Employee employee, IssueType state) {
		return issueRepo.findForProjectAndEmployee(project, employee, state);
	}

	@Transactional(readOnly = true)
	public List<LogbookEntry> findLogbookEntriesForIssue(Issue issue) {
		return lbRepo.findForIssue(issue);

	}

	@Transactional(readOnly = true)
	public Issue findIssueById(Long id) {
		return issueRepo.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<Issue> findAllIssues() {
		return issueRepo.findAll();
	}

	/**
	 * adds a logbookentry to a project and removes its reference to any issue
	 * which it has been assigned to.
	 * 
	 * @param lb
	 * @param project
	 */
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

	/**
	 * assignd a {@link LogbookEntry} to a {@link Issue}. If the issue has not
	 * been assigned to a project an {@link IllegalStateException} is thrown. An
	 * Issue can either be assigned to a project or an issue
	 * 
	 * @param lb
	 * @param issue
	 * @throws IllegalStateException
	 */
	@Transactional
	public void addLogbookEntryToIssue(LogbookEntry lb, Issue issue) {
		if (issue.getProject() == null)
			throw new IllegalStateException("cannot add logbookentry to unassigned issue");
		issue.addLogbookEntry(lb);

		issue.getProject().removeLogbookEntry(lb);

		syncIssue(issue);
	}

	/**
	 * assigns an issue to a project. Any previously existing reference to other
	 * projects is overwritten. An Issue can either be assigned to a project or
	 * an issue
	 * 
	 * @param issue
	 * @param project
	 */
	@Transactional
	public void assignIssueToProject(Issue issue, Project project) {
		issue.moveToProject(project);
		lbRepo.save(lbRepo.findForIssue(issue));
		syncIssue(issue);
	}

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
	public List<LogbookEntry> findAllLogbookEntries() {
		return lbRepo.findAll();
	}

	/**
	 * finds all logbookEntries for a project and all logbookeEntries assigned
	 * to the issues, which are assigned to said project
	 * 
	 * @param project
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<LogbookEntry> findAllLogbookEntriesForProject(Project project) {
		List<LogbookEntry> l;
		l = lbRepo.findForProject(project);
		for (Issue i : project.getIssues()) {
			l.addAll(lbRepo.findForIssue(i));
		}

		return l;

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

	@Transactional(readOnly=true)
	public LogbookEntry findLogbookEntryById(long id) {
		return lbRepo.findOne(id);
	}
	@Transactional(readOnly=true)
	public List<Employee> findEmployeesForProject(Project project) {
		return emplRepo.findForProject(project);
	}

	// END Logbookentries methods
	// -------------------------------------------------------------------------------------------------------------

}
