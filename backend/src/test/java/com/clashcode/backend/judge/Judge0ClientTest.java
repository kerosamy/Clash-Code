//package com.clashcode.backend.judge;
//
//import com.clashcode.backend.judge.Judge0.Judge0Client;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.client.RestTemplate;
//
//@ExtendWith(MockitoExtension.class)
//class Judge0ClientTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private Judge0Client judge0Client;
//
//    @BeforeEach
//    void setup() {
//        ReflectionTestUtils.setField(
//                judge0Client,
//                "JUDGE0_URL",
//                "http://judge0"
//        );
//    }
//
