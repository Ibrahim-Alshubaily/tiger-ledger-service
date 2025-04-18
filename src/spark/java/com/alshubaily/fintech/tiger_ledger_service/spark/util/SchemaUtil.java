package com.alshubaily.fintech.tiger_ledger_service.spark.util;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Map;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class SchemaUtil {

    private static final Map<Class<?>, DataType> typeMapping =
            Map.ofEntries(
                    Map.entry(BigInteger.class, DataTypes.createDecimalType(38, 0)),
                    Map.entry(Long.TYPE, DataTypes.LongType),
                    Map.entry(Long.class, DataTypes.LongType),
                    Map.entry(Integer.TYPE, DataTypes.IntegerType),
                    Map.entry(Integer.class, DataTypes.IntegerType),
                    Map.entry(Timestamp.class, DataTypes.TimestampType));

    public static StructType schemaFromRecord(Class<?> recordClass) {
        StructType schema = new StructType();
        for (var field : recordClass.getDeclaredFields()) {
            DataType sparkType = typeMapping.getOrDefault(field.getType(), DataTypes.StringType);
            schema = schema.add(field.getName(), sparkType, true);
        }
        return schema;
    }
}
