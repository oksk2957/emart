package com.ex.emartUser.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ex.emartUser.dto.EmartUserDTO;

@Mapper
public interface EmartUserMapper {
    
       /** INSERT – DTO 를 그대로 넘긴다 */
    int insertPlatformUserMapper( EmartUserDTO emartUserDTO);

      //  findByAccountId(@Param("userId") Integer accountId);

      EmartUserDTO findEmartUserByIdAndPassword(
         @Param("userId") String userId, 
        @Param("userPassword") String userPassword);
}
