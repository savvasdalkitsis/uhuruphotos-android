package com.savvasdalkitsis.uhuruphotos.db;

import com.savvasdalkitsis.uhuruphotos.db.auth.Token;
import com.savvasdalkitsis.uhuruphotos.db.entities.auth.TokenType;
import com.squareup.sqldelight.EnumColumnAdapter;
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver;

import java.util.Properties;

public class TestDatabase {

    public static Database getDb() {
        JdbcSqliteDriver driver = new JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, new Properties());
        Database.Companion.getSchema().create(driver);
        return Database.Companion.invoke(
                driver,
                new Token.Adapter(new EnumColumnAdapter<>(new TokenType[]{}))
        );
    }
}
