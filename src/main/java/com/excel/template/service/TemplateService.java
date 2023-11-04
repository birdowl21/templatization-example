package com.excel.template.service;

import com.excel.template.entity.Field;
import com.excel.template.entity.Organization;
import com.excel.template.entity.Value;
import com.excel.template.repo.TemplateRepo;
import com.excel.template.repo.ValueRepo;
import com.excel.template.reqobj.Column;
import com.excel.template.reqobj.EditedField;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TemplateService {

    private TemplateRepo templateRepo;

    private ValueRepo valueRepo;
    @Autowired
    public TemplateService(TemplateRepo templateRepo, ValueRepo valueRepo) {
        this.templateRepo = templateRepo;
        this.valueRepo = valueRepo;
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
    @Transactional
    public void deleteAll(int orgId) {
        templateRepo.deleteByOrgId(orgId);
    }

    @Transactional
    public void deleteAllByIds(int orgId,List<Long> fieldIds) {
        templateRepo.deleteByOrgIdAndFieldIds(orgId,fieldIds);
    }

    @Transactional
    public void saveData(Organization organization,EditedField[][] columns) throws Exception {
        Map<String,List<Value>> map=new HashMap<>();
        String key="",val="";
//        List<String> vals;
        List<Field> fields= new ArrayList<>();
        List<Value> values= new ArrayList<>();
        for (int i = 0; i < columns[0].length ; i++) {
            key=columns[0][i].getFieldName();
            values= new ArrayList<>();
            for (int j = 1; j < columns.length; j++) {
                Value value = new Value();
                value.setValueName(columns[j][i].getFieldName());
                values.add(value);
            }
            if(!map.containsKey(key))
                map.put(key,values);
            else
                throw new Exception("Duplicate column found!");

        }
//        valueRepo.deleteByFieldId()
        List<Long> fieldIds= organization.getFields().stream().map(Field::getFieldId).toList();
        valueRepo.deleteAllByFieldId(fieldIds);
        valueRepo.saveAll(values);
        templateRepo.saveAll(fields);
    }
}
