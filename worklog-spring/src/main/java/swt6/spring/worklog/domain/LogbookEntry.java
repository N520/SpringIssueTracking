package swt6.spring.worklog.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class LogbookEntry implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id @GeneratedValue
  private Long     id;
  
  private String   activity;

  @Temporal(TemporalType.TIME)
  private Date     startTime;
  
  @Temporal(TemporalType.TIME)
  private Date     endTime;

  @Fetch(FetchMode.JOIN) 
  @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE}, 
             fetch=FetchType.EAGER, optional=false)
  private Employee employee;
  
  public LogbookEntry() {
  }
  
  public LogbookEntry(String activity, Date start, Date end) {
    this.activity = activity;
    this.startTime = start;
    this.endTime = end;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getActivity() {
    return activity;
  }
  
  public void setActivity(String activity) {
    this.activity = activity;
  }
  
  public Employee getEmployee() {
    return employee;
  }
  
  public void setEmployee(Employee employee) {
    this.employee = employee;
  }
    
  public void detachEmployee() {
    if (this.employee != null)
      this.employee.getLogbookEntries().remove(this);
    
    this.employee = null;
  }

  public Date getStartTime() {
    return startTime;
  }
  
  public void setStartTime(Date start) {
    this.startTime = start;
  }

  public Date getEndTime() {
    return endTime;
  }
  
  public void setEndTime(Date end) {
    this.endTime = end;
  }
  
  @Override
  public String toString() {
  	DateFormat fmt = DateFormat.getDateTimeInstance();
  	return activity + ": " 
           + fmt.format(startTime) + " - " 
           + fmt.format(endTime) + " ("
           + getEmployee().getLastName() + ")";
  }
}
