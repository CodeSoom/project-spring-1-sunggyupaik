package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.dto.UploadFileResultDto;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import com.example.bookclub.errors.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AccountServiceTest {
    private static final Long ACCOUNT_SETUP_ID = 1L;
    private static final String ACCOUNT_SETUP_NAME = "accountSetupName";
    private static final String ACCOUNT_SETUP_EMAIL = "accountSetupEmail";
    private static final String ACCOUNT_SETUP_NICKNAME = "accountSetupNickName";
    private static final String ACCOUNT_SETUP_PASSWORD = "accountSetupPassword";
    private static final boolean ACCOUNT_SETUP_DELETED_FALSE = false;

    private static final Long ACCOUNT_CREATED_ID = 2L;
    private static final String ACCOUNT_CREATED_NAME = "accountCreatedName";
    private static final String ACCOUNT_CREATED_EMAIL = "accountCreatedEmail";
    private static final String ACCOUNT_CREATED_NICKNAME = "accountCreatedNickName";
    private static final String ACCOUNT_CREATED_PASSWORD = "accountCreatedPassword";

    private static final String ACCOUNT_UPDATED_NICKNAME = "accountUpdatedNickName";
    private static final String ACCOUNT_NOT_VALID_PASSWORD = "accountNotValidPassword";
    private static final String ACCOUNT_UPDATED_PASSWORD = "accountUpdatedPassword";

    private static final Long UPLOAD_FILE_ID = 3L;
    private static final String UPLOAD_FILE_FILENAME = "setupFileName";
    private static final String UPLOAD_FILE_FILE_ORIGINAL_NAME = "setupFileOriginalName";
    private static final String UPLOAD_FILE_FILE_URL = "setupFileUrl";

    private static final Long UPLOAD_FILE_CREATED_ID = 8L;
    private static final String UPLOAD_FILE_CREATED_FILENAME = "createdFileName";
    private static final String UPLOAD_FILE_FILE_ORIGINAL_CREATED_NAME = "createdFileOriginalName";
    private static final String UPLOAD_FILE_FILE_CREATED_URL = "createdFileUrl";

    private static final Long STUDY_SETUP_ID = 7L;
    private static final String STUDY_SETUP_EMAIL = ACCOUNT_SETUP_EMAIL;

    private static final Long UPLOADFILE_UPDATE_ID = 4L;
    private static final String UPLOADFILE_UPDATE_FILENAME = "updatedFileName";
    private static final String UPLOADFILE_UPDATE_FILENORIGINALNAME = "updatedFileOriginalName";
    private static final String UPLOADFILE_UPDATE_FILEURL = "updatedFileUrl";

    private static final Long ACCOUNT_NOT_EXISTED_ID = 100L;
    private static final String ACCOUNT_NOT_EXISTED_EMAIL = "accountNotExistedEmail";
    private static final String ACCOUNT_DUPLICATED_EMAIL = "accountExistedEmail";
    private static final String ACCOUNT_DUPLICATED_NICKNAME = "accountExistedNickName";
    private static final String ACCOUNT_CREATED_AUTHENTICATION_NUMBER = "existedAuthentication";
    private static final String AUTHENTICATION_NUMBER_NOT_MATCHED = "notMatchedAuthentication";

    private UploadFileService uploadFileService;
    private RoleRepository roleRepository;
    private AccountService accountService;
    private AccountRepository accountRepository;
    private EmailAuthenticationRepository emailAuthenticationRepository;

    private UploadFile uploadFile;
    private UploadFile createdUploadFile;
    private UploadFile updateUploadFile;
    private Study setUpStudy;
    private Account setUpAccount;
    private Account createdAccountWithoutUploadFile;
    private Account createdAccountWithUploadFile;
    private PasswordEncoder passwordEncoder;

    private AccountCreateDto accountCreateDto;
    private AccountCreateDto emailExistedAccountCreateDto;
    private AccountCreateDto nicknameExistedAccountCreateDto;
    private AccountCreateDto emailNotReceivedAuthenticationNumberAccountCreateDto;
    private AccountCreateDto authenticationNumberNotMatchedAccountCreateDto;
    private AccountUpdateDto accountUpdateDto;
    private AccountUpdateDto nicknameDuplicatedAccountUpdateDto;
    private AccountUpdateDto passwordNotValidAccountUpdateDto;
    private AccountUpdatePasswordDto accountUpdatePasswordDto;
    private AccountUpdatePasswordDto newPasswordNotMatchedUpdateDto;
    private AccountUpdatePasswordDto passwordNotMatchedDto;
    private EmailAuthentication emailAuthentication;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        emailAuthenticationRepository = mock(EmailAuthenticationRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        uploadFileService = mock(UploadFileService.class);
        roleRepository = mock(RoleRepository.class);

        accountService = new AccountService(accountRepository, emailAuthenticationRepository,
                passwordEncoder, uploadFileService, roleRepository);

        uploadFile = UploadFile.builder()
                .id(UPLOAD_FILE_ID)
                .fileName(UPLOAD_FILE_FILENAME)
                .fileOriginalName(UPLOAD_FILE_FILE_ORIGINAL_NAME)
                .fileUrl(UPLOAD_FILE_FILE_URL)
                .build();

        createdUploadFile = UploadFile.builder()
                .id(UPLOAD_FILE_CREATED_ID)
                .fileName(UPLOAD_FILE_CREATED_FILENAME)
                .fileOriginalName(UPLOAD_FILE_FILE_ORIGINAL_CREATED_NAME)
                .fileUrl(UPLOAD_FILE_FILE_CREATED_URL)
                .build();

        updateUploadFile = UploadFile.builder()
                .id(UPLOADFILE_UPDATE_ID)
                .fileName(UPLOAD_FILE_FILENAME)
                .fileOriginalName(UPLOAD_FILE_FILE_ORIGINAL_NAME)
                .fileUrl(UPLOAD_FILE_FILE_URL)
                .build();

        setUpStudy = Study.builder()
                .id(STUDY_SETUP_ID)
                .email(STUDY_SETUP_EMAIL)
                .build();

        setUpAccount = Account.builder()
                .id(ACCOUNT_SETUP_ID)
                .name(ACCOUNT_SETUP_NAME)
                .email(ACCOUNT_SETUP_EMAIL)
                .nickname(ACCOUNT_SETUP_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_SETUP_PASSWORD))
                .deleted(ACCOUNT_SETUP_DELETED_FALSE)
                .uploadFile(uploadFile)
                .build();

        setUpAccount.addUploadFile(uploadFile);
        setUpStudy.addAccount(setUpAccount);

        createdAccountWithUploadFile = Account.builder()
                .id(ACCOUNT_CREATED_ID)
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_CREATED_PASSWORD))
                .uploadFile(createdUploadFile)
                .build();

        createdAccountWithoutUploadFile = Account.builder()
                .id(ACCOUNT_CREATED_ID)
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_CREATED_PASSWORD))
                .build();

        accountCreateDto = AccountCreateDto.builder()
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .authenticationNumber(ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
                .build();

        authenticationNumberNotMatchedAccountCreateDto = AccountCreateDto.builder()
                .authenticationNumber(AUTHENTICATION_NUMBER_NOT_MATCHED)
                .build();

        accountUpdateDto = AccountUpdateDto.builder()
                .nickname(ACCOUNT_UPDATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

        emailExistedAccountCreateDto = AccountCreateDto.builder()
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_DUPLICATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

        emailNotReceivedAuthenticationNumberAccountCreateDto = AccountCreateDto.builder()
                .email(ACCOUNT_NOT_EXISTED_EMAIL)
                .build();

        nicknameExistedAccountCreateDto = AccountCreateDto.builder()
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_DUPLICATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

        nicknameDuplicatedAccountUpdateDto = AccountUpdateDto.builder()
                .nickname(ACCOUNT_DUPLICATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

        passwordNotValidAccountUpdateDto = AccountUpdateDto.builder()
                .nickname(ACCOUNT_SETUP_NICKNAME)
                .password(ACCOUNT_NOT_VALID_PASSWORD)
                .build();

        accountUpdatePasswordDto = AccountUpdatePasswordDto.builder()
                .password(ACCOUNT_CREATED_PASSWORD)
                .newPassword(ACCOUNT_UPDATED_PASSWORD)
                .newPasswordConfirmed(ACCOUNT_UPDATED_PASSWORD)
                .build();

        newPasswordNotMatchedUpdateDto = AccountUpdatePasswordDto.builder()
                .password(ACCOUNT_CREATED_PASSWORD)
                .newPassword(ACCOUNT_UPDATED_PASSWORD)
                .newPasswordConfirmed(ACCOUNT_SETUP_PASSWORD)
                .build();

        emailAuthentication = EmailAuthentication.builder()
                .email(ACCOUNT_CREATED_EMAIL)
                .authenticationNumber(ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
                .build();
    }

    @Test
    public void findWithExistedId() {
        given(accountRepository.findById(ACCOUNT_SETUP_ID)).willReturn(Optional.of(setUpAccount));

        Account account = accountService.findUser(ACCOUNT_SETUP_ID);

        assertThat(account.getId()).isEqualTo(ACCOUNT_SETUP_ID);
        assertThat(account.getUploadFile().getId()).isEqualTo(UPLOAD_FILE_ID);
        assertThat(account.getStudy().getId()).isEqualTo(STUDY_SETUP_ID);
    }

    @Test
    public void findWithNotExistedId() {
        given(accountRepository.findById(ACCOUNT_NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findUser(ACCOUNT_NOT_EXISTED_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test void findUserByExistedEmail() {
        given(accountRepository.findByEmail(ACCOUNT_SETUP_EMAIL)).willReturn(Optional.of(setUpAccount));

        Account account = accountService.findUserByEmail(ACCOUNT_SETUP_EMAIL);

        assertThat(account.getId()).isEqualTo(ACCOUNT_SETUP_ID);
        assertThat(account.getUploadFile().getId()).isEqualTo(UPLOAD_FILE_ID);
        assertThat(account.getStudy().getId()).isEqualTo(STUDY_SETUP_ID);
    }

    @Test void findUserByNotExistedEmail() {
        given(accountRepository.findByEmail(ACCOUNT_NOT_EXISTED_EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findUserByEmail(ACCOUNT_NOT_EXISTED_EMAIL))
                .isInstanceOf(AccountEmailNotFoundException.class);
    }

    @Test void getUserByExistedId() {
        given(accountRepository.findById(ACCOUNT_SETUP_ID)).willReturn(Optional.of(setUpAccount));

        AccountResultDto accountResultDto = accountService.getUser(ACCOUNT_SETUP_ID);

        assertThat(accountResultDto.getId()).isEqualTo(ACCOUNT_SETUP_ID);
        assertThat(accountResultDto.getUploadFileResultDto().getId()).isEqualTo(UPLOAD_FILE_ID);
        assertThat(accountResultDto.getStudyResultDto().getId()).isEqualTo(STUDY_SETUP_ID);
    }

    @Test void getUserByNotExistedId() {
        given(accountRepository.findById(ACCOUNT_NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getUser(ACCOUNT_NOT_EXISTED_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    public void createWithUploadFile() {
        given(accountRepository.save(any(Account.class))).willReturn(createdAccountWithUploadFile);
        given(emailAuthenticationRepository.findByEmail(ACCOUNT_CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        AccountResultDto accountResultDto = accountService.createUser(accountCreateDto, createdUploadFile);

        assertThat(accountResultDto.getId()).isEqualTo(ACCOUNT_CREATED_ID);
        assertThat(accountResultDto.getName()).isEqualTo(accountCreateDto.getName());
        assertThat(accountResultDto.getEmail()).isEqualTo(accountCreateDto.getEmail());
        assertThat(accountResultDto.getNickname()).isEqualTo(accountCreateDto.getNickname());
        assertThat(passwordEncoder.matches(accountCreateDto.getPassword(), accountResultDto.getPassword())).isTrue();
        assertThat(accountResultDto.getUploadFileResultDto().getId()).isEqualTo(UPLOAD_FILE_CREATED_ID);
        assertThat(emailAuthentication.getAuthenticationNumber()).isEqualTo(accountCreateDto.getAuthenticationNumber());

        verify(emailAuthenticationRepository).delete(emailAuthentication);
    }

    @Test
    public void createWithoutUploadFile() {
        given(accountRepository.save(any(Account.class))).willReturn(createdAccountWithoutUploadFile);
        given(emailAuthenticationRepository.findByEmail(ACCOUNT_CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        AccountResultDto accountResultDto = accountService.createUser(accountCreateDto, null);

        assertThat(accountResultDto.getId()).isEqualTo(ACCOUNT_CREATED_ID);
        assertThat(accountResultDto.getName()).isEqualTo(accountCreateDto.getName());
        assertThat(accountResultDto.getEmail()).isEqualTo(accountCreateDto.getEmail());
        assertThat(accountResultDto.getNickname()).isEqualTo(accountCreateDto.getNickname());
        assertThat(passwordEncoder.matches(accountCreateDto.getPassword(), accountResultDto.getPassword())).isTrue();
        assertThat(accountResultDto.getUploadFileResultDto().getId()).isNull();
        assertThat(emailAuthentication.getAuthenticationNumber()).isEqualTo(accountCreateDto.getAuthenticationNumber());

        verify(emailAuthenticationRepository).delete(emailAuthentication);
    }

    @Test
    public void createWithDuplicatedEmail() {
        given(accountRepository.existsByEmail(ACCOUNT_DUPLICATED_EMAIL)).willReturn(true);

        assertThatThrownBy(() -> accountService.createUser(emailExistedAccountCreateDto, null))
                .isInstanceOf(AccountEmailDuplicatedException.class);
    }

    @Test
    public void createWithDuplicatedNickname() {
        given(accountRepository.existsByNickname(ACCOUNT_DUPLICATED_NICKNAME)).willReturn(true);
        given(emailAuthenticationRepository.findByEmail(ACCOUNT_CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        assertThatThrownBy(() -> accountService.createUser(nicknameExistedAccountCreateDto, null))
                .isInstanceOf(AccountNicknameDuplicatedException.class);
    }

    @Test
    public void createWithEmailNotReceivedAuthenticationNumber() {
        given(emailAuthenticationRepository.findByEmail(ACCOUNT_NOT_EXISTED_EMAIL))
                .willReturn(Optional.empty());

        assertThatThrownBy(
                () -> accountService.createUser(emailNotReceivedAuthenticationNumberAccountCreateDto, null)
        )
                .isInstanceOf(EmailNotAuthenticatedException.class);
    }

    @Test
    public void createWithEmailNotMatchedAuthenticationNumber() {
        given(emailAuthenticationRepository.findByEmail(ACCOUNT_CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        assertThatThrownBy(
                () -> accountService.createUser(authenticationNumberNotMatchedAccountCreateDto, null)
        )
                .isInstanceOf(EmailNotAuthenticatedException.class);
    }

    @Test
    public void updateWithUploadFileAlreadyHasUploadFile() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));

        AccountResultDto accountResultDto =
                accountService.updateUser(ACCOUNT_CREATED_ID, accountUpdateDto, updateUploadFile);

        assertThat(accountResultDto.getNickname()).isEqualTo(accountUpdateDto.getNickname());
        assertThat(passwordEncoder.matches(accountUpdateDto.getPassword(), accountResultDto.getPassword())).isTrue();

        UploadFileResultDto updatedUploadFileResultDto = accountResultDto.getUploadFileResultDto();
        assertThat(updatedUploadFileResultDto.getId()).isEqualTo(updateUploadFile.getId());
        assertThat(updatedUploadFileResultDto.getFileName()).isEqualTo(updateUploadFile.getFileName());
        assertThat(updatedUploadFileResultDto.getFileOriginalName()).isEqualTo(updateUploadFile.getFileOriginalName());
        assertThat(updatedUploadFileResultDto.getFileUrl()).isEqualTo(updateUploadFile.getFileUrl());
    }

    @Test
    void updateWithUploadFileBeforeNotHasUploadFile() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithoutUploadFile));

        AccountResultDto accountResultDto =
                accountService.updateUser(ACCOUNT_CREATED_ID, accountUpdateDto, updateUploadFile);

        UploadFileResultDto updatedUploadFileResultDto = accountResultDto.getUploadFileResultDto();
        assertThat(updatedUploadFileResultDto.getId()).isEqualTo(updateUploadFile.getId());
        assertThat(updatedUploadFileResultDto.getFileName()).isEqualTo(updateUploadFile.getFileName());
        assertThat(updatedUploadFileResultDto.getFileOriginalName()).isEqualTo(updateUploadFile.getFileOriginalName());
        assertThat(updatedUploadFileResultDto.getFileUrl()).isEqualTo(updateUploadFile.getFileUrl());
    }

    @Test
    void updateWithoutUploadFileAlreadyHasUploadFile() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));

        AccountResultDto accountResultDto =
                accountService.updateUser(ACCOUNT_CREATED_ID, accountUpdateDto, null);

        UploadFileResultDto updatedUploadFileResultDto = accountResultDto.getUploadFileResultDto();
        UploadFile savedUpdatedUploadFile = createdAccountWithUploadFile.getUploadFile();
        assertThat(updatedUploadFileResultDto.getId()).isEqualTo(savedUpdatedUploadFile.getId());
        assertThat(updatedUploadFileResultDto.getFileName()).isEqualTo(savedUpdatedUploadFile.getFileName());
        assertThat(updatedUploadFileResultDto.getFileOriginalName()).isEqualTo(savedUpdatedUploadFile.getFileOriginalName());
        assertThat(updatedUploadFileResultDto.getFileUrl()).isEqualTo(savedUpdatedUploadFile.getFileUrl());
    }

    @Test
    void updateWithoutUploadFileBeforeNotHasUploadFile() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithoutUploadFile));

        AccountResultDto accountResultDto =
                accountService.updateUser(ACCOUNT_CREATED_ID, accountUpdateDto, null);

        assertThat(accountResultDto.getUploadFileResultDto().getId()).isNull();
    }

    @Test
    public void updatedWithDuplicatedNickname() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));
        given(accountRepository.existsByIdNotAndNickname(ACCOUNT_CREATED_ID, ACCOUNT_DUPLICATED_NICKNAME)).willReturn(true);

        assertThatThrownBy(
                () -> accountService.updateUser(ACCOUNT_CREATED_ID, nicknameDuplicatedAccountUpdateDto, null)
        )
                .isInstanceOf(AccountNicknameDuplicatedException.class);
    }

    @Test
    public void updateWithNotExistedId() {
        given(accountRepository.findById(ACCOUNT_NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(
                () -> accountService.updateUser(ACCOUNT_NOT_EXISTED_ID, accountUpdateDto, null)
        )
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    public void updateWithNotValidPassword() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));

        assertThatThrownBy(
                () -> accountService.updateUser(ACCOUNT_CREATED_ID, passwordNotValidAccountUpdateDto, null)
        )
                .isInstanceOf(AccountPasswordBadRequestException.class);
    }

    @Test
    public void updatePasswordWithValidAttribute() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));

        AccountResultDto accountResultDto =
                accountService.updatePassword(ACCOUNT_CREATED_ID, accountUpdatePasswordDto);

        assertThat(
                passwordEncoder.matches(
                    accountUpdatePasswordDto.getNewPassword(), accountResultDto.getPassword()
                )
        ).isTrue();
    }

    @Test
    public void updatePasswordWithNotMatchedNewPassword() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));

        assertThatThrownBy(
                () -> accountService.updatePassword(ACCOUNT_CREATED_ID, newPasswordNotMatchedUpdateDto)
        )
                .isInstanceOf(AccountNewPasswordNotMatchedException.class);
    }

    @Test
    public void updatePasswordWithNotExistedId() {
        given(accountRepository.findById(ACCOUNT_NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(
                () -> accountService.updatePassword(ACCOUNT_NOT_EXISTED_ID, accountUpdatePasswordDto)
        )
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    public void deleteWithExistedId() {
        given(accountRepository.findById(ACCOUNT_CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));

        AccountResultDto accountResultDto = accountService.deleteUser(ACCOUNT_CREATED_ID);

        assertThat(accountResultDto.getId()).isEqualTo(ACCOUNT_CREATED_ID);
        assertThat(accountResultDto.isDeleted()).isTrue();
        assertThat(accountResultDto.getUploadFileResultDto().getId()).isNull();
    }

    @Test
    public void deleteWithNotExistedId() {
        given(accountRepository.findById(ACCOUNT_NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.deleteUser(ACCOUNT_NOT_EXISTED_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    public void getAuthenticationNumberWithExistedEmail() {
        given(emailAuthenticationRepository.findByEmail(ACCOUNT_CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        EmailAuthentication emailAuthentication = accountService.getAuthenticationNumber(ACCOUNT_CREATED_EMAIL);

        assertThat(emailAuthentication.getEmail()).isEqualTo(ACCOUNT_CREATED_EMAIL);
        assertThat(emailAuthentication.getAuthenticationNumber()).isEqualTo(ACCOUNT_CREATED_AUTHENTICATION_NUMBER);
    }
}
