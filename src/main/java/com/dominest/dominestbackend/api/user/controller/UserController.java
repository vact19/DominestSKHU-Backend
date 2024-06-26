package com.dominest.dominestbackend.api.user.controller;

import com.dominest.dominestbackend.api.user.request.ChangePasswordRequest;
import com.dominest.dominestbackend.api.user.request.JoinRequest;
import com.dominest.dominestbackend.api.user.request.LoginRequest;
import com.dominest.dominestbackend.api.common.ResponseTemplate;
import com.dominest.dominestbackend.api.schedule.response.UserScheduleResponse;
import com.dominest.dominestbackend.api.todo.response.TodoUserResponse;
import com.dominest.dominestbackend.domain.jwt.dto.TokenDto;
import com.dominest.dominestbackend.domain.jwt.service.TokenValidator;
import com.dominest.dominestbackend.domain.schedule.service.ScheduleService;
import com.dominest.dominestbackend.domain.todo.service.TodoService;
import com.dominest.dominestbackend.domain.user.service.UserService;
import com.dominest.dominestbackend.global.util.PrincipalParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final TokenValidator tokenValidator;
    private final ScheduleService scheduleService;
    private final TodoService todoService;

    @PostMapping("/join") // 회원가입
    public ResponseTemplate<Void> signUp(@RequestBody @Valid final JoinRequest request){
        userService.save(request);

        return new ResponseTemplate<>(HttpStatus.OK, "회원가입에 성공하였습니다.");
    }

    @PostMapping("/login") // 로그인
    public ResponseTemplate<TokenDto> login(@RequestBody @Valid final LoginRequest request) {
        TokenDto tokenDto = userService.loginTemp(request.getEmail(), request.getPassword());

        return new ResponseTemplate<>(HttpStatus.OK, "로그인 성공", tokenDto);
    }

    @PostMapping("/login/short-token-exp") // 로그인
    public ResponseTemplate<TokenDto> loginV2(@RequestBody @Valid final LoginRequest request) {
        TokenDto tokenDto = userService.login(request.getEmail(), request.getPassword());

        return new ResponseTemplate<>(HttpStatus.OK, "로그인 성공", tokenDto);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseTemplate<Void> logout(Principal principal) {
        // 액세스 토큰 검증은 필터에서 거치므로 바로 로그아웃 처리
        userService.logout(PrincipalParser.toEmail(principal));

        return new ResponseTemplate<>(HttpStatus.OK, "로그아웃 성공");
    }

    /**
     *   refresh 토큰을 이용, access 토큰을 재발급하는 메소드
     */
    @PostMapping(value = "/token/reissue")
    public ResponseTemplate<TokenDto> accessToken(HttpServletRequest httpServletRequest){

        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        tokenValidator.validateBearer(authorizationHeader);

        String refreshToken = authorizationHeader.split(" ")[1];
        TokenDto tokenDto = userService.reissueByRefreshToken(refreshToken);

        return new ResponseTemplate<>(HttpStatus.OK, "토큰 재발급", tokenDto);
    }

    @PostMapping("/my-page/password") // 비밀번호 변경
    public ResponseTemplate<Void> changePassword(@RequestBody ChangePasswordRequest request
            , Principal principal) {
        String logInUserEmail = PrincipalParser.toEmail(principal);

        userService.changePassword(logInUserEmail, request.getPassword(), request.getNewPassword());

        return new ResponseTemplate<>(HttpStatus.OK, "비밀번호를 성공적으로 변경하였습니다.");
    }

    @GetMapping("/schedule-userinfo") // 유저 이름, 번호 가져오기
    public ResponseTemplate<List<UserScheduleResponse>> getUserInfoSchedule() {
        List<UserScheduleResponse> userResponses = scheduleService.getUserInfo();
        return new ResponseTemplate<>(HttpStatus.OK, "유저의 이름과 번호를 성공적으로 불러왔습니다.", userResponses);
    }

    @GetMapping("/todo-userinfo") // 투두 근로자 불러오기
    public ResponseTemplate<List<TodoUserResponse>> getUserInfoTodo() {
        List<TodoUserResponse> nameResponse = todoService.getUserNameTodo();
        return new ResponseTemplate<>(HttpStatus.OK, "유저의 이름을 모두 불러오는데 성공했습니다.", nameResponse);
    }
}
