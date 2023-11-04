package com.excel.template.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "data_values")
public class Value {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "value_id")
    private long valueId;
    @Column(name = "field_value")
    private String valueName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "field_id", referencedColumnName = "field_id")
    private Field field;

    public long getValueId() {
        return valueId;
    }

    public void setValueId(long valueId) {
        this.valueId = valueId;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "Value{" +
                "valueId=" + valueId +
                ", valueName='" + valueName + '\'' +
                ", field=" + field +
                '}';
    }
}
