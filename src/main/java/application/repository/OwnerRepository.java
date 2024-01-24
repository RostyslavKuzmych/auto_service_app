package application.repository;

import application.model.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Query("SELECT o FROM Owner o LEFT JOIN FETCH o.orders order "
            + "LEFT JOIN FETCH order.goods LEFT JOIN FETCH order.jobs WHERE o.id = :id")
    Optional<Owner> findByIdWithOrders(Long id);

    @Query("SELECT o FROM Owner o LEFT JOIN FETCH o.orders order "
            + "JOIN FETCH o.cars c WHERE c.id = :id")
    Optional<Owner> findByCarId(Long id);
}
