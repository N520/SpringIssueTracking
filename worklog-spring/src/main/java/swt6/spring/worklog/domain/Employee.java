package swt6.spring.worklog.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Employee implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  private String firstName;
  private String lastName;
  
  @Temporal(TemporalType.DATE)
  private Date dateOfBirth;

  @OneToMany(mappedBy="employee", cascade=CascadeType.ALL, fetch=FetchType.LAZY, 
             orphanRemoval=true) 
  private Set<LogbookEntry> logbookEntries = new HashSet<>();

  public Employee() {  
  }
  
  public Employee(String firstName, String lastName, Date dateOfBirth) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
  }

  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }
  
  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth; 
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

  public Set<LogbookEntry> getLogbookEntries() {
    return logbookEntries;
  }
  
  public void setLogbookEntries(Set<LogbookEntry> logbookEntries) {
    this.logbookEntries = logbookEntries;
  }
  
  public void addLogbookEntry(LogbookEntry entry) {
    if (entry.getEmployee() != null)
       entry.getEmployee().logbookEntries.remove(entry);
    this.logbookEntries.add(entry);
    entry.setEmployee(this);
  }

  public void removeLogbookEntry(LogbookEntry entry) {
    this.logbookEntries.remove(entry);
  }

	public String toString() {
    DateFormat fmt = DateFormat.getDateInstance();
    StringBuffer sb = new StringBuffer();
    sb.append(id + ": " + lastName + ", " + firstName + " (" + fmt.format(dateOfBirth.getTime()) + ")" );
    
    return sb.toString();
  }
}