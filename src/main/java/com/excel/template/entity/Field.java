package com.excel.template.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "template")
public class Field {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "org_id", referencedColumnName = "org_id")
    private Organization organization;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id")
    private long fieldId;
    @Column(name = "field_name",nullable = false)
    private String fieldName;


    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }



    @Override
    public String toString() {
        return "Field{" +
                "organization=" + organization +
                ", fieldId=" + fieldId +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
