package com.healthtrack.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
// appointment的ConsultationType是枚举类型，JAVA实体定义的(IN_PERSON, VIRTUAL)需要转换成数据库能存储的字符串类型('In-Person','Virtual')
@Converter(autoApply = true)
public class AppointmentConsultationTypeConverter implements AttributeConverter<Appointment.ConsultationType, String> {

    @Override
    public String convertToDatabaseColumn(Appointment.ConsultationType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public Appointment.ConsultationType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Appointment.ConsultationType.fromDbValue(dbData);
    }
}

