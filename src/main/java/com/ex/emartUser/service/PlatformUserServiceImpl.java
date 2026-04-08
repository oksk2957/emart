// package com.ex.Platform.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;



// import java.util.Map;

// @Service
// public class PlatformUserServiceImpl implements EmartUserService {

//     @Autowired
//     private PlatformUserSqlMapper platformUserSqlMapper;

//     @Transactional
//     /** DTO 를 그대로 전달 */

//     @Override
//     public PlatformAdminUserDto getAdminUserByAccountId(Integer accountId) {
//         return platformUserSqlMapper.findByAccountId(accountId);
//     }

//     @Override
//  public void insertPlatformUser(PlatformAdminUserDto emartUserDTO) {
//         int rows = platformUserSqlMapper.insertPlatformUserMapper(emartUserDTO);
        
//         if (rows != 1) {
//             throw new IllegalStateException("관리자 계정 등록에 실패했습니다. (영향을 받은 행 없음)");
//         }
//     }

// }