package application.repository;

import application.model.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT order FROM Order order "
            + "JOIN FETCH order.goods JOIN FETCH order.jobs WHERE order.id = :id")
    Optional<Order> findByIdWithGoodsAndJobs(Long id);
}
