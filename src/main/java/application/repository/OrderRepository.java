package application.repository;

import application.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT order FROM Order order "
            + "LEFT JOIN FETCH order.goods LEFT JOIN FETCH order.jobs WHERE order.id = :id")
    Optional<Order> findByIdWithGoodsAndJobs(Long id);

    List<Order> findAllByCarId(Long id);
}
