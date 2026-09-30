INSERT INTO users
(id,first_name, last_name, email, password, phone_number, role, created_at,
 updated_at)
VALUES (1,'James', 'Anderson', 'james.anderson@gmail.com',
        '123Jamses&&',
        '0412345678',
        'CUSTOMER', NOW(),
        NOW()),

       (2,'Emily', 'Wilson', 'emily.wilson@gmail.com',
        'PPOWW@$$',
        '0423456789',
        'CUSTOMER', NOW(),
        NOW()),

       (3,'Michael', 'Brown', 'michael.brown@gmail.com',
        'Mic24(#',
        '0434567890',
        'CUSTOMER', NOW(),
        NOW());