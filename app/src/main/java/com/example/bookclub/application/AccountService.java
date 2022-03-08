package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import com.example.bookclub.errors.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.repository.account.JpaAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 사용자 조회, 생성, 수정, 삭제, 인증번호 조회, 삭제, 닉네임 중복검사, 비밀번호 변경, 사용자 수 조회를 한다
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
     * 주어진 사용자 아이디에 해당하는 계정을 반환한다.
     *
     * @param id 사용자 아이디 식별자
     * @return 조회한 사용자
     * @throws AccountNotFoundException 주어진 아이디에 해당하는 사용자가 없는 경우
     */
    public Account findAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * 주어진 사용자 이메일에 해당하는 계정을 반환한다.
     *
     * @param email 사용자 이메일 식별자
     * @return 조회한 사용자
     * @throws AccountEmailNotFoundException 주어진 이메일에 해당하는 사용자가 없는 경우
     */
    public Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountEmailNotFoundException(email));
    }

    /**
     * 주어진 사용자 아이디에 해당하는 계정을 반환한다.
     *
     * @param id 사용자 아이디 식별자
     * @return 조회한 사용자 정보
     * @throws AccountNotFoundException 주어진 아이디에 해당하는 사용자가 없는 경우
     * */
    public AccountResultDto getAccount(Long id) {
        return accountRepository.findById(id)
                .map(AccountResultDto::of)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * 주어진 사용자와 사진 정보로 사용자를 생성하고 반환한다.
     * 인증이 완료되면 인증번호를 삭제한다.
     *
     * @param accountCreateDto 사용자 정보
     * @param uploadFile 사진 정보
     * @return 생성된 사용자 정보
     * @throws EmailNotAuthenticatedException 주어진 이메일에 해당하는 인증번호가 없는 경우
     * @throws AccountNicknameDuplicatedException 주어진 사용자 닉네임이 이미 존재하는 경우
     */
    public AccountResultDto createAccount(AccountCreateDto accountCreateDto, UploadFile uploadFile) {
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

        return AccountResultDto.of(createdAccount);
    }

    /**
     * 주어진 아이디와 수정할 사용자정보, 사진정보로 사용자를 수정하고 반환한다.
     *
     * @param id 사용자 아이디 식별자
     * @param accountUpdateDto 수정할 사용자 정보
     * @param uploadFile 수정할 사진 정보
     * @return 사용자 정보
     * @throws AccountPasswordBadRequestException 저장된 사용자 비밀번호와 입력한 비밀번호가 다른 경우
     * @throws AccountNicknameDuplicatedException 수정할 사용자 닉네임이 이미 존재하는 경우
     */
    public AccountResultDto updateUser(Long id, AccountUpdateDto accountUpdateDto,
                                       UploadFile uploadFile) {
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

        return AccountResultDto.of(account);
    }

    /**
     * 주어진 이메일로 인증번호를 반환한다.
     *
     * @param email 사용자 이메일 식별자
     * @return 조회한 인증번호
     * @throws EmailNotAuthenticatedException 주어진 이메일에 해당하는 인증번호가 없는 경우
     */
    public EmailAuthentication getAuthenticationNumber(String email) {
        return emailAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotAuthenticatedException(email));
    }

    /**
     * 주어진 이메일에 해당하는 인증번호를 삭제하고 반환한다.
     *
     * @param email 사용자 이메일 식별자
     * @return 삭제한 인증번호
     */
    public EmailAuthentication deleteEmailAuthentication(String email) {
        EmailAuthentication emailAuthentication = getAuthenticationNumber(email);
        emailAuthenticationRepository.delete(emailAuthentication);
        return emailAuthentication;
    }

    /**
     * 주어진 아이디에 해당하는 계정을 삭제하고 반환한다.
     *
     * @param id 사용자 아이디 식별자
     * @return 삭제한 사용자 정보
     */
    public AccountResultDto deleteAccount(Long id) {
        Account account = findAccount(id);
        account.delete();

        return AccountResultDto.of(account);
    }

    /**
     * 주어진 아이디와 닉네임으로 닉네임 중복검사를 하고 여부를 반환한다.
     *
     * @param id 사용자 아이디 식별자
     * @param nickname 사용자 닉네임 식별자
     * @return 닉네임 중복 여부
     */
    public boolean isNicknameDuplicated(Long id, String nickname) {
        return accountRepository.existsByIdNotAndNickname(id, nickname);
    }

    /**
     * 주어진 아이디와 수정할 비밀번호로 비밀번호를 수정하고 사용자 정보를 반환한다.
     *
     * @param id 사용자 아이디 식별자
     * @param accountUpdatePasswordDto 사용자 수정 정보
     * @return 수정된 사용자 정보
     * @throws AccountPasswordBadRequestException 저장된 비밀번호와 입력한 비밀번호가 다른 경우
     * @throws AccountNewPasswordNotMatchedException 입력한 비밀번호와 비밀번호 확인이 다른 경우
     */
    public AccountResultDto updatePassword(Long id, AccountUpdatePasswordDto accountUpdatePasswordDto) {
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

        return AccountResultDto.of(account);
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
