package VoThuanLoi2.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String voucherCode;

    @Column
    private Double discountAmount;

    @Column
    private Boolean isSent;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
