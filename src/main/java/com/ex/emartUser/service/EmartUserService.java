package com.ex.emartUser.service;

import java.util.Map;

import com.ex.emartUser.dto.EmartUserDTO;

public interface EmartUserService {

        Map<String, Object> insertPlatformUser(EmartUserDTO emartUserDTO);

       Map<String, Object> EmartUserGetByIdAndPassword(EmartUserDTO emartUserDTO);
}
