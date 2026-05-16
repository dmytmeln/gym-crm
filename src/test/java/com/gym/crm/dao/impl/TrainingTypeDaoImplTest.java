package com.gym.crm.dao.impl;

import com.gym.crm.entity.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainingTypeConfigForDirectFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(TrainingTypeDaoImpl.class)
class TrainingTypeDaoImplTest extends AbstractDaoTest<TrainingTypeDaoImpl> {

    private static final long EXISTING_ID = 1L;
    private static final String EXISTING_NAME = "CARDIO";
    private static final long NON_EXISTING_ID = 99999L;
    private static final String NON_EXISTING_NAME = "NON_EXISTENT";

    @Test
    void shouldFindByIdWhenExists() {
        TrainingType expected = testDbClient.findTrainingType(EXISTING_ID);

        Optional<TrainingType> actual = dao.findById(EXISTING_ID);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison(getTrainingTypeConfigForDirectFields())
                .isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        Optional<TrainingType> actual = dao.findById(NON_EXISTING_ID);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        assertThatThrownBy(() -> dao.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Training type ID cannot be null");
    }

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

}
