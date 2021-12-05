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
import com.example.bookclub.errors.AccountEmailNotFoundException;
import com.example.bookclub.errors.AccountNotFoundException;
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
    private static final Long CREATED_IMAGE_ID = 5L;
    private static final String ACCOUNT_NOT_EXISTED_EMAIL = "notExistedEmail";
    private static final String DUPLICATED_EMAIL = "existedEmail";
    private static final String DUPLICATED_NICKNAME = "existedNickName";
    private static final String CREATED_AUTHENTICATIONNUMBER = "existedAuthentication";
    private static final String NOT_EXISTED_AUTHENTICATIONNUMBER = "notExistedAuthentication";

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
    private Account notEncodedCreatedAccountWithUploadFile;
    private Account createdAccount;
    private Account createdAccountWithUploadFile;
    private PasswordEncoder passwordEncoder;

    private AccountCreateDto accountCreateDto;
    private AccountCreateDto authenticationNumberNotMatchedAccountCreateDto;
    private AccountCreateDto emailExistedAccountCreateDto;
    private AccountCreateDto nicknameExistedAccountCreateDto;
    private AccountUpdateDto accountUpdateDto;
    private AccountUpdateDto nicknameDuplicatedAccountUpdateDto;
    private AccountUpdateDto passwordNotValidAccountUpdateDto;
