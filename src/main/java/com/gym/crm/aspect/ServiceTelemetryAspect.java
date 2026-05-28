package com.gym.crm.aspect;

import com.gym.crm.actuator.metrics.LoginCounter;
import com.gym.crm.actuator.metrics.TrainingCreatedCounter;
import com.gym.crm.actuator.metrics.UserRegistrationCounter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ServiceTelemetryAspect {

    private final UserRegistrationCounter registrationCounter;
    private final LoginCounter loginCounter;
    private final TrainingCreatedCounter trainingCounter;

    @Pointcut("execution(* com.gym.crm.service.TraineeService.createTrainee(..))")
    private void traineeRegistrationMethod() {
    }

    @Pointcut("execution(* com.gym.crm.service.TrainerService.createTrainer(..))")
    private void trainerRegistrationMethod() {
    }

    @Pointcut("execution(* com.gym.crm.service.AuthenticationService.login(..))")
    private void loginMethod() {
    }

    @Pointcut("execution(* com.gym.crm.service.TrainingService.createTraining(..))")
    private void trainingCreationMethod() {
    }

    @Around("traineeRegistrationMethod()")
    public Object profileTraineeRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Trainee result = (Trainee) joinPoint.proceed();
            registrationCounter.incrementTrainee(true);

            return result;
        } catch (Throwable t) {
            registrationCounter.incrementTrainee(false);
            throw t;
        }
    }

    @Around("trainerRegistrationMethod()")
    public Object profileTrainerRegistration(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Trainer result = (Trainer) joinPoint.proceed();
            registrationCounter.incrementTrainer(true);

            return result;
        } catch (Throwable t) {
            registrationCounter.incrementTrainer(false);
            throw t;
        }
    }

    @Around("loginMethod()")
    public Object profileLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            loginCounter.increment(true);

            return result;
        } catch (Throwable t) {
            loginCounter.increment(false);
            throw t;
        }
    }

    @Around("trainingCreationMethod()")
    public Object profileTrainingCreation(ProceedingJoinPoint joinPoint) throws Throwable {
        Training result = (Training) joinPoint.proceed();

        String trainingType = result.getTrainingType().getTrainingTypeName();
        trainingCounter.increment(trainingType);

        return result;
    }

}
