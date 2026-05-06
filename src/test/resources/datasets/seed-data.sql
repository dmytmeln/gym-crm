INSERT INTO training_type (id, training_type_name)
VALUES (1, 'CARDIO'),
       (2, 'STRENGTH'),
       (3, 'YOGA');

INSERT INTO user (id, first_name, last_name, username, password, is_active)
VALUES (1, 'Liam', 'Miller', 'liam.miller', '$2a$10$YBzS3XXJ9BUE6w9bh20gFedY20Ny5S8LuoTTzkhY9cIYp.1uViRhq', true),
       (2, 'Sophia', 'Wilson', 'sophia.wilson', '$2a$10$4LjEt.VXNoOg6uEmPWz2Nuqj28JoSpN0YvWAgNbXY8uflaYrsY2Qy', true),
       (3, 'Bob', 'Wilson', 'bob.wilson', '$2a$10$ZNfQh4hBskU5niD3P6gFne6cZF3Bv70wuReKJGWq3H8ibYOdrTIwG', false),
       (4, 'Marcus', 'Stone', 'marcus.stone', '$2a$10$2OrkRCw/bpKYyayM7.7.IuvbodPFfydDFT.ZIbVxzTqQPA5tmxeF6', true),
       (5, 'Sarah', 'Adams', 'sarah.adams', '$2a$10$RHgNzNa4yuuoeTtqrrHlHecvldGpHlIZ3eXwGZIhi9QkPmP0kUS7W', true),
       (6, 'Alex', 'Morgan', 'alex.morgan', '$2a$10$6wofb9OtA3h/1J1V7bGL4OYgzw.c4HPIgCBpi/9uheeupvZpoCE4K', true);

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