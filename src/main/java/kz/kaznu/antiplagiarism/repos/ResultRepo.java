package kz.kaznu.antiplagiarism.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kz.kaznu.antiplagiarism.model.Result;

import java.util.List;

@Repository
public interface ResultRepo extends CrudRepository<Result, Long> {

    List<Result> findAllById(Long id);
}
