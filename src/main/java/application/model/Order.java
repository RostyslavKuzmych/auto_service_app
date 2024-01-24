package application.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLDelete(sql = "UPDATE orders SET is_deleted = true WHERE id = ?")
@SQLRestriction(value = "is_deleted = false")
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Car car;
    @Column(nullable = false)
    private String problemDescription;
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime dateOfAcceptance;
    @OneToMany(mappedBy = "order")
    private Set<Job> jobs = new HashSet<>();
    @ManyToMany
    @JoinTable(name = "orders_goods",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "good_id"))
    private Set<Good> goods = new HashSet<>();
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.RECEIVED;
    @Column(nullable = false)
    private BigDecimal finalAmount = BigDecimal.ZERO;
    private LocalDateTime endDate;
    @Column(nullable = false)
    private boolean isDeleted = false;

    public enum Status {
        RECEIVED,
        PROCESSED,
        SUCCESSFUL,
        NOT_SUCCESSFUL,
        PAID
    }
}
