package com.flab.jbly.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.flab.jbly.application.user.UserServiceImpl;
import com.flab.jbly.infrastructure.encryption.Encryption;
import com.flab.jbly.infrastructure.exception.user.AccountMisMatchInfoException;
import com.flab.jbly.infrastructure.exception.user.DuplicatedUserException;
import com.flab.jbly.presentation.user.request.AccountDeleteRequest;
import com.flab.jbly.presentation.user.request.LoginRequest;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class UserUnitTest {

    private UserServiceImpl userService;
    private HashMapRepository repository = new HashMapRepository();

    @BeforeEach
    void setUp() {
        repository.clearDB();
        userService = new UserServiceImpl(repository, new Encryption());
    }

    @DisplayName("로그인 시 데이터의 타입이 안 맞을 경우")
    @ParameterizedTest
    @MethodSource("failLoginDataSet")
    public void loginRequestExceptionTest(String userId, String pwd) throws Exception {
        assertThatThrownBy(() -> new LoginRequest(userId, pwd)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("계정 삭제 시 PK 값이 존재하지 않을 경우")
    @ParameterizedTest
    @MethodSource("failDeleteRequestSet")
    public void accountDeleteRequestPKNotRequiredConditionTest() throws Exception {
        Long pk = 0L;
        assertThatThrownBy(() -> new AccountDeleteRequest(pk,"abc")).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("회원 가입 성공하는 경우")
    @Test
    public void signUpSuccessTest() throws Exception {
        var request = UserSteps.AddUser();
        var userPk = 1L;
        userService.saveUser(request.toCommand());
        var user = repository.getUserById(userPk);
        assertThat(user.getId()).isEqualTo(userPk);
    }

    @DisplayName("회원 가입 실패하는 경우, 동일한 ID값이 입력될 경우 실패합니다.")
    @Test
    public void signUpFailTest() throws Exception {
        var user1 = UserSteps.AddUser();
        var user2 = UserSteps.AddUser();
        userService.saveUser(user1.toCommand());
        assertThatThrownBy(() -> userService.saveUser(user2.toCommand())).isInstanceOf(DuplicatedUserException.class);
    }

    @DisplayName("회원 계정 삭제 성공하는 경우")
    @Test
    public void deleteUserSuccessTest() throws Exception {
        var user = UserSteps.AddUser();
        userService.saveUser(user.toCommand());

        var request = UserSteps.deleteRequest();
        userService.deleteUserAccount(request.toCommand());

        assertThat(repository.getUserById(request.Id())).isNull();
    }

    @DisplayName("아이디가 틀릴 경우 회원 계정 삭제 실패")
    @Test
    public void deleteUserFailTest() throws Exception {
        var user = UserSteps.AddUser();
        userService.saveUser(user.toCommand());

        var request = new AccountDeleteRequest(1L, "abc");
        assertThatThrownBy(() -> userService.deleteUserAccount(request.toCommand())).isInstanceOf(AccountMisMatchInfoException.class);
    }

    private static Stream<Arguments> failLoginDataSet() {
        return Stream.of(
            Arguments.of("", "!1234abcd"),
            Arguments.of("abcd", ""),
            Arguments.of("", "")
        );
    }

    private static Stream<Arguments> failDeleteRequestSet() {
        return Stream.of(
            Arguments.of(0L, "abc"),
            Arguments.of(1L, "")
        );
    }
}
