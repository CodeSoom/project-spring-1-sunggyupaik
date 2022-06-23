package com.example.bookclub.application.account;

import com.example.bookclub.application.uploadfile.UploadFileService;
import com.example.bookclub.common.exception.account.AccountEmailDuplicatedException;
import com.example.bookclub.common.exception.account.AccountEmailNotFoundException;
import com.example.bookclub.common.exception.account.AccountNewPasswordNotMatchedException;
import com.example.bookclub.common.exception.account.AccountNicknameDuplicatedException;
import com.example.bookclub.common.exception.account.AccountNotFoundException;
import com.example.bookclub.common.exception.account.AccountPasswordBadRequestException;
import com.example.bookclub.common.exception.account.emailauthentication.EmailNotAuthenticatedException;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.account.emailauthentication.EmailAuthentication;
import com.example.bookclub.domain.account.emailauthentication.EmailAuthenticationRepository;
import com.example.bookclub.domain.account.role.Role;
import com.example.bookclub.domain.account.role.RoleRepository;
import com.example.bookclub.domain.uplodfile.UploadFile;
import com.example.bookclub.dto.AccountDto;
import com.example.bookclub.infrastructure.account.JpaAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 사용자 조회, 생성, 수정, 삭제, 인증번호 조회, 삭제, 닉네임 중복검사, 비밀번호 변경, 사용자 수 조회를 한다.
 */
@Service
@Transactional
public class AccountService {
    private final JpaAccountRepository accountRepository;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadFileService uploadFileService;
    private final RoleRepository roleRepository;

    public AccountService(JpaAccountRepository accountRepository,
                          EmailAuthenticationRepository emailAuthenticationRepository,
                          PasswordEncoder passwordEncoder,
                          UploadFileService uploadFileService,
                          RoleRepository roleRepository
    ) {
        this.accountRepository = accountRepository;
        this.emailAuthenticationRepository = emailAuthenticationRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadFileService = uploadFileService;
        this.roleRepository = roleRepository;
    }

