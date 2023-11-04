package com.excel.template.repo;

import com.excel.template.entity.Organization;
import com.excel.template.service.OrganizationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization,Long> {

}