//    private AccountResultDto accountResultDto;
    private AccountUpdatePasswordDto accountUpdatePasswordDto;
    private AccountUpdatePasswordDto newPasswordNotMatchedDto;
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

        notEncodedCreatedAccountWithUploadFile = Account.builder()
                .id(ACCOUNT_CREATED_ID)
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .uploadFile(uploadFile)
                .build();

        createdAccount = Account.builder()
                .id(ACCOUNT_CREATED_ID)
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_CREATED_PASSWORD))
                .uploadFile(createdUploadFile)
                .build();

        createdAccountWithUploadFile = Account.builder()
                .id(CREATED_IMAGE_ID)
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_CREATED_PASSWORD))
                .uploadFile(uploadFile)
                .build();

        accountCreateDto = AccountCreateDto.builder()
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .authenticationNumber(CREATED_AUTHENTICATIONNUMBER)
                .build();

        authenticationNumberNotMatchedAccountCreateDto = AccountCreateDto.builder()
                .authenticationNumber(NOT_EXISTED_AUTHENTICATIONNUMBER)
                .build();

        accountUpdateDto = AccountUpdateDto.builder()
                .nickname(ACCOUNT_UPDATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

        emailExistedAccountCreateDto = AccountCreateDto.builder()
                .name(ACCOUNT_CREATED_NAME)
                .email(DUPLICATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

        nicknameExistedAccountCreateDto = AccountCreateDto.builder()
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(DUPLICATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

        nicknameDuplicatedAccountUpdateDto = AccountUpdateDto.builder()
                .nickname(DUPLICATED_NICKNAME)
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

        newPasswordNotMatchedDto = AccountUpdatePasswordDto.builder()
                .password(ACCOUNT_CREATED_PASSWORD)
                .newPassword(ACCOUNT_UPDATED_PASSWORD)
                .newPasswordConfirmed(ACCOUNT_SETUP_PASSWORD)
                .build();

        passwordNotMatchedDto = AccountUpdatePasswordDto.builder()
                .password("")
                .newPassword(ACCOUNT_UPDATED_PASSWORD)
                .newPasswordConfirmed(ACCOUNT_UPDATED_PASSWORD)
                .build();

//        accountResultDto = AccountResultDto.of(setUpAccount);

        emailAuthentication = EmailAuthentication.builder()
                .email(ACCOUNT_CREATED_EMAIL)
                .authenticationNumber(CREATED_AUTHENTICATIONNUMBER)
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
    public void createWithValidAttributeWithUploadFile() {
        given(accountRepository.save(any(Account.class))).willReturn(createdAccount);
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
//
//    @Test
//    public void createWithUploadFile() {
//        given(accountRepository.save(any(Account.class))).willReturn(notEncodedCreatedAccountWithUploadFile);
//        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL)).willReturn(Optional.of(emailAuthentication));
//
//        AccountResultDto accountResultDto = accountService.createUser(accountCreateDto, uploadFile);
//        UploadFile uploadFile = accountResultDto.getUploadFile();
//
//        assertThat(uploadFile.getId()).isEqualTo(UPLOADFILE_ID);
//        assertThat(uploadFile.getFileName()).isEqualTo(UPLOADFILE_FILENAME);
//        assertThat(uploadFile.getFileOriginalName()).isEqualTo(UPLOADFILE_FILENORIGINALNAME);
//        assertThat(uploadFile.getFileUrl()).isEqualTo(UPLOADFILE_FILEURL);
//    }
//
//    @Test
//    public void createWithDuplicatedEmail() {
//        given(accountRepository.existsByEmail(DUPLICATED_EMAIL)).willReturn(true);
//
//        assertThatThrownBy(() -> accountService.createUser(emailExistedAccountCreateDto, null))
//                .isInstanceOf(AccountEmailDuplicatedException.class);
//    }
//
//    @Test
//    public void createWithNotMatchedAuthenticationNumber() {
//        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL))
//                .willReturn(Optional.of(emailAuthentication));
//
//        assertThatThrownBy(() -> accountService.createUser(authenticationNumberNotMatchedAccountCreateDto, null))
//                .isInstanceOf(EmailNotAuthenticatedException.class);
//    }
//
//    @Test
//    public void createWithDuplicatedNickname() {
//        given(accountRepository.existsByNickname(DUPLICATED_NICKNAME)).willReturn(true);
//        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL)).willReturn(Optional.of(emailAuthentication));
//
//        assertThatThrownBy(() -> accountService.createUser(nicknameExistedAccountCreateDto, null))
//                .isInstanceOf(AccountNicknameDuplicatedException.class);
//    }
//
//    @Test
//    public void updateWithValidAttribute() {
//        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccountWithUploadFile));
//
//        AccountResultDto accountResultDto = accountService.updateUser(CREATED_ID, accountUpdateDto, updateUploadFile);
//        UploadFile uploadFile = accountResultDto.getUploadFile();
//
//        assertThat(accountResultDto.getNickname()).isEqualTo(accountUpdateDto.getNickname());
//        assertThat(passwordEncoder.matches(accountUpdateDto.getPassword(), accountResultDto.getPassword())).isTrue();
//
//        assertThat(uploadFile.getId()).isEqualTo(UPLOADFILE_UPDATE_ID);
//        assertThat(uploadFile.getFileName()).isEqualTo(UPLOADFILE_UPDATE_FILENAME);
//        assertThat(uploadFile.getFileOriginalName()).isEqualTo(UPLOADFILE_UPDATE_FILENORIGINALNAME);
//        assertThat(uploadFile.getFileUrl()).isEqualTo(UPLOADFILE_UPDATE_FILEURL);
//    }
//
//    @Test
//    public void updatedWithDuplicatedNickname() {
//        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));
//        given(accountRepository.existsByIdNotAndNickname(CREATED_ID, DUPLICATED_NICKNAME)).willReturn(true);
//
//        assertThatThrownBy(() -> accountService.updateUser(CREATED_ID, nicknameDuplicatedAccountUpdateDto, null))
//                .isInstanceOf(AccountNicknameDuplicatedException.class);
//    }
//
//    @Test
//    public void updateWithNotValidPassword() {
//        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));
//
//        assertThatThrownBy(() -> accountService.updateUser(CREATED_ID, passwordNotValidAccountUpdateDto, null))
//                .isInstanceOf(AccountPasswordBadRequestException.class);
//    }
//
//    @Test
//    public void updatePasswordWithValidAttribute() {
//        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));
//
//        AccountResultDto accountResultDto = accountService.updateUserPassword(CREATED_ID, accountUpdatePasswordDto);
//
//        assertThat(passwordEncoder.matches(
//                accountUpdatePasswordDto.getNewPassword(),
//                accountResultDto.getPassword())
//        ).isTrue();
//    }
//
//    @Test
//    public void updatePasswordWithNotMatchedNewPassword() {
//        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));
//
//        assertThatThrownBy(() -> accountService.updateUserPassword(CREATED_ID, newPasswordNotMatchedDto))
//                .isInstanceOf(AccountNewPasswordNotMatchedException.class);
//    }
//
//    @Test
//    public void updatePasswordWithNotValidPassword() {
//        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));
//
//        assertThatThrownBy(() -> accountService.updateUserPassword(CREATED_ID, passwordNotMatchedDto))
//                .isInstanceOf(AccountPasswordBadRequestException.class);
//    }
//
//    @Test
//    public void deleteAccount() {
//        given(accountRepository.findById(CREATED_IMAGE_ID)).willReturn(Optional.of(createdAccountWithUploadFile));
//
//        AccountResultDto accountResultDto = accountService.deleteUser(CREATED_IMAGE_ID);
//
//        assertThat(accountResultDto.getId()).isEqualTo(CREATED_IMAGE_ID);
//        assertThat(accountResultDto.isDeleted()).isTrue();
//        assertThat(accountResultDto.getUploadFile()).isNull();
//    }
//
//    @Test
//    public void deleteAccountWithNotExistedId() {
//        given(accountRepository.findById(NOT_EXISTED_ID)).willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> accountService.deleteUser(NOT_EXISTED_ID))
//                .isInstanceOf(AccountNotFoundException.class);
//    }
}
