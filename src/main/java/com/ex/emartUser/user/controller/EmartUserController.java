package com.ex.emartUser.user.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ex.emartUser.dto.EmartUserDTO;
import com.ex.emartUser.service.EmartUserService;
import com.ex.jwt.TokenProvider;

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


}