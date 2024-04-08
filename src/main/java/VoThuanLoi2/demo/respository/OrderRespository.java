package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRespository extends JpaRepository<Orders, Long> {
    List<Orders> findByType_Name(String typeName);
    List<Orders> findByUserUsername(String username);
    List<Orders> findByType_NameNotIn(List<String> excludedTypeNames);

    @Query("SELECT b FROM Orders b Where year(b.order_date) = :year and month(b.order_date) = :month")
    public List<Orders> findListSaleWithDate(@Param("year") Integer year, @Param("month") Integer month);
}
