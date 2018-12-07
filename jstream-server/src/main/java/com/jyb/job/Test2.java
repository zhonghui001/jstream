package com.jyb.job;

import com.jyb.config.Name;
import com.jyb.source.KafkaSource;

import java.lang.reflect.Field;
import java.util.Set;

import static org.reflections.ReflectionUtils.*;
public class Test2 {

    public static void main(String[] args) {

        Set<Field> fields = getAllFields(KafkaSource.KafkaSouceConfig.class, withAnnotation(Name.class));
        fields.stream().forEach(field -> {
            Name name = field.getAnnotation(Name.class);
            System.out.println(field.getName()+"----"+name.value());
        });

    }
}
