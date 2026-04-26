-- V6__insert_submissions.sql

-- ===================== USER 1 (kero) =====================
INSERT INTO submission (submitted_at, code, language_version, status, memory_taken, time_taken,
                        number_of_test_cases, number_of_passed_test_cases, number_of_current_test_case, user_id, problem_id)
VALUES
    ('2025-12-10 10:00:00', '#include <iostream>\nusing namespace std;\nint main(){int a,b;cin>>a>>b;cout<<a+b;}',
     'CPP_GCC_9_2', 'ACCEPTED', 8, 14, 5, 5, 5, 1, 1),

    ('2025-12-10 10:05:00', '#include <iostream>\nusing namespace std;\nint main(){int a,b;cin>>a>>b;cout<<a*b;}',
     'CPP_GCC_9_2', 'WRONG_ANSWER', 10, 16, 5, 2, 3, 1, 2),

    ('2025-12-10 10:10:00', '#include <iostream>\nusing namespace std;\nint main(){while(true){} return 0;}',
     'CPP_GCC_9_2', 'TIME_LIMIT_EXCEEDED', 6, 1000, 5, 0, 1, 1, 3);


-- ===================== USER 2 (john) =====================
INSERT INTO submission (submitted_at, code, language_version, status, memory_taken, time_taken,
                        number_of_test_cases, number_of_passed_test_cases, number_of_current_test_case, user_id, problem_id)
VALUES
    ('2025-12-10 11:00:00', '#include <iostream>\nusing namespace std;\nint main(){int x;cin>>x;cout<<x*2;}',
     'CPP_GCC_9_2', 'ACCEPTED', 9, 13, 5, 5, 5, 2, 4),

    ('2025-12-10 11:05:00', '#include <vector>\nusing namespace std;\nint main(){vector<int>v(1000000000);}',
     'CPP_GCC_9_2', 'MEMORY_LIMIT_EXCEEDED', 600, 1, 5, 0, 1, 2, 5),

    ('2025-12-10 11:10:00', '#include <iostream>\nusing namespace std;\nint main(){int x;cin>>x;cout<<x-5;}',
     'CPP_GCC_9_2', 'WRONG_ANSWER', 8, 17, 5, 3, 4, 2, 6);


-- ===================== USER 3 (caro) =====================
INSERT INTO submission (submitted_at, code, language_version, status, memory_taken, time_taken,
                        number_of_test_cases, number_of_passed_test_cases, number_of_current_test_case, user_id, problem_id)
VALUES
    ('2025-12-10 12:00:00', '#include <iostream>\nusing namespace std;\nint main(){int n;cin>>n;cout<<n+1;}',
     'CPP_GCC_9_2', 'ACCEPTED', 7, 11, 5, 5, 5, 3, 7),

    ('2025-12-10 12:05:00', 'int main(){while(true){} }',
     'CPP_GCC_9_2', 'TIME_LIMIT_EXCEEDED', 9, 1000, 5, 0, 1, 3, 8),

    ('2025-12-10 12:10:00', '#include <iostream>\nusing namespace std;\nint main(){int a,b;cin>>a>>b;cout<<a/b;}',
     'CPP_GCC_9_2', 'WRONG_ANSWER', 10, 15, 5, 1, 2, 3, 9);


-- ===================== USER 4 (miky) =====================
INSERT INTO submission (submitted_at, code, language_version, status, memory_taken, time_taken,
                        number_of_test_cases, number_of_passed_test_cases, number_of_current_test_case, user_id, problem_id)
VALUES
    ('2025-12-10 13:00:00', '#include <iostream>\nusing namespace std;\nint main(){int n;cin>>n;cout<<n*n;}',
     'CPP_GCC_9_2', 'ACCEPTED', 12, 16, 5, 5, 5, 4, 10),

    ('2025-12-10 13:05:00', '#include <vector>\nusing namespace std;\nint main(){vector<int>v(999999999);}',
     'CPP_GCC_9_2', 'MEMORY_LIMIT_EXCEEDED', 700, 1, 5, 0, 1, 4, 11),

    ('2025-12-10 13:10:00', '#include <iostream>\nusing namespace std;\nint main(){while(true){}}',
     'CPP_GCC_9_2', 'TIME_LIMIT_EXCEEDED', 11, 1000, 5, 0, 1, 4, 12);


-- ===================== USER 5 (jana) =====================
INSERT INTO submission (submitted_at, code, language_version, status, memory_taken, time_taken,
                        number_of_test_cases, number_of_passed_test_cases, number_of_current_test_case, user_id, problem_id)
VALUES
    ('2025-12-10 14:00:00', '#include <iostream>\nusing namespace std;\nint main(){int a,b;cin>>a>>b;cout<<a+b;}',
     'CPP_GCC_9_2', 'ACCEPTED', 9, 13, 5, 5, 5, 5, 13),

    ('2025-12-10 14:05:00', '#include <iostream>\nusing namespace std;\nint main(){int a,b;cin>>a>>b;cout<<a*b;}',
     'CPP_GCC_9_2', 'WRONG_ANSWER', 8, 15, 5, 2, 3, 5, 14),

    ('2025-12-10 14:10:00', '#include <vector>\nusing namespace std;\nint main(){vector<int>v(1000000000);} ',
     'CPP_GCC_9_2', 'MEMORY_LIMIT_EXCEEDED', 750, 1, 5, 0, 1, 5, 15);


-- ===================== USER 6 (mina) =====================
INSERT INTO submission (submitted_at, code, language_version, status, memory_taken, time_taken,
                        number_of_test_cases, number_of_passed_test_cases, number_of_current_test_case, user_id, problem_id)
VALUES
    ('2025-12-10 15:00:00', '#include <iostream>\nusing namespace std;\nint main(){int n;cin>>n;cout<<n*10;}',
     'CPP_GCC_9_2', 'ACCEPTED', 8, 12, 5, 5, 5, 6, 16),

    ('2025-12-10 15:05:00', 'int main(){while(true){} }',
     'CPP_GCC_9_2', 'TIME_LIMIT_EXCEEDED', 7, 1000, 5, 0, 1, 6, 17),

    ('2025-12-10 15:10:00', '#include <iostream>\nusing namespace std;\nint main(){int a,b;cin>>a>>b;cout<<a/b;}',
     'CPP_GCC_9_2', 'WRONG_ANSWER', 9, 14, 5, 3, 3, 6, 18);
