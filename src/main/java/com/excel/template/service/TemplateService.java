package com.excel.template.service;

import com.excel.template.entity.Field;
import com.excel.template.entity.Organization;
import com.excel.template.repo.TemplateRepo;
import com.excel.template.reqobj.Column;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    private TemplateRepo templateRepo;

    @Autowired
    public TemplateService(TemplateRepo templateRepo) {
        this.templateRepo = templateRepo;
    }

    public List<Field> findFields(int orgId) {
        return templateRepo.findByOrgId(orgId);
    }

    public void addColumn(Organization organization, Column column) {
            Field newField = new Field();
            newField.setFieldName(column.getName());
            newField.setOrganization(organization);
            templateRepo.save(newField);
    }


    public void editColumn(Field field, String newFieldName) {
        field.setFieldName(newFieldName);
        templateRepo.save(field);
    }

    @Transactional
    public void deleteColumn(long orgId,Field field) {
        templateRepo.deleteByOrgIdAndFieldId(orgId,field.getFieldId());
    }

    public void saveAll(List<Field> fields) {
        templateRepo.saveAll(fields);
    }
}
