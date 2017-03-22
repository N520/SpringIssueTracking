package swt6.spring.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Module implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6314940550724631882L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@ManyToOne
	private Project project;

	public Module() {

	}

	public Module(String name) {
		super();
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Long getId() {
		return id;
	}

	
	
}
