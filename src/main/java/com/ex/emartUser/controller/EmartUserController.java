package com.ex.emartUser.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ex.emartUser.dto.EmartUserDTO;
import com.ex.emartUser.service.EmartUserService;
import com.ex.jwt.TokenProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("emartUser")
public class EmartUserController {

    // Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(EmartUserController.class);

    @Autowired
    private EmartUserService emartUserService;

        @Autowired
    private TokenProvider tokenProvider;


    

    @RequestMapping("loginPage")
    public String loginPage() {

        return "emartUser/loginPage";

    }

    // 로그인 처리 - JWT 발급
    @PostMapping("loginProcess")
    public String loginProcess(@ModelAttribute EmartUserDTO emartUserDTO, 
                              RedirectAttributes redirectAttrs,
                              HttpServletResponse response) {
        
        logger.info("loginProcess 호출 - userId: {}", emartUserDTO.getUserId());
        
        // Service 호출
        Map<String, Object> result = emartUserService.EmartUserGetByIdAndPassword(emartUserDTO);
        
        if (Boolean.TRUE.equals(result.get("success"))) {
            // ✅ 로그인 성공 - JWT 발급
            EmartUserDTO user = (EmartUserDTO) result.get("user");
            
       // ✅ 올바른 메서드 호출 (파라미터 값 전달)
        String accessToken = tokenProvider.createToken(user.getUserId(), user.getRoleId());
        // ✅ Refresh Token은 userId만 필요
        String refreshToken = tokenProvider.createRefreshToken(user.getUserId());
            
            // 3. Cookie에 JWT 저장
            createJwtCookie(response, "X-ACCESS-TOKEN", accessToken, 1800);      // 30분
            createJwtCookie(response, "X-REFRESH-TOKEN", refreshToken, 43200);   // 12시간
        
            logger.info("JWT 발급 완료 - userId: {}", user.getUserId());
            
            // 메인 페이지로 이동
            return "redirect:/emartUser/mainPage";
        } else {
      
             // ✅ 로그인 실패 - logger로 확인
        String errorMessage = (String) result.get("message");
        logger.warn("로그인 실패 - userId: {}, 오류: {}", emartUserDTO.getUserId(), errorMessage);
        
            redirectAttrs.addFlashAttribute("errorMessage", result.get("message"));
            return "redirect:/emartUser/loginPage";
        }
    }

    // Cookie 생성 메서드
    private void createJwtCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);        // JavaScript 접근 불가 (보안)
        cookie.setSecure(false);         // HTTPS에서만 사용 시 true
        cookie.setPath("/");             // 전체 경로에서 사용
        cookie.setMaxAge(maxAge);        // 초 단위
        response.addCookie(cookie);
    }
    // 로그아웃 (JWT 삭제)
    @PostMapping("logout")
    public String logout(HttpServletResponse response) {
        deleteJwtCookie(response, "X-ACCESS-TOKEN");
        deleteJwtCookie(response, "X-REFRESH-TOKEN");
        return "redirect:/emartUser/loginPage";
    }

    private void deleteJwtCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 즉시 만료
        response.addCookie(cookie);
    }


    @RequestMapping("useTerms")
    public String use_terms() {

        return "emartUser/useTerms"; // 이대로 사용
    }

    @RequestMapping("registerPage")
    public String registerPage() {

        return "emartUser/registerPage"; // 이대로 사용
    }

    @PostMapping("/registerProcess")
    public String registerProcess(@ModelAttribute EmartUserDTO emartUserDTO,
            RedirectAttributes redirectAttrs) {
        logger.info("registerProcess 호출 - name: {}", emartUserDTO.getName());

        Map<String, Object> response = emartUserService.insertPlatformUser(emartUserDTO);

        if (Objects.equals(response.get("success"), Boolean.TRUE)) {
              return "redirect:/emartUser/mainPage"; // ✓ 성공 시 리다이렉트
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", response.get("message"));
            return "redirect:/emartUser/registerPage"; // ✓ 실패 시 실패 메시지와 함께 리다이렉트
        }

    }


 @GetMapping("/mainPage")
public String mainPage(HttpServletRequest request, Model model) {
    logger.info("mainPage 호출");
    
    String userId = null;
    
    // ✅ Cookie에서 JWT 토큰 가져오기
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("X-ACCESS-TOKEN".equals(cookie.getName())) {
                String accessToken = cookie.getValue();
                logger.debug("Access Token found: {}", accessToken);
                
                // ✅ 토큰에서 userId 파싱 (올바른 메서드 사용)
                try {
                    userId = tokenProvider.getUserId(accessToken);  // 수정!
                    logger.debug("Parsed userId: {}", userId);
                } catch (Exception e) {
                    logger.error("Token parsing error: {}", e.getMessage());
                }
                break;
            }
        }
    }
    
    // ✅ Model에 userId 전달
    model.addAttribute("userId", userId);
    
    logger.info("mainPage - userId: {}", userId);
        
        // ✅ Model에 userId 전달
        model.addAttribute("userId", userId);
        
        logger.info("mainPage - userId: {}", userId);
        return "emartUser/mainPage"; // 해당 템플릿이 있다면 사용
    }


