package application.repository;

import application.model.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.orders o "
            + "LEFT JOIN FETCH o.goods LEFT JOIN FETCH o.jobs WHERE owner.id = :id")
    Optional<Owner> findByIdWithOrders(Long id);

    @Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.cars c "
            + "WHERE owner.id = :id")
    Optional<Owner> findByIdWithCars(Long id);

    @Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.orders o "
            + "JOIN FETCH owner.cars c WHERE c.id = :id")
    Optional<Owner> findByCarId(Long id);

    @Query("SELECT owner FROM Owner owner JOIN FETCH owner.orders o "
            + "JOIN FETCH owner.cars c WHERE o.id = :id")
    Optional<Owner> findByOrderId(Long id);
}
