package com.codebloom.cineman.common.converter;

import com.codebloom.cineman.common.enums.Method;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MethodCodeConverter implements AttributeConverter<Method, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Method attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public Method convertToEntityAttribute(Integer dbData) {
        return Method.fromCode(dbData);
    }
}