// 180-184번 줄을 아래로 변경
@GetMapping("/smartstore_seller")
public String smartStoreSeller() {
    return "emartUser/smartstore_seller";
}

@GetMapping("/emart_useTerms")
public String emart_useTerms(Model model) {
    // [수정] sellerUser 객체를 Model에 추가하지 않으면 Thymeleaf 파싱 오류 발생
    // 방법 1: 더미 객체 추가
    // model.addAttribute("sellerUser", new SellerUser());
    
    // 방법 2: HTML에서 th:object 제거 (권장)
    return "emartUser/emart_useTerms";
}

//  @GetMapping("/refresh")
// public Map<String, Object> refreshToken(HttpServletRequest request,
//                                          HttpServletResponse response) {
    
//     // ① Cookie에서 Refresh Token 가져오기
//     String refreshToken = null;
//     Cookie[] cookies = request.getCookies();
//     if (cookies != null) {
//         for (Cookie cookie : cookies) {
//             if ("X-REFRESH-TOKEN".equals(cookie.getName())) {
//                 refreshToken = cookie.getValue();
//                 break;
//             }
//         }
//     }
    
//     // ② Refresh Token이 없으면 에러
//     if (refreshToken == null || refreshToken.isEmpty()) {
//         throw new IllegalArgumentException("Refresh token not found");
//     }
    
//     // ③ Refresh Token 검증
//     if (!tokenProvider.validateRefreshToken(refreshToken)) {
//         throw new IllegalArgumentException("Invalid refresh token");
//     }
    
//     // ④ Claims에서 userId 추출
//     String userId = tokenProvider.getUserId(refreshToken);
    
//     // ⑤ roleId 조회 (DB에서 또는 기존 Access Token에서)
//     // 여기서는 간단히 기본값 1 (USER_ROLE)으로 설정
//     // 실제로는 DB에서 사용자의 roleId를 조회해야 함
//     int roleId = 1;  // 기본값 - 실제 구현에서는 DB 조회
    
//     // ⑥ 새로운 Access Token 생성 (int 타입으로 전달)
//     String newAccessToken = tokenProvider.createToken(userId, roleId);
//     String newRefreshToken = tokenProvider.createRefreshToken(userId);
    
//     // ⑦ 새로운 Token을 Cookie에 저장
//     createJwtCookie(response, "X-ACCESS-TOKEN", newAccessToken, 1800);     // 30분
//     createJwtCookie(response, "X-REFRESH-TOKEN", newRefreshToken, 43200);   // 12시간
    
//     Map<String, Object> result = new HashMap<>();
//     result.put("success", true);
//     result.put("accessToken", newAccessToken);
//     result.put("refreshToken", newRefreshToken);
    
//     System.out.println(result);
//       System.out.println(result);

//     return result;
//     }

 // ===== [수정됨] 로그인 상태 확인 API =====
    @GetMapping("/checkLoginStatus")
    @ResponseBody
    public Map<String, Object> checkLoginStatus(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        // Cookie에서 JWT 토큰 가져오기
        String accessToken = null;
        String refreshToken = null;
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("X-ACCESS-TOKEN".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("X-REFRESH-TOKEN".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        
        logger.info("로그인 상태 확인 - AccessToken: {}, RefreshToken: {}", 
            (accessToken != null ? "있음" : "없음"), 
            (refreshToken != null ? "있음" : "없음"));
        
        // JWT 토큰 검증 - Access Token 우선 확인
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                String userId = tokenProvider.getUserId(accessToken);
                if (userId != null && !userId.isEmpty()) {
                    result.put("isLoggedIn", true);
                    result.put("userId", userId);
                    logger.info("로그인 상태 확인 성공 - userId: {}", userId);
                    return result;
                }
            } catch (Exception e) {
                logger.error("Access Token 검증 실패: {}", e.getMessage());
            }
        }
        
        // Access Token이 없거나 유효하지 않을 때, Refresh Token 확인
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                // Refresh Token 검증
                if (tokenProvider.validateRefreshToken(refreshToken)) {
                    String userId = tokenProvider.getUserId(refreshToken);
                    if (userId != null && !userId.isEmpty()) {
                        result.put("isLoggedIn", true);
                        result.put("userId", userId);
                        result.put("needsRefresh", true); // 토큰 재발급 필요 플래그
                        logger.info("Refresh Token으로 로그인 확인 - userId: {}", userId);
                        return result;
                    }
                }
            } catch (Exception e) {
                logger.error("Refresh Token 검증 실패: {}", e.getMessage());
            }
        }
        
        // 로그인 안된 상태
        result.put("isLoggedIn", false);
        logger.info("로그인 상태 확인 - 로그인 안됨");
        return result;
    }




}

                              