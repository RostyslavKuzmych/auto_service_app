package application.repository;

import application.model.Order;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"goods", "jobs"})
    Optional<Order> findOrderById(Long id);
}
