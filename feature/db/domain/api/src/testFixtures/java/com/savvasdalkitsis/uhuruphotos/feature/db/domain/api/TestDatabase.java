package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api;

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.Token;
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.auth.TokenType;
import com.squareup.sqldelight.EnumColumnAdapter;
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class TestDatabase {

    private TestDatabase() {
        // not to be instantiated
    }

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
