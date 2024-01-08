package application.repository;

import application.model.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Query("SELECT owner FROM Owner owner JOIN FETCH owner.orders o "
            + "JOIN FETCH o.goods JOIN FETCH o.jobs WHERE owner.id = :id")
    Optional<Owner> findByIdWithOrders(Long id);

    @Query("SELECT owner FROM Owner owner JOIN FETCH owner.orders o "
            + "JOIN FETCH owner.cars c WHERE c.id = :id")
    Optional<Owner> findByCarId(Long id);
}
