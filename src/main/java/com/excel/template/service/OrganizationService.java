package com.excel.template.service;

import com.excel.template.entity.Organization;
import com.excel.template.repo.OrganizationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationService {

    private OrganizationRepo organizationRepo;

    @Autowired
    public OrganizationService(OrganizationRepo organizationRepo) {
        this.organizationRepo = organizationRepo;
    }

    public Optional<Organization> findById(long orgId) {
        return organizationRepo.findById(orgId);
    }
}
