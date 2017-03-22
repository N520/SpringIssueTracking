package swt6.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swt6.spring.domain.Issue;

@Repository("issueRepo")
public interface IssueRepository extends JpaRepository<Issue, Long> {

}
