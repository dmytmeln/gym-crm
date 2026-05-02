INSERT INTO training_type (id, training_type_name)
VALUES (1, 'CARDIO'),
       (2, 'STRENGTH'),
       (3, 'YOGA');

INSERT INTO user (id, first_name, last_name, username, password, is_active)
VALUES (1, 'Liam', 'Miller', 'liam.miller', 'pass123', true),
       (2, 'Sophia', 'Wilson', 'sophia.wilson', 'pass456', true),
       (3, 'Bob', 'Wilson', 'bob.wilson', 'pass789', false),
       (4, 'Marcus', 'Stone', 'marcus.stone', 'pass111', true),
       (5, 'Sarah', 'Adams', 'sarah.adams', 'pass222', true),
       (6, 'Alex', 'Morgan', 'alex.morgan', 'pass333', true);

INSERT INTO trainee (id, date_of_birth, address, user_id)
VALUES (1, '1990-05-15', 'NYC', 1),
       (2, '1992-08-20', NULL, 2);

INSERT INTO trainer (id, specialization_id, user_id)
VALUES (1, 1, 4),
       (2, 2, 5),
       (3, 3, 6);

INSERT INTO trainee_trainer (trainee_id, trainer_id)
VALUES (1, 1);

INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration)
VALUES (1, 1, 1, 'Morning HIIT', 1, '2025-01-15', 60),
       (2, 2, 2, 'Evening Weights', 2, '2025-01-16', 90);