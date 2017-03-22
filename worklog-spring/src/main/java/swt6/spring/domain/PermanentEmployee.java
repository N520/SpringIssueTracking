package swt6.spring.domain;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("P") // needed for Inheritancestrategy singletable only
public class PermanentEmployee extends Employee {
	private static final long serialVersionUID = 1L;
	private double salary;

	public PermanentEmployee() {
	}

	public PermanentEmployee(String firstName, String lastName, Date dateOfBirth) {
		super(firstName, lastName, dateOfBirth);
	}

	public PermanentEmployee(String firstName, String lastName, Date dateOfBirth, Address address, int salary) {
		super(firstName, lastName, dateOfBirth, address);
		this.salary = salary;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String toString() {
		return super.toString() + ", salary=" + salary;
	}
}
