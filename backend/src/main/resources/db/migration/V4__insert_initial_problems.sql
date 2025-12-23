SET @BASE_PATH = '${BASE_PATH}';


-- ================= Problem 1 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Sum of Two Numbers', 'Two integers a b', 'Single integer', 'Read two integers a and b and output their sum.', '', 1000, 64, 'APPROVED', 100, 'a,b=map(int,input().split())\nprint(a+b)', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (1, CONCAT(@BASE_PATH,'/1/testcase_1_input.txt'), CONCAT(@BASE_PATH,'/1/testcase_1_output.txt'), true, LAST_INSERT_ID()),
                                                                             (2, CONCAT(@BASE_PATH,'/1/testcase_2_input.txt'), CONCAT(@BASE_PATH,'/1/testcase_2_output.txt'), true, LAST_INSERT_ID()),
                                                                             (3, CONCAT(@BASE_PATH,'/1/testcase_3_input.txt'), CONCAT(@BASE_PATH,'/1/testcase_3_output.txt'), true, LAST_INSERT_ID()),
                                                                             (4, CONCAT(@BASE_PATH,'/1/testcase_4_input.txt'), CONCAT(@BASE_PATH,'/1/testcase_4_output.txt'), true, LAST_INSERT_ID()),
                                                                             (5, CONCAT(@BASE_PATH,'/1/testcase_5_input.txt'), CONCAT(@BASE_PATH,'/1/testcase_5_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 2 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Maximum of Three Numbers', 'Three integers a b c', 'Single integer', 'Given three integers, print the maximum.', '', 1000, 64, 'APPROVED', 2000, 'print(max(map(int,input().split())))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (6, CONCAT(@BASE_PATH,'/2/testcase_6_input.txt'), CONCAT(@BASE_PATH,'/2/testcase_6_output.txt'), true, LAST_INSERT_ID()),
                                                                             (7, CONCAT(@BASE_PATH,'/2/testcase_7_input.txt'), CONCAT(@BASE_PATH,'/2/testcase_7_output.txt'), true, LAST_INSERT_ID()),
                                                                             (8, CONCAT(@BASE_PATH,'/2/testcase_8_input.txt'), CONCAT(@BASE_PATH,'/2/testcase_8_output.txt'), true, LAST_INSERT_ID()),
                                                                             (9, CONCAT(@BASE_PATH,'/2/testcase_9_input.txt'), CONCAT(@BASE_PATH,'/2/testcase_9_output.txt'), true, LAST_INSERT_ID()),
                                                                             (10, CONCAT(@BASE_PATH,'/2/testcase_10_input.txt'), CONCAT(@BASE_PATH,'/2/testcase_10_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 3 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Even or Odd', 'Single integer n', 'String "Even" or "Odd"', 'Print whether the given integer n is even or odd.', '', 1000, 64, 'APPROVED', 100, 'n=int(input())\nprint("Even" if n%2==0 else "Odd")', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (11, CONCAT(@BASE_PATH,'/3/testcase_11_input.txt'), CONCAT(@BASE_PATH,'/3/testcase_11_output.txt'), true, LAST_INSERT_ID()),
                                                                             (12, CONCAT(@BASE_PATH,'/3/testcase_12_input.txt'), CONCAT(@BASE_PATH,'/3/testcase_12_output.txt'), true, LAST_INSERT_ID()),
                                                                             (13, CONCAT(@BASE_PATH,'/3/testcase_13_input.txt'), CONCAT(@BASE_PATH,'/3/testcase_13_output.txt'), true, LAST_INSERT_ID()),
                                                                             (14, CONCAT(@BASE_PATH,'/3/testcase_14_input.txt'), CONCAT(@BASE_PATH,'/3/testcase_14_output.txt'), true, LAST_INSERT_ID()),
                                                                             (15, CONCAT(@BASE_PATH,'/3/testcase_15_input.txt'), CONCAT(@BASE_PATH,'/3/testcase_15_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 4 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Sum of Array', 'First line n, second line n integers', 'Single integer', 'Calculate the sum of all integers in an array.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\narr=list(map(int,input().split()))\nprint(sum(arr))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH'),(LAST_INSERT_ID(),'DATA_STRUCTURES');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (16, CONCAT(@BASE_PATH,'/4/testcase_16_input.txt'), CONCAT(@BASE_PATH,'/4/testcase_16_output.txt'), true, LAST_INSERT_ID()),
                                                                             (17, CONCAT(@BASE_PATH,'/4/testcase_17_input.txt'), CONCAT(@BASE_PATH,'/4/testcase_17_output.txt'), true, LAST_INSERT_ID()),
                                                                             (18, CONCAT(@BASE_PATH,'/4/testcase_18_input.txt'), CONCAT(@BASE_PATH,'/4/testcase_18_output.txt'), true, LAST_INSERT_ID()),
                                                                             (19, CONCAT(@BASE_PATH,'/4/testcase_19_input.txt'), CONCAT(@BASE_PATH,'/4/testcase_19_output.txt'), true, LAST_INSERT_ID()),
                                                                             (20, CONCAT(@BASE_PATH,'/4/testcase_20_input.txt'), CONCAT(@BASE_PATH,'/4/testcase_20_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 5 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Factorial', 'Single integer n', 'Single integer', 'Compute the factorial of n.', '', 1000, 64, 'APPROVED', 300, 'import math\nn=int(input())\nprint(math.factorial(n))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (21, CONCAT(@BASE_PATH,'/5/testcase_21_input.txt'), CONCAT(@BASE_PATH,'/5/testcase_21_output.txt'), true, LAST_INSERT_ID()),
                                                                             (22, CONCAT(@BASE_PATH,'/5/testcase_22_input.txt'), CONCAT(@BASE_PATH,'/5/testcase_22_output.txt'), true, LAST_INSERT_ID()),
                                                                             (23, CONCAT(@BASE_PATH,'/5/testcase_23_input.txt'), CONCAT(@BASE_PATH,'/5/testcase_23_output.txt'), true, LAST_INSERT_ID()),
                                                                             (24, CONCAT(@BASE_PATH,'/5/testcase_24_input.txt'), CONCAT(@BASE_PATH,'/5/testcase_24_output.txt'), true, LAST_INSERT_ID()),
                                                                             (25, CONCAT(@BASE_PATH,'/5/testcase_25_input.txt'), CONCAT(@BASE_PATH,'/5/testcase_25_output.txt'), true, LAST_INSERT_ID());
-- ================= Problem 6 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'GCD of Two Numbers', 'Two integers a b', 'Single integer', 'Compute the greatest common divisor (GCD) of two numbers.', '', 1000, 64, 'APPROVED', 200, 'import math\na,b=map(int,input().split())\nprint(math.gcd(a,b))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (26, CONCAT(@BASE_PATH,'/6/testcase_26_input.txt'), CONCAT(@BASE_PATH,'/6/testcase_26_output.txt'), true, LAST_INSERT_ID()),
                                                                             (27, CONCAT(@BASE_PATH,'/6/testcase_27_input.txt'), CONCAT(@BASE_PATH,'/6/testcase_27_output.txt'), true, LAST_INSERT_ID()),
                                                                             (28, CONCAT(@BASE_PATH,'/6/testcase_28_input.txt'), CONCAT(@BASE_PATH,'/6/testcase_28_output.txt'), true, LAST_INSERT_ID()),
                                                                             (29, CONCAT(@BASE_PATH,'/6/testcase_29_input.txt'), CONCAT(@BASE_PATH,'/6/testcase_29_output.txt'), true, LAST_INSERT_ID()),
                                                                             (30, CONCAT(@BASE_PATH,'/6/testcase_30_input.txt'), CONCAT(@BASE_PATH,'/6/testcase_30_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 7 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Count Vowels', 'Single string s', 'Single integer', 'Count the number of vowels in the string.', '', 1000, 64, 'APPROVED', 200, 's=input()\nvowels="aeiouAEIOU"\nprint(sum(1 for c in s if c in vowels))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'STRINGS');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (31, CONCAT(@BASE_PATH,'/7/testcase_31_input.txt'), CONCAT(@BASE_PATH,'/7/testcase_31_output.txt'), true, LAST_INSERT_ID()),
                                                                             (32, CONCAT(@BASE_PATH,'/7/testcase_32_input.txt'), CONCAT(@BASE_PATH,'/7/testcase_32_output.txt'), true, LAST_INSERT_ID()),
                                                                             (33, CONCAT(@BASE_PATH,'/7/testcase_33_input.txt'), CONCAT(@BASE_PATH,'/7/testcase_33_output.txt'), true, LAST_INSERT_ID()),
                                                                             (34, CONCAT(@BASE_PATH,'/7/testcase_34_input.txt'), CONCAT(@BASE_PATH,'/7/testcase_34_output.txt'), true, LAST_INSERT_ID()),
                                                                             (35, CONCAT(@BASE_PATH,'/7/testcase_35_input.txt'), CONCAT(@BASE_PATH,'/7/testcase_35_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 8 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Reverse String', 'Single string s', 'Single string', 'Print the reverse of the given string.', '', 1000, 64, 'APPROVED', 200, 'print(input()[::-1])', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'STRINGS');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (36, CONCAT(@BASE_PATH,'/8/testcase_36_input.txt'), CONCAT(@BASE_PATH,'/8/testcase_36_output.txt'), true, LAST_INSERT_ID()),
                                                                             (37, CONCAT(@BASE_PATH,'/8/testcase_37_input.txt'), CONCAT(@BASE_PATH,'/8/testcase_37_output.txt'), true, LAST_INSERT_ID()),
                                                                             (38, CONCAT(@BASE_PATH,'/8/testcase_38_input.txt'), CONCAT(@BASE_PATH,'/8/testcase_38_output.txt'), true, LAST_INSERT_ID()),
                                                                             (39, CONCAT(@BASE_PATH,'/8/testcase_39_input.txt'), CONCAT(@BASE_PATH,'/8/testcase_39_output.txt'), true, LAST_INSERT_ID()),
                                                                             (40, CONCAT(@BASE_PATH,'/8/testcase_40_input.txt'), CONCAT(@BASE_PATH,'/8/testcase_40_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 9 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Sum of Digits', 'Single integer n', 'Single integer', 'Print the sum of digits of the number n.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\nprint(sum(map(int,str(n))))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (41, CONCAT(@BASE_PATH,'/9/testcase_41_input.txt'), CONCAT(@BASE_PATH,'/9/testcase_41_output.txt'), true, LAST_INSERT_ID()),
                                                                             (42, CONCAT(@BASE_PATH,'/9/testcase_42_input.txt'), CONCAT(@BASE_PATH,'/9/testcase_42_output.txt'), true, LAST_INSERT_ID()),
                                                                             (43, CONCAT(@BASE_PATH,'/9/testcase_43_input.txt'), CONCAT(@BASE_PATH,'/9/testcase_43_output.txt'), true, LAST_INSERT_ID()),
                                                                             (44, CONCAT(@BASE_PATH,'/9/testcase_44_input.txt'), CONCAT(@BASE_PATH,'/9/testcase_44_output.txt'), true, LAST_INSERT_ID()),
                                                                             (45, CONCAT(@BASE_PATH,'/9/testcase_45_input.txt'), CONCAT(@BASE_PATH,'/9/testcase_45_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 10 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Palindrome Check', 'Single string s', 'Yes or No', 'Check if the string is a palindrome.', '', 1000, 64, 'APPROVED', 200, 's=input()\nprint("Yes" if s==s[::-1] else "No")', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'STRINGS');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (46, CONCAT(@BASE_PATH,'/10/testcase_46_input.txt'), CONCAT(@BASE_PATH,'/10/testcase_46_output.txt'), true, LAST_INSERT_ID()),
                                                                             (47, CONCAT(@BASE_PATH,'/10/testcase_47_input.txt'), CONCAT(@BASE_PATH,'/10/testcase_47_output.txt'), true, LAST_INSERT_ID()),
                                                                             (48, CONCAT(@BASE_PATH,'/10/testcase_48_input.txt'), CONCAT(@BASE_PATH,'/10/testcase_48_output.txt'), true, LAST_INSERT_ID()),
                                                                             (49, CONCAT(@BASE_PATH,'/10/testcase_49_input.txt'), CONCAT(@BASE_PATH,'/10/testcase_49_output.txt'), true, LAST_INSERT_ID()),
                                                                             (50, CONCAT(@BASE_PATH,'/10/testcase_50_input.txt'), CONCAT(@BASE_PATH,'/10/testcase_50_output.txt'), true, LAST_INSERT_ID());
-- ================= Problem 11 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Sum of Array', 'First line n, second line n integers', 'Single integer', 'Compute the sum of all elements in the array.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\na=list(map(int,input().split()))\nprint(sum(a))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'DATA_STRUCTURES');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (51, CONCAT(@BASE_PATH,'/11/testcase_51_input.txt'), CONCAT(@BASE_PATH,'/11/testcase_51_output.txt'), true, LAST_INSERT_ID()),
                                                                             (52, CONCAT(@BASE_PATH,'/11/testcase_52_input.txt'), CONCAT(@BASE_PATH,'/11/testcase_52_output.txt'), true, LAST_INSERT_ID()),
                                                                             (53, CONCAT(@BASE_PATH,'/11/testcase_53_input.txt'), CONCAT(@BASE_PATH,'/11/testcase_53_output.txt'), true, LAST_INSERT_ID()),
                                                                             (54, CONCAT(@BASE_PATH,'/11/testcase_54_input.txt'), CONCAT(@BASE_PATH,'/11/testcase_54_output.txt'), true, LAST_INSERT_ID()),
                                                                             (55, CONCAT(@BASE_PATH,'/11/testcase_55_input.txt'), CONCAT(@BASE_PATH,'/11/testcase_55_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 12 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Find Maximum', 'First line n, second line n integers', 'Single integer', 'Find the maximum number in the array.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\na=list(map(int,input().split()))\nprint(max(a))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'DATA_STRUCTURES');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (56, CONCAT(@BASE_PATH,'/12/testcase_56_input.txt'), CONCAT(@BASE_PATH,'/12/testcase_56_output.txt'), true, LAST_INSERT_ID()),
                                                                             (57, CONCAT(@BASE_PATH,'/12/testcase_57_input.txt'), CONCAT(@BASE_PATH,'/12/testcase_57_output.txt'), true, LAST_INSERT_ID()),
                                                                             (58, CONCAT(@BASE_PATH,'/12/testcase_58_input.txt'), CONCAT(@BASE_PATH,'/12/testcase_58_output.txt'), true, LAST_INSERT_ID()),
                                                                             (59, CONCAT(@BASE_PATH,'/12/testcase_59_input.txt'), CONCAT(@BASE_PATH,'/12/testcase_59_output.txt'), true, LAST_INSERT_ID()),
                                                                             (60, CONCAT(@BASE_PATH,'/12/testcase_60_input.txt'), CONCAT(@BASE_PATH,'/12/testcase_60_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 13 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Count Even Numbers', 'First line n, second line n integers', 'Single integer', 'Count the number of even numbers in the array.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\na=list(map(int,input().split()))\nprint(sum(1 for x in a if x%2==0))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'DATA_STRUCTURES');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (61, CONCAT(@BASE_PATH,'/13/testcase_61_input.txt'), CONCAT(@BASE_PATH,'/13/testcase_61_output.txt'), true, LAST_INSERT_ID()),
                                                                             (62, CONCAT(@BASE_PATH,'/13/testcase_62_input.txt'), CONCAT(@BASE_PATH,'/13/testcase_62_output.txt'), true, LAST_INSERT_ID()),
                                                                             (63, CONCAT(@BASE_PATH,'/13/testcase_63_input.txt'), CONCAT(@BASE_PATH,'/13/testcase_63_output.txt'), true, LAST_INSERT_ID()),
                                                                             (64, CONCAT(@BASE_PATH,'/13/testcase_64_input.txt'), CONCAT(@BASE_PATH,'/13/testcase_64_output.txt'), true, LAST_INSERT_ID()),
                                                                             (65, CONCAT(@BASE_PATH,'/13/testcase_65_input.txt'), CONCAT(@BASE_PATH,'/13/testcase_65_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 14 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Factorial', 'Single integer n', 'Single integer', 'Compute n! (factorial of n).', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\nf=1\nfor i in range(2,n+1): f*=i\nprint(f)', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (66, CONCAT(@BASE_PATH,'/14/testcase_66_input.txt'), CONCAT(@BASE_PATH,'/14/testcase_66_output.txt'), true, LAST_INSERT_ID()),
                                                                             (67, CONCAT(@BASE_PATH,'/14/testcase_67_input.txt'), CONCAT(@BASE_PATH,'/14/testcase_67_output.txt'), true, LAST_INSERT_ID()),
                                                                             (68, CONCAT(@BASE_PATH,'/14/testcase_68_input.txt'), CONCAT(@BASE_PATH,'/14/testcase_68_output.txt'), true, LAST_INSERT_ID()),
                                                                             (69, CONCAT(@BASE_PATH,'/14/testcase_69_input.txt'), CONCAT(@BASE_PATH,'/14/testcase_69_output.txt'), true, LAST_INSERT_ID()),
                                                                             (70, CONCAT(@BASE_PATH,'/14/testcase_70_input.txt'), CONCAT(@BASE_PATH,'/14/testcase_70_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 15 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Fibonacci Number', 'Single integer n', 'Single integer', 'Print the nth Fibonacci number.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\na,b=0,1\nfor _ in range(n): a,b=b,a+b\nprint(a)', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'DP');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (71, CONCAT(@BASE_PATH,'/15/testcase_71_input.txt'), CONCAT(@BASE_PATH,'/15/testcase_71_output.txt'), true, LAST_INSERT_ID()),
                                                                             (72, CONCAT(@BASE_PATH,'/15/testcase_72_input.txt'), CONCAT(@BASE_PATH,'/15/testcase_72_output.txt'), true, LAST_INSERT_ID()),
                                                                             (73, CONCAT(@BASE_PATH,'/15/testcase_73_input.txt'), CONCAT(@BASE_PATH,'/15/testcase_73_output.txt'), true, LAST_INSERT_ID()),
                                                                             (74, CONCAT(@BASE_PATH,'/15/testcase_74_input.txt'), CONCAT(@BASE_PATH,'/15/testcase_74_output.txt'), true, LAST_INSERT_ID()),
                                                                             (75, CONCAT(@BASE_PATH,'/15/testcase_75_input.txt'), CONCAT(@BASE_PATH,'/15/testcase_75_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 16 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Check Prime', 'Single integer n', 'Yes or No', 'Determine if the number n is prime.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\ndef is_prime(x):\n if x<2: return False\n for i in range(2,int(x**0.5)+1):\n  if x%i==0: return False\n return True\nprint("Yes" if is_prime(n) else "No")', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (76, CONCAT(@BASE_PATH,'/16/testcase_76_input.txt'), CONCAT(@BASE_PATH,'/16/testcase_76_output.txt'), true, LAST_INSERT_ID()),
                                                                             (77, CONCAT(@BASE_PATH,'/16/testcase_77_input.txt'), CONCAT(@BASE_PATH,'/16/testcase_77_output.txt'), true, LAST_INSERT_ID()),
                                                                             (78, CONCAT(@BASE_PATH,'/16/testcase_78_input.txt'), CONCAT(@BASE_PATH,'/16/testcase_78_output.txt'), true, LAST_INSERT_ID()),
                                                                             (79, CONCAT(@BASE_PATH,'/16/testcase_79_input.txt'), CONCAT(@BASE_PATH,'/16/testcase_79_output.txt'), true, LAST_INSERT_ID()),
                                                                             (80, CONCAT(@BASE_PATH,'/16/testcase_80_input.txt'), CONCAT(@BASE_PATH,'/16/testcase_80_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 17 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Reverse Integer', 'Single integer n', 'Single integer', 'Reverse the digits of an integer n.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\nprint(int(str(n)[::-1]))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (81, CONCAT(@BASE_PATH,'/17/testcase_81_input.txt'), CONCAT(@BASE_PATH,'/17/testcase_81_output.txt'), true, LAST_INSERT_ID()),
                                                                             (82, CONCAT(@BASE_PATH,'/17/testcase_82_input.txt'), CONCAT(@BASE_PATH,'/17/testcase_82_output.txt'), true, LAST_INSERT_ID()),
                                                                             (83, CONCAT(@BASE_PATH,'/17/testcase_83_input.txt'), CONCAT(@BASE_PATH,'/17/testcase_83_output.txt'), true, LAST_INSERT_ID()),
                                                                             (84, CONCAT(@BASE_PATH,'/17/testcase_84_input.txt'), CONCAT(@BASE_PATH,'/17/testcase_84_output.txt'), true, LAST_INSERT_ID()),
                                                                             (85, CONCAT(@BASE_PATH,'/17/testcase_85_input.txt'), CONCAT(@BASE_PATH,'/17/testcase_85_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 18 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Count Words', 'Single string s', 'Single integer', 'Count the number of words in the string s.', '', 1000, 64, 'APPROVED', 200, 's=input()\nprint(len(s.split()))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'STRINGS');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (86, CONCAT(@BASE_PATH,'/18/testcase_86_input.txt'), CONCAT(@BASE_PATH,'/18/testcase_86_output.txt'), true, LAST_INSERT_ID()),
                                                                             (87, CONCAT(@BASE_PATH,'/18/testcase_87_input.txt'), CONCAT(@BASE_PATH,'/18/testcase_87_output.txt'), true, LAST_INSERT_ID()),
                                                                             (88, CONCAT(@BASE_PATH,'/18/testcase_88_input.txt'), CONCAT(@BASE_PATH,'/18/testcase_88_output.txt'), true, LAST_INSERT_ID()),
                                                                             (89, CONCAT(@BASE_PATH,'/18/testcase_89_input.txt'), CONCAT(@BASE_PATH,'/18/testcase_89_output.txt'), true, LAST_INSERT_ID()),
                                                                             (90, CONCAT(@BASE_PATH,'/18/testcase_90_input.txt'), CONCAT(@BASE_PATH,'/18/testcase_90_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 19 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Sum of Squares', 'First line n, second line n integers', 'Single integer', 'Compute the sum of squares of all elements.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\na=list(map(int,input().split()))\nprint(sum(x*x for x in a))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'MATH');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (91, CONCAT(@BASE_PATH,'/19/testcase_91_input.txt'), CONCAT(@BASE_PATH,'/19/testcase_91_output.txt'), true, LAST_INSERT_ID()),
                                                                             (92, CONCAT(@BASE_PATH,'/19/testcase_92_input.txt'), CONCAT(@BASE_PATH,'/19/testcase_92_output.txt'), true, LAST_INSERT_ID()),
                                                                             (93, CONCAT(@BASE_PATH,'/19/testcase_93_input.txt'), CONCAT(@BASE_PATH,'/19/testcase_93_output.txt'), true, LAST_INSERT_ID()),
                                                                             (94, CONCAT(@BASE_PATH,'/19/testcase_94_input.txt'), CONCAT(@BASE_PATH,'/19/testcase_94_output.txt'), true, LAST_INSERT_ID()),
                                                                             (95, CONCAT(@BASE_PATH,'/19/testcase_95_input.txt'), CONCAT(@BASE_PATH,'/19/testcase_95_output.txt'), true, LAST_INSERT_ID());

-- ================= Problem 20 =================
INSERT INTO problem
(submissions_count, title, input_format, output_format, statement, notes, time_limit, memory_limit, problem_status, rate, solution_code, language_version)
VALUES
    (0, 'Count Odd Numbers', 'First line n, second line n integers', 'Single integer', 'Count the number of odd numbers in the array.', '', 1000, 64, 'APPROVED', 200, 'n=int(input())\na=list(map(int,input().split()))\nprint(sum(1 for x in a if x%2==1))', 'PYTHON_3_8');
INSERT INTO problem_topics (problem_id, tags) VALUES
                                                  (LAST_INSERT_ID(),'IMPLEMENTATION'),(LAST_INSERT_ID(),'DATA_STRUCTURES');
INSERT INTO test_case (id, input_path, output_path, visible, problem_id) VALUES
                                                                             (96, CONCAT(@BASE_PATH,'/20/testcase_96_input.txt'), CONCAT(@BASE_PATH,'/20/testcase_96_output.txt'), true, LAST_INSERT_ID()),
                                                                             (97, CONCAT(@BASE_PATH,'/20/testcase_97_input.txt'), CONCAT(@BASE_PATH,'/20/testcase_97_output.txt'), true, LAST_INSERT_ID()),
                                                                             (98, CONCAT(@BASE_PATH,'/20/testcase_98_input.txt'), CONCAT(@BASE_PATH,'/20/testcase_98_output.txt'), true, LAST_INSERT_ID()),
                                                                             (99, CONCAT(@BASE_PATH,'/20/testcase_99_input.txt'), CONCAT(@BASE_PATH,'/20/testcase_99_output.txt'), true, LAST_INSERT_ID()),
                                                                             (100, CONCAT(@BASE_PATH,'/20/testcase_100_input.txt'), CONCAT(@BASE_PATH,'/20/testcase_100_output.txt'), true, LAST_INSERT_ID());
