package com.excel.template.repo;

import com.excel.template.entity.Value;
import jakarta.transaction.Transactional;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValueRepo extends JpaRepository<Value,Long> {
    @Modifying
    @Query(value = "delete from data_values where field_id in ?1",nativeQuery = true)
    void deleteAllByFieldId(List<Long> fieldIds);
}
