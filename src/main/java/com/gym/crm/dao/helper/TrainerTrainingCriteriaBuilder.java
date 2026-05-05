package com.gym.crm.dao.helper;

import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainee_;
import com.gym.crm.entity.Trainer_;
import com.gym.crm.entity.Training;
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
public class TrainerTrainingCriteriaBuilder extends TrainingCriteriaBuilder<TrainerTrainingSearchFilter> {

    @Override
    protected Join<?, User> getTargetUserJoin(Root<Training> root) {
        return root.join(Training_.TRAINER, JoinType.INNER).join(Trainer_.USER, JoinType.INNER);
    }

    @Override
    protected String getPartnerName(TrainerTrainingSearchFilter criteria) {
        return criteria.getTraineeName();
    }

    @Override
    protected Join<?, User> getPartnerUserJoin(Root<Training> root) {
        return root.join(Training_.TRAINEE, JoinType.INNER).join(Trainee_.USER, JoinType.INNER);
    }

    @Override
    protected void addSpecificFilters(CriteriaBuilder cb, Root<Training> root, TrainerTrainingSearchFilter criteria, List<Predicate> predicates) {
    }

}