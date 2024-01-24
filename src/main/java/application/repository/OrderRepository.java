package application.repository;

import application.model.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.goods LEFT JOIN FETCH o.jobs WHERE o.id = :id")
    Optional<Order> findByIdWithGoodsAndJobs(Long id);
}
