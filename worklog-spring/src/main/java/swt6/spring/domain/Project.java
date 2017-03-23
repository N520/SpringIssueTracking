package swt6.spring.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

@Entity
public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Employee> members = new HashSet<>();

	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
	private Set<Issue> issues = new HashSet<>();

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "projectLead")
	private Employee projectLeader;

	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
	private Set<LogbookEntry> entries = new HashSet<>();

	public Long getId() {
		return id;
	}

	public Project() {
	}

	public Project(String name) {
		this.name = name;
	}

	public Project(String name, Employee lead) {
		this(name);
		projectLeader = lead;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Employee> getMembers() {
		return members;
	}

	public void setMembers(Set<Employee> members) {
		this.members = members;
	}

	public void addMember(Employee empl) {
		if (empl == null)
			throw new IllegalArgumentException("employee must not be null");
		empl.getProjects().add(this);
		this.members.add(empl);
	}

	public void removeMember(Employee empl) {
		if (empl == null)
			throw new IllegalArgumentException("employee must not be null");

		for (Project p : empl.getProjects()) {
			if (p.getId().equals(getId()))
				empl.getProjects().remove(p);
		}

		for (Employee e : members) {
			if (e.getId().equals(empl.getId()))
				this.members.remove(e);
		}
	}

	public String toString() {
		return name;
	}

	public void addLogBookEntry(LogbookEntry lb) {
		if (lb == null)
			throw new IllegalArgumentException("logbookentry must not be null");
		getEntries().add(lb);
		lb.setProject(this);
	}
	
	public void removeLogbookEntry(LogbookEntry lb) {
		if (lb == null)
			throw new IllegalArgumentException("logbookEntry must not be null");
		getIssues().remove(lb);		
	}


	public void addIssue(Issue issue) {
		if (issue == null)
			throw new IllegalArgumentException("issue must not be null");
		getIssues().add(issue);
		issue.setProject(this);
	}

	public void removeIssue(Issue issue) {
		if (issue == null)
			throw new IllegalArgumentException("issue must not be null");
		getIssues().remove(issue);
		// issue must never be null thus the reference is kept since it is only
		// used while deleting issues
		// issue.setProject(null);
	}

	public Employee getProjectLeader() {
		return projectLeader;
	}

	public void setProjectLeader(Employee projectLeader) {
		this.projectLeader = projectLeader;
	}

	public Set<Issue> getIssues() {
		return issues;
	}

	public void setIssues(Set<Issue> issues) {
		this.issues = issues;
	}

	public Set<LogbookEntry> getEntries() {
		return entries;
	}

	public void setEntries(Set<LogbookEntry> entries) {
		this.entries = entries;
	}


}
