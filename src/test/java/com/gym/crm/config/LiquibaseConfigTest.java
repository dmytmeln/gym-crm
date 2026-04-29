package com.gym.crm.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {DatasourceConfig.class, LiquibaseConfig.class, HibernateConfig.class})
@TestPropertySource(value = "classpath:application-test.yml", factory = YamlPropertySourceFactory.class)
class LiquibaseConfigTest {

    private static final String TABLE_USER = "user";
    private static final String TABLE_TRAINING_TYPE = "training_type";
    private static final String TABLE_TRAINEE = "trainee";
    private static final String TABLE_TRAINER = "trainer";
    private static final String TABLE_TRAINING = "training";
    private static final String TABLE_TRAINEE_TRAINER = "trainee_trainer";

    @Autowired
    private DataSource dataSource;

    @ParameterizedTest
    @ValueSource(strings = {TABLE_USER, TABLE_TRAINING_TYPE, TABLE_TRAINEE, TABLE_TRAINER, TABLE_TRAINING, TABLE_TRAINEE_TRAINER})
    void shouldCreateTable(String tableName) throws SQLException {
        assertThat(tableExists(tableName))
                .as("Table %s should exist", tableName)
                .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "first_name", "last_name", "username", "password", "is_active"})
    void shouldCreateUserTableColumns(String columnName) throws SQLException {
        assertTableColumnExists(TABLE_USER, columnName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "training_type_name"})
    void shouldCreateTrainingTypeTableColumns(String columnName) throws SQLException {
        assertTableColumnExists(TABLE_TRAINING_TYPE, columnName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "date_of_birth", "address", "user_id"})
    void shouldCreateTraineeTableColumns(String columnName) throws SQLException {
        assertTableColumnExists(TABLE_TRAINEE, columnName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "specialization_id", "user_id"})
    void shouldCreateTrainerTableColumns(String columnName) throws SQLException {
        assertTableColumnExists(TABLE_TRAINER, columnName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "trainee_id", "trainer_id", "training_name", "training_type_id", "training_duration", "training_date"})
    void shouldCreateTrainingTableColumns(String columnName) throws SQLException {
        assertTableColumnExists(TABLE_TRAINING, columnName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"trainee_id", "trainer_id"})
    void shouldCreateTraineeTrainerTableColumns(String columnName) throws SQLException {
        assertTableColumnExists(TABLE_TRAINEE_TRAINER, columnName);
    }

    @ParameterizedTest
    @CsvSource({
            "training_type, 3",
            "user, 6",
            "trainee, 3",
            "trainer, 3",
            "training, 4",
            "trainee_trainer, 3"
    })
    void shouldHaveCorrectRowCounts(String tableName, int expectedCount) throws SQLException {
        assertThat(tableRowCount(tableName))
                .as("Table %s should have %d rows", tableName, expectedCount)
                .isEqualTo(expectedCount);
    }

    @Test
    void shouldContainTrainingTypes() throws SQLException {
        List<String> actual = getColumnValues(TABLE_TRAINING_TYPE, "training_type_name");
        assertThat(actual).containsExactlyInAnyOrder("CARDIO", "STRENGTH", "YOGA");
    }

    @Test
    void shouldContainUsers() throws SQLException {
        List<String> actual = getColumnValues(TABLE_USER, "username");
        assertThat(actual).contains("liam.miller", "sophia.wilson", "bob.wilson", "marcus.stone");
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            return tableExists(metaData, tableName.toUpperCase()) || tableExists(metaData, tableName.toLowerCase());
        }
    }

    private boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            return columnExists(metaData, tableName.toUpperCase(), columnName.toUpperCase()) ||
                    columnExists(metaData, tableName.toLowerCase(), columnName.toLowerCase());
        }
    }

    private boolean columnExists(DatabaseMetaData metaData, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = metaData.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }

    private int tableRowCount(String tableName) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(String.format("SELECT COUNT(*) FROM %s", tableName))) {
            rs.next();

            return rs.getInt(1);
        }
    }

    private List<String> getColumnValues(String tableName, String columnName) throws SQLException {
        List<String> values = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(String.format("SELECT %s FROM %s", columnName, tableName))) {
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        }

        return values;
    }

    private void assertTableColumnExists(String tableUser, String columnName) throws SQLException {
        assertThat(columnExists(tableUser, columnName))
                .as("Column %s in table %s should exist", columnName, tableUser)
                .isTrue();
    }

}