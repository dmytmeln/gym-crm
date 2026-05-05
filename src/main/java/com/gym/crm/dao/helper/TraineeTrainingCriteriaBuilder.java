package com.gym.crm.dao.helper;

import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.entity.Trainee_;
import com.gym.crm.entity.Trainer_;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.TrainingType_;
import com.gym.crm.entity.Training_;
import com.gym.crm.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraineeTrainingCriteriaBuilder extends TrainingCriteriaBuilder<TraineeTrainingSearchFilter> {

    @Override
    protected void addSpecificFilters(CriteriaBuilder cb, Root<Training> root, TraineeTrainingSearchFilter criteria, List<Predicate> predicates) {
        addTrainingTypeIdFilter(cb, root, criteria, predicates);
    }

    @Override
    protected Join<?, User> getTargetUserJoin(Root<Training> root) {
        return root.join(Training_.TRAINEE, JoinType.INNER).join(Trainee_.USER, JoinType.INNER);
    }

    @Override
    protected String getPartnerName(TraineeTrainingSearchFilter criteria) {
        return criteria.getTrainerName();
    }

    @Override
    protected Join<?, User> getPartnerUserJoin(Root<Training> root) {
        return root.join(Training_.TRAINER, JoinType.INNER).join(Trainer_.USER, JoinType.INNER);
    }

    private void addTrainingTypeIdFilter(CriteriaBuilder cb, Root<Training> root, TraineeTrainingSearchFilter criteria, List<Predicate> predicates) {
        if (criteria.getTrainingTypeId() == null) {
            return;
        }

        Join<Training, TrainingType> trainingTypeJoin = root.join(Training_.TRAINING_TYPE, JoinType.INNER);
        predicates.add(cb.equal(trainingTypeJoin.get(TrainingType_.ID), criteria.getTrainingTypeId()));
    }

}