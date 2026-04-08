// package com.ex.emartUser.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;


// import java.util.HashMap;
// import java.util.Map;

// import org.slf4j.Logger; // Logger
// import org.slf4j.LoggerFactory; // Logger

// @Service
// public class UserServiceImpl implements UserService {
    
//     // Logger 객체 생성
//     private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

//     @Autowired
//     private UserSqlMapper userSqlMapper;


//     // ✅ registerUser 메서드 구현 추가

//     /* ---------- 회원가입 전체 처리 ---------- */
// @Override
// @Transactional // DB 반영을 위해 반드시 추가
// public Map<String, Object> registerUser(UserDto userDto) {
//    Map<String, Object> response = new HashMap<>();
    
//     try {
//         // Integer로 받기 (MyBatis가 실행 결과인 1을 Integer로 변환해서 줍니다)
//         Integer result = userSqlMapper.createUser(userDto); 

//         // null 체크를 포함하는 것이 더 안전합니다 (Integer는 null일 수 있으므로)
//         if (result != null && result > 0) {
//             response.put("success", true);
//             response.put("message", "회원가입 완료");
//         } else {
//             response.put("success", false);
//             response.put("message", "DB 저장 실패");
//         }
//     } catch (Exception e) {
//         response.put("success", false);
//         response.put("message", "에러: " + e.getMessage());
//     }
//     return response;
// }


//     // ✅ 추가된 validatePasswords 메서드 구현
//     @Override
//     public boolean validatePasswords(String password, String confirmPassword) {
//         // 1. null 체크
//         if (password == null || confirmPassword == null) {
//             return false;
//         }
//         // 3. 비밀번호 일치 여부 확인
//         return password.equals(confirmPassword);
//     }
// }
