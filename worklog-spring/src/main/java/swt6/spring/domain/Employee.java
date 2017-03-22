package swt6.spring.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("E")
public abstract class Employee implements Serializable {

	private static final long serialVersionUID = 4982742211001582409L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String firstName;
	private String lastName;
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "zipCode", column = @Column(name = "adress_zipCode")) })
	private Address address;

	@ManyToMany( mappedBy = "members", fetch = FetchType.EAGER)
		
	private Set<Project> projects = new HashSet<>();

	@OneToMany(mappedBy = "employee", orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Issue> issues = new HashSet<>();

	public Employee() {

	}

	public Employee(String firstName, String lastName, Date dateOfBirth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
	}

	public Employee(String firstName, String lastName, Date dateOfBirth, Address address) {
		this(firstName, lastName, dateOfBirth);
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	public void addProject(Project project) {
		if (project == null)
			throw new IllegalArgumentException("project must not be null");
		project.getMembers().add(this);
		this.projects.add(project);
	}

	public void removeProject(Project project) {
		if (project == null)
			throw new IllegalArgumentException("project must not be null");
		project.getMembers().remove(this);
		this.projects.remove(project);
	}

	public void removeIssue(Issue issue) {
		issues.remove(issue);
		issue.setEmployee(null);
	}

	public void addIssue(Issue issue) {
		if (issue.getEmployee() != null)
			issue.getEmployee().removeIssue(issue);

		issues.add(issue);
		issue.setEmployee(this);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%d: %s, %s (%s)", getId(), getFirstName(), getLastName(), getDateOfBirth()));
		if (address != null)
			sb.append(", " + getAddress());
		return sb.toString();
	}

}
