package swt6.spring.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

@Entity
@FilterDef(name = "ISSUE_STATE_FILTER", parameters = @ParamDef(name = "state", type = "string"), defaultCondition = "state = (:state)")
@Filters({ @Filter(name = "ISSUE_STATE_FILTER", condition = "state = :state"), })
public class Issue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(EnumType.STRING)
	private IssueType state;

	@Enumerated(EnumType.STRING)
	private PriorityType priority;

	@Column(nullable = true)
	private int effort;

	@Column(nullable = true)
	private int estimatedTime;

	@Column(length = 3)
	private int progress;

	@ManyToOne(optional = true)
	private Employee employee;

	@ManyToOne(optional = false)
	private Project project;

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.REMOVE }, fetch = FetchType.EAGER, mappedBy = "issue", orphanRemoval = true)
	private Set<LogbookEntry> logbookEntries = new HashSet<>();

	public Issue() {
	}

	public Issue(IssueType state, PriorityType priority, int progress, int estimatedTime, Employee employee,
			Project project) {
		super();
		this.state = state;
		this.priority = priority;
		this.progress = progress;
		this.employee = employee;
		this.project = project;
		this.estimatedTime = estimatedTime;
		project.addIssue(this);
	}

	public Issue(Project project) {
		this(IssueType.NEW, PriorityType.NORMAL, 0, 0, null, project);
	}

	public IssueType getState() {
		return state;
	}

	public void setState(IssueType state) {
		this.state = state;
	}

	public PriorityType getPriority() {
		return priority;
	}

	public void setPriority(PriorityType priority) {
		this.priority = priority;
	}

	public int getEffort() {
		return effort;
	}

	public void setEffort(int effort) {
		this.effort = effort;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public Long getId() {
		return id;
	}

	public int getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(int estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public Set<LogbookEntry> getLogbookEntries() {
		return logbookEntries;
	}

	public void setLogbookEntries(Set<LogbookEntry> logbookEntries) {
		this.logbookEntries = logbookEntries;
	}

	public void attachEmployee(Employee empl) {
		if (employee != null)
			employee.removeIssue(this);

		if (empl != null) {
			empl.addIssue(this);
		}

		this.employee = empl;
	}

	public void detachEmployee() {
		if (employee != null)
			employee.removeIssue(this);
		employee = null;
	}

	/**
	 * removes the issue from the current project and attaches it to another
	 * 
	 * @param project
	 * @param m
	 *            which module of project the logbookentries should migrate to
	 */
	public void moveToProject(Project project) {

		if (project != null) {
			project.addIssue(this);
			this.project = project;

		}
		if (this.project != null) {
			this.project.removeIssue(this);

		}
	}

	public void addLogbookEntry(LogbookEntry lb) {
		if (lb != null) {
			if (lb.getIssue() != null) {
				lb.getIssue().getLogbookEntries().remove(lb);
			}
			logbookEntries.add(lb);
			lb.setIssue(this);

		}

	}

	@Override
	public String toString() {
		String employeeStr = employee == null ? "nobody" : employee.toString();
		return getId() + ": " + state + " issue assigned to " + employeeStr;
	}

}
