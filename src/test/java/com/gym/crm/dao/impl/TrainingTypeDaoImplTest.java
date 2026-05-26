package com.gym.crm.dao.impl;

import com.gym.crm.entity.TrainingType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainingTypeConfigForDirectFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainingTypeDaoImplTest extends AbstractDaoTest<TrainingTypeDaoImpl> {

    private static final long EXISTING_ID = 1L;
    private static final String EXISTING_NAME = "CARDIO";
    private static final String NON_EXISTING_NAME = "NON_EXISTENT";

    @Test
    void shouldFindByNameWhenExists() {
        TrainingType expected = testDbClient.findTrainingType(EXISTING_ID);

        Optional<TrainingType> actual = dao.findByName(EXISTING_NAME);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison(getTrainingTypeConfigForDirectFields())
                .isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundByName() {
        Optional<TrainingType> actual = dao.findByName(NON_EXISTING_NAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullName() {
        assertThatThrownBy(() -> dao.findByName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Training type name cannot be null");
    }

    @Test
    void shouldFindAllTrainingTypes() {
        List<TrainingType> expected = testDbClient.findAllTrainingTypes();

        List<TrainingType> actual = dao.findAll();

        assertThat(actual)
                .hasSize(expected.size())
                .usingRecursiveComparison(getTrainingTypeConfigForDirectFields())
                .isEqualTo(expected);
    }

}
