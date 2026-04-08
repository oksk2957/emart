package com.ex.emartUser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.ex.emartUser.dto.EmartUserDTO;
import com.ex.emartUser.mapper.EmartUserMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmartUserServiceImpl implements EmartUserService {
 @Autowired
    private EmartUserMapper emartUserMapper;  // ✅ Mapper 주입
    @Override
    @Transactional
    public Map<String, Object> insertPlatformUser(EmartUserDTO emartUserDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // ✅ 실제 DB에 데이터 Insert (여기서 mapper 호출!)
            int rows = emartUserMapper.insertPlatformUserMapper(emartUserDTO);
            
            if (rows > 0) {
                response.put("success", true);
                response.put("message", "플랫폼 사용자 등록이 성공적으로 완료되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "사용자 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    @Override
    public Map<String, Object> EmartUserGetByIdAndPassword(EmartUserDTO emartUserDTO) {
        Map<String, Object> response = new HashMap<>();
        // DTO에서 값 꺼내기
        String userId = emartUserDTO.getUserId();
        String userPassword = emartUserDTO.getUserPassword();
        // Mapper 호출 - 사용자 조회
        EmartUserDTO user = emartUserMapper.findEmartUserByIdAndPassword(userId, userPassword);
        if (user != null) {
            // 로그인 성공
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("user", user);  // 사용자 정보도 함께 반환
        } else {
            // 로그인 실패 (사용자 없음 또는 비밀번호 불일치)
            // 1
            response.put("success", false);
            response.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return response;
    }
}
 