    /**
     * 주어진 사용자 식별자에 해당하는 사용자를 반환한다.
     *
     * @param id 사용자 식별자
     * @return 주어진 사용자 식별자에 해당하는 사용자
     * @throws AccountNotFoundException 주어진 사용자 식별자에 해당하는 사용자가 없는 경우
     */
    public Account findAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * 주어진 사용자 이메일에 해당하는 사용자을 반환한다.
     *
     * @param email 사용자 이메일
     * @return 조회한 사용자 정보
     * @throws AccountEmailNotFoundException 주어진 사용자 이메일에 해당하는 사용자가 없는 경우
     */
    public Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountEmailNotFoundException(email));
    }

    /**
     * 주어진 사용자 식별자에 해당하는 사용자을 반환한다.
     *
     * @param id 사용자 식별자
     * @return 조회한 사용자 정보
     * @throws AccountNotFoundException 주어진 사용자 식별자에 해당하는 사용자가 없는 경우
     * */
    public AccountDto.AccountResultDto getAccount(Long id) {
        return accountRepository.findById(id)
                .map(AccountDto.AccountResultDto::of)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * 주어진 사진 정보로 사용자를 생성하고 생성된 사용자 정보를 반환한다.
     * 인증이 완료되면 인증번호를 삭제한다.
     *
     * @param accountCreateDto 사용자 정보
     * @param uploadFile 사진 정보
     * @return 생성된 사용자 정보
     * @throws EmailNotAuthenticatedException 주어진 이메일에 해당하는 인증번호가 없는 경우
     * @throws AccountNicknameDuplicatedException 주어진 사용자 닉네임이 이미 존재하는 경우
     */
    public AccountDto.AccountCreateResultDto createAccount(
            AccountDto.AccountCreateDto accountCreateDto,
            UploadFile uploadFile
    ) {
        String email = accountCreateDto.getEmail();
        if (accountRepository.existsByEmail(email)) {
            throw new AccountEmailDuplicatedException(email);
        }

        EmailAuthentication emailAuthentication = getAuthenticationNumber(email);
        String authenticationNumber = emailAuthentication.getAuthenticationNumber();
        if (!emailAuthentication.isSameWith(authenticationNumber)) {
            throw new EmailNotAuthenticatedException(authenticationNumber);
        }

        String nickname = accountCreateDto.getNickname();
        boolean nicknameDuplicated = accountRepository.existsByNickname(nickname);
        if (nicknameDuplicated) {
            throw new AccountNicknameDuplicatedException(nickname);
        }

        Account account = accountCreateDto.toEntity();
        if(uploadFile != null) {
            account.addUploadFile(uploadFile);
        }

        account.updatePassword(account.getPassword(), passwordEncoder);
        Account createdAccount = accountRepository.save(account);

        Role role = Role.builder()
                .email(account.getEmail())
                .name("USER")
                .build();
        roleRepository.save(role);

        deleteEmailAuthentication(emailAuthentication.getEmail());

        return AccountDto.AccountCreateResultDto.of(createdAccount);
    }

    /**
     * 주어진 사용자 식별자와 수정할 사용자정보, 사진정보로 사용자를 수정하고 반환한다.
     *
     * @param id 사용자 식별자
     * @param accountUpdateDto 수정할 사용자 정보
     * @param uploadFile 수정할 사진 정보
     * @return 사용자 정보
     * @throws AccountPasswordBadRequestException 저장된 사용자 비밀번호와 주어진 비밀번호가 다른 경우
     * @throws AccountNicknameDuplicatedException 수정할 사용자 닉네임이 이미 존재하는 경우
     */
    public AccountDto.AccountUpdateResultDto updateAccount(
            Long id,
            AccountDto.AccountUpdateDto accountUpdateDto,
            UploadFile uploadFile
    ) {
        Account account = findAccount(id);

        String password = accountUpdateDto.getPassword();
        if (!account.isPasswordSameWith(password, passwordEncoder)) {
            throw new AccountPasswordBadRequestException();
        }

        String nickname = accountUpdateDto.getNickname();
        if (isNicknameDuplicated(id, nickname)) {
            throw new AccountNicknameDuplicatedException(nickname);
        }
        account.updateNickname(nickname);

        if(uploadFile != null) {
            uploadFileService.deleteUploadFile(accountUpdateDto.getSavedFileName());
            account.addUploadFile(uploadFile);
        }

        return AccountDto.AccountUpdateResultDto.of(account);
    }

    /**
     * 주어진 사용자 이메일로 인증번호를 반환한다.
     *
     * @param email 사용자 이메일
     * @return 조회한 인증번호
     * @throws EmailNotAuthenticatedException 주어진 사용자 이메일에 해당하는 인증번호가 없는 경우
     */
    public EmailAuthentication getAuthenticationNumber(String email) {
        return emailAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotAuthenticatedException(email));
    }

    /**
     * 주어진 사용자 이메일에 해당하는 인증번호를 삭제하고 반환한다.
     *
     * @param email 사용자 이메일
     * @return 삭제한 인증번호
     */
    public EmailAuthentication deleteEmailAuthentication(String email) {
        EmailAuthentication emailAuthentication = getAuthenticationNumber(email);
        emailAuthenticationRepository.delete(emailAuthentication);
        return emailAuthentication;
    }

    /**
     * 주어진 사용자 식별자에 해당하는 사용자를 삭제하고 반환한다.
     *
     * @param id 사용자 식별자
     * @return 삭제한 사용자 정보
     */
    public AccountDto.AccountDeleteResultDto deleteAccount(Long id) {
        Account account = findAccount(id);
        account.delete();

        return AccountDto.AccountDeleteResultDto.of(account);
    }

    /**
     * 주어진 사용자 식별자와 닉네임으로 닉네임 중복검사를 하고 여부를 반환한다.
     *
     * @param id 사용자 식별자
     * @param nickname 수정할 사용자 닉네임
     * @return 닉네임 중복 여부
     */
    public boolean isNicknameDuplicated(Long id, String nickname) {
        return accountRepository.existsByIdNotAndNickname(id, nickname);
    }

    /**
     * 주어진 사용자 식별자와 수정할 비밀번호로 비밀번호를 수정하고 사용자 정보를 반환한다.
     *
     * @param id 사용자 식별자
     * @param accountUpdatePasswordDto 수정할 사용자 정보
     * @return 수정된 사용자 정보
     * @throws AccountPasswordBadRequestException 저장된 비밀번호와 주어진 비밀번호가 다른 경우
     * @throws AccountNewPasswordNotMatchedException 주어진 비밀번호와 비밀번호 확인이 다른 경우
     */
    public AccountDto.AccountUpdatePasswordResultDto updatePassword(
            Long id,
            AccountDto.AccountUpdatePasswordDto accountUpdatePasswordDto
    ) {
        Account account = findAccount(id);

        String password = accountUpdatePasswordDto.getPassword();
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new AccountPasswordBadRequestException();
        }

        String newPassword = accountUpdatePasswordDto.getNewPassword();
        String newPasswordConfirmed = accountUpdatePasswordDto.getNewPasswordConfirmed();
        if (!newPassword.equals(newPasswordConfirmed)) {
            throw new AccountNewPasswordNotMatchedException();
        }

        account.updatePassword(accountUpdatePasswordDto.getNewPassword(), passwordEncoder);

        return AccountDto.AccountUpdatePasswordResultDto.of(account);
    }

    /**
     * 모든 사용자 수를 반환한다.
     *
     * @return 모든 사용자 수
     */
    public long getAllAccountsCount() {
        return accountRepository.getAllAccountsCount();
    }
}
