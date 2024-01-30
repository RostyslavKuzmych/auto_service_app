package application.repository;

import application.model.Master;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {
    @EntityGraph(attributePaths = "orders")
    Optional<Master> findMasterById(Long id);
}
