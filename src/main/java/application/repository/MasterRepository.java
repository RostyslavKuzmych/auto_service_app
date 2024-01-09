package application.repository;

import application.model.Master;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {
    @Query("SELECT m FROM Master m LEFT JOIN FETCH m.orders o LEFT JOIN FETCH "
            + "o.goods LEFT JOIN FETCH o.jobs "
            + "WHERE m.id = :id")
    Optional<Master> findByIdWithAllOrders(Long id);
}
