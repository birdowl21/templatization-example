package com.excel.template.repo;

import com.excel.template.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepo extends JpaRepository<Field,Long> {

    @Query(value = "select * from template where org_id=?1",nativeQuery = true)
    List<Field> findByOrgId(int orgId);
    @Query(value = "delete from template where org_id=?1 and field_id=?2",nativeQuery = true)
    @Modifying
    void deleteByOrgIdAndFieldId(long orgId, long fieldId);
    @Query(value = "delete from template where org_id=?1",nativeQuery = true)
    @Modifying
    void deleteByOrgId(int orgId);
    @Query(value = "delete from template where org_id=?1 and field_id in ?2",nativeQuery = true)
    @Modifying
    void deleteByOrgIdAndFieldIds(int orgId, List<Long> fieldIds);
}
