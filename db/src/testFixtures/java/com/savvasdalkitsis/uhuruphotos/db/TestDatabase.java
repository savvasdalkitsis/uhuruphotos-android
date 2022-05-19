package com.savvasdalkitsis.uhuruphotos.db;

import com.savvasdalkitsis.uhuruphotos.db.auth.Token;
import com.savvasdalkitsis.uhuruphotos.db.entities.auth.TokenType;
import com.squareup.sqldelight.EnumColumnAdapter;
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class TestDatabase {

    public static Database getDb() throws IOException {
        String file = File.createTempFile("temp-db-", ".db").toURI().getRawPath();
        JdbcSqliteDriver driver = new JdbcSqliteDriver("jdbc:sqlite:" + file, new Properties());
        Database.Companion.getSchema().create(driver);
        return Database.Companion.invoke(
                driver,
                new Token.Adapter(new EnumColumnAdapter<>(new TokenType[]{}))
        );
    }
}
