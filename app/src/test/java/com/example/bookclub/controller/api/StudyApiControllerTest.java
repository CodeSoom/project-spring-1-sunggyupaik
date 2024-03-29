package com.example.bookclub.controller.api;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.study.StudyCommentLikeService;
import com.example.bookclub.application.study.StudyCommentService;
import com.example.bookclub.application.study.StudyFavoriteService;
import com.example.bookclub.application.study.StudyLikeService;
import com.example.bookclub.application.study.StudyService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Day;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.domain.study.Zone;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.dto.UploadFileDto;
import com.example.bookclub.common.exception.account.AccountNotManagerOfStudyException;
import com.example.bookclub.common.exception.study.StudyAlreadyExistedException;
import com.example.bookclub.common.exception.study.StudyAlreadyInOpenOrCloseException;
import com.example.bookclub.common.exception.study.studycomment.StudyCommentContentNotExistedException;
import com.example.bookclub.common.exception.study.studycomment.StudyCommentDeleteBadRequest;
import com.example.bookclub.common.exception.study.studycommentlike.StudyCommentLikeAlreadyExistedException;
import com.example.bookclub.common.exception.study.studycommentlike.StudyCommentLikeNotFoundException;
import com.example.bookclub.common.exception.study.studycomment.StudyCommentNotFoundException;
import com.example.bookclub.common.exception.study.favorite.StudyFavoriteAlreadyExistedException;
import com.example.bookclub.common.exception.study.favorite.StudyFavoriteNotExistedException;
import com.example.bookclub.common.exception.study.studylike.StudyLikeAlreadyExistedException;
import com.example.bookclub.common.exception.study.studylike.StudyLikeNotExistedException;
import com.example.bookclub.common.exception.study.StudyNotAppliedBefore;
import com.example.bookclub.common.exception.study.StudyNotFoundException;
import com.example.bookclub.common.exception.study.StudyNotInOpenStateException;
import com.example.bookclub.common.exception.study.StudySizeFullException;
import com.example.bookclub.common.exception.study.StudyStartAndEndDateNotValidException;
import com.example.bookclub.common.exception.study.StudyStartAndEndTimeNotValidException;
import com.example.bookclub.common.exception.study.StudyStartDateInThePastException;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
import com.example.bookclub.security.UserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.bookclub.common.util.ApiDocumentUtils.getDocumentRequest;
import static com.example.bookclub.common.util.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyApiController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ExtendWith({RestDocumentationExtension.class})
class StudyApiControllerTest {
    private static final Long ACCOUNT_ID = 2L;
    private static final String ACCOUNT_NAME = "accountName";
    private static final String ACCOUNT_EMAIL = "email";
    private static final String ACCOUNT_NICKNAME = "accountNickname";
    private static final String ACCOUNT_PASSWORD = "accountPassword";

    private static final Long ACCOUNT_SECOND_ID = 3L;
    private static final String ACCOUNT_SECOND_NAME = "accountSecondName";
    private static final String ACCOUNT_SECOND_EMAIL = "accountSecondEmail";
    private static final String ACCOUNT_SECOND_NICKNAME = "accountSecondNickname";
    private static final String ACCOUNT_SECOND_PASSWORD = "accountSecondPassword";
    private static final String ACCOUNT_UPDATE_EMAIL = ACCOUNT_SECOND_EMAIL;

    private static final Long STUDY_SETUP_EXISTED_ID = 1L;
    private static final String STUDY_SETUP_NAME = "setupStudyName";
    private static final String STUDY_SETUP_BOOK_NAME = "setupStudyBookName";
    private static final String STUDY_SETUP_BOOK_IMAGE = "setupStudyBookImage";
    private static final String STUDY_SETUP_EMAIL = ACCOUNT_SECOND_EMAIL;
    private static final String STUDY_SETUP_DESCRIPTION = "setupStudyDescription";
    private static final String STUDY_SETUP_CONTACT = "setupContact";
    private static final int STUDY_SETUP_SIZE = 5;
    private static final LocalDate STUDY_SETUP_START_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate STUDY_SETUP_END_DATE = LocalDate.now().plusDays(7);
    private static final Day STUDY_SETUP_DAY = Day.MONDAY;
    private static final String STUDY_SETUP_START_TIME = "13:00";
    private static final String STUDY_SETUP_END_TIME = "15:30";
    private static final StudyState STUDY_SETUP_STUDY_STATE = StudyState.OPEN;
    private static final Zone STUDY_SETUP_ZONE = Zone.SEOUL;
    private static final String STUDY_SETUP_CREATED_BY = "createdBy";
    private static final String STUDY_SETUP_UPDATED_BY = "updatedBy";
    private static final LocalDateTime STUDY_SETUP_CREATED_DATE = LocalDateTime.now();
    private static final LocalDateTime STUDY_SETUP_UPDATED_DATE = LocalDateTime.now();
    private static final String STUDY_UPDATED_DATE = "2022-04-14 12:00:00";

    private static final String STUDY_UPDATE_NAME = "studyUpdatedName";
    private static final String STUDY_UPDATE_DESCRIPTION = "studyUpdatedDescription";
    private static final String STUDY_UPDATE_CONTACT = "studyUpdatedContact";
    private static final int STUDY_UPDATE_SIZE = 10;
    private static final LocalDate STUDY_UPDATE_START_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate STUDY_UPDATE_END_DATE = LocalDate.now().plusDays(5);
    private static final Day STUDY_UPDATE_DAY = Day.THURSDAY;
    private static final String STUDY_UPDATE_START_TIME = "12:00";
    private static final String STUDY_UPDATE_END_TIME = "14:30";
    private static final StudyState STUDY_UPDATE_STUDY_STATE = StudyState.OPEN;
    private static final Zone STUDY_UPDATE_ZONE = Zone.BUSAN;

    private static final Long STUDY_FULL_SIZE_ID = 4L;
    private static final int STUDY_FULL_SIZE = 10;
    private static final int STUDY_FULL_SIZE_APPLY_COUNT = 10;

    private static final Long STUDY_NOT_EXISTED_ID = 999L;
    private static final Long STUDY_CLOSED_ID = 5L;
    private static final Long ACCOUNT_CLOSED_STUDY_ID = 6L;
    private static final int STUDY_CLOSED_SIZE = 10;
    private static final LocalDate CREATE_START_DATE_PAST = LocalDate.now().minusDays(1);

    private static final Long STUDY_COMMENT_EXISTED_ID = 7L;
    private static final Long STUDY_COMMENT_NOT_EXISTED_ID = 8L;
    private static final String STUDY_COMMENT_CONTENT = "studyCommentContent";

    private static final Long STUDY_LIKE_CREATE_ID = 9L;
    private static final Long STUDY_COMMENT_LIKE_CREATE_ID = 10L;

    private static final Long STUDY_FAVORITE_CREATE_ID = 11L;
    private static final Long STUDY_FAVORITE_NOT_EXISTED_ID = 12L;

    private static final Long FILE_CREATED_ID = 13L;
    private static final String FILE_CREATED_NAME = "createdFileName.jpg";
    private static final String FILE_CREATED_ORIGINAL_NAME = "createdOriginalName.jpg";
    private static final String FILE_CREATED_URL = "createdFileUrl";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    private StudyService studyService;

    @MockBean
    private StudyLikeService studyLikeService;

    @MockBean
    private StudyCommentService studyCommentService;

    @MockBean
    private StudyCommentLikeService studyCommentLikeService;

    @MockBean
    private StudyFavoriteService studyFavoriteService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountAuthenticationService accountAuthenticationService;

    @MockBean
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomEntryPoint customEntryPoint;

    @MockBean
    private CustomDeniedHandler customDeniedHandler;

    @MockBean
    private PersistTokenRepository tokenRepository;

    private Account accountWithoutStudy;
    private Account accountWithSetupStudy;
    private Account accountWithClosedStudy;
    private UsernamePasswordAuthenticationToken accountWithoutStudyToken;
    private UsernamePasswordAuthenticationToken accountWithSetupStudyToken;
    private UsernamePasswordAuthenticationToken accountWithClosedStudyToken;
    private Study setUpStudy;
    private Study updatedStudy;
    private Study dateNotValidStudy;
    private Study fullSizeStudy;
    private Study closedStudy;

    private StudyApiDto.StudyCreateDto studyCreateDto;
    private StudyApiDto.StudyCreateDto studyStartDateIsPastCreateDto;
    private StudyApiDto.StudyCreateDto studyStartDateIsAfterEndDateCreateDto;
    private StudyApiDto.StudyCreateDto studyStartTimeIsAfterEndTimeCreateDto;
    private StudyApiDto.StudyCreateDto studyAlreadyInOpenOrCloseCreateDto;

    private StudyApiDto.StudyUpdateDto studyUpdateDto;
    private StudyApiDto.StudyUpdateDto studyStartDateIsPastUpdateDto;
    private StudyApiDto.StudyUpdateDto studyStartDateIsAfterEndDateUpdateDto;

    private StudyApiDto.StudyResultDto studyResultDto;
    private StudyApiDto.StudyResultDto updatedStudyResultDto;

    private StudyApiDto.StudyCommentCreateDto studyCommentCreateDto;
    private StudyApiDto.StudyCommentCreateDto studyCommentCreateWithoutContentDto;
    private StudyApiDto.StudyCommentResultDto studyCommentResultDto;

    private StudyApiDto.StudyApplyResultDto studyApplyResultDto;
    private StudyApiDto.StudyLikeResultDto studyLikeResultDto;
    private StudyApiDto.StudyLikesCommentResultDto studyLikesCommentResultDto;
    private StudyApiDto.StudyFavoriteResultDto studyFavoriteResultDto;

    private UploadFileDto.UploadFileResultDto uploadFileResultDto;

    private List<Study> list;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .alwaysDo(print())
                .build();

        accountWithoutStudy = Account.builder()
                .id(ACCOUNT_ID)
                .name(ACCOUNT_NAME)
                .email(ACCOUNT_EMAIL)
                .nickname(ACCOUNT_NICKNAME)
                .password(ACCOUNT_PASSWORD)
                .build();

        accountWithSetupStudy = Account.builder()
                .id(ACCOUNT_SECOND_ID)
                .name(ACCOUNT_SECOND_NAME)
                .email(ACCOUNT_SECOND_EMAIL)
                .nickname(ACCOUNT_SECOND_NICKNAME)
                .password(ACCOUNT_SECOND_PASSWORD)
                .build();

        accountWithClosedStudy = Account.builder()
                .id(ACCOUNT_CLOSED_STUDY_ID)
                .build();

        setUpStudy = Study.builder()
                .id(STUDY_SETUP_EXISTED_ID)
                .name(STUDY_SETUP_NAME)
                .bookName(STUDY_SETUP_BOOK_NAME)
                .bookImage(STUDY_SETUP_BOOK_IMAGE)
                .email(ACCOUNT_SECOND_EMAIL)
                .description(STUDY_SETUP_DESCRIPTION)
                .contact(STUDY_SETUP_CONTACT)
                .size(STUDY_SETUP_SIZE)
                .startDate(STUDY_SETUP_START_DATE)
                .endDate(STUDY_SETUP_END_DATE)
                .startTime(STUDY_SETUP_START_TIME)
                .endTime(STUDY_SETUP_END_TIME)
                .day(STUDY_SETUP_DAY)
                .studyState(STUDY_SETUP_STUDY_STATE)
                .zone(STUDY_SETUP_ZONE)
                .build();

        setUpStudy.setCreatedDate(STUDY_SETUP_CREATED_DATE);
        setUpStudy.setUpdatedDate(STUDY_SETUP_UPDATED_DATE);
        setUpStudy.setCreatedBy(STUDY_SETUP_CREATED_BY);
        setUpStudy.setUpdatedBy(STUDY_SETUP_UPDATED_BY);
        setUpStudy.addAdmin(accountWithSetupStudy);

        updatedStudy = Study.builder()
                .id(STUDY_SETUP_EXISTED_ID)
                .name(STUDY_UPDATE_NAME)
                .bookName(STUDY_SETUP_BOOK_NAME)
                .bookImage(STUDY_SETUP_BOOK_IMAGE)
                .email(ACCOUNT_UPDATE_EMAIL)
                .description(STUDY_UPDATE_DESCRIPTION)
                .contact(STUDY_UPDATE_CONTACT)
                .size(STUDY_UPDATE_SIZE)
                .startDate(STUDY_UPDATE_START_DATE)
                .endDate(STUDY_UPDATE_END_DATE)
                .startTime(STUDY_UPDATE_START_TIME)
                .endTime(STUDY_UPDATE_END_TIME)
                .day(STUDY_UPDATE_DAY)
                .studyState(STUDY_UPDATE_STUDY_STATE)
                .zone(STUDY_UPDATE_ZONE)
                .build();

        updatedStudy.setCreatedDate(STUDY_SETUP_CREATED_DATE);
        updatedStudy.setUpdatedDate(STUDY_SETUP_UPDATED_DATE);
        updatedStudy.setCreatedBy(STUDY_SETUP_CREATED_BY);
        updatedStudy.setUpdatedBy(STUDY_SETUP_UPDATED_BY);

        fullSizeStudy = Study.builder()
                .id(STUDY_FULL_SIZE_ID)
                .size(STUDY_FULL_SIZE)
                .applyCount(STUDY_FULL_SIZE_APPLY_COUNT)
                .build();

        closedStudy = Study.builder()
                .id(STUDY_CLOSED_ID)
                .studyState(StudyState.CLOSE)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .size(STUDY_CLOSED_SIZE)
                .build();

        closedStudy.addAccount(accountWithClosedStudy);

        accountWithoutStudyToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(accountWithoutStudy, List.of(new SimpleGrantedAuthority("USER"))),
                accountWithoutStudy.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")));

        accountWithSetupStudyToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(accountWithSetupStudy, List.of(new SimpleGrantedAuthority("USER"))),
                accountWithSetupStudy.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")));

        accountWithClosedStudyToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(accountWithClosedStudy, List.of(new SimpleGrantedAuthority("USER"))),
                accountWithClosedStudy.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")));

        studyCreateDto = StudyApiDto.StudyCreateDto.builder()
                .name(STUDY_SETUP_NAME)
                .email(STUDY_SETUP_EMAIL)
                .description(STUDY_SETUP_DESCRIPTION)
                .contact(STUDY_SETUP_CONTACT)
                .size(STUDY_SETUP_SIZE)
                .startDate(STUDY_SETUP_START_DATE)
                .endDate(STUDY_SETUP_END_DATE)
                .startTime(STUDY_SETUP_START_TIME)
                .endTime(STUDY_SETUP_END_TIME)
                .day(STUDY_SETUP_DAY)
                .zone(STUDY_SETUP_ZONE)
                .build();

        studyStartDateIsPastCreateDto = StudyApiDto.StudyCreateDto.builder()
                .startDate(CREATE_START_DATE_PAST)
                .endDate(STUDY_SETUP_END_DATE)
                .build();

        studyStartDateIsAfterEndDateCreateDto = StudyApiDto.StudyCreateDto.builder()
                .startDate(STUDY_SETUP_END_DATE)
                .endDate(STUDY_SETUP_START_DATE)
                .build();

        studyStartTimeIsAfterEndTimeCreateDto = StudyApiDto.StudyCreateDto.builder()
                .startTime(STUDY_SETUP_END_TIME)
                .endTime(STUDY_SETUP_START_TIME)
                .build();

        studyUpdateDto = StudyApiDto.StudyUpdateDto.builder()
                .name(STUDY_UPDATE_NAME)
                .description(STUDY_UPDATE_DESCRIPTION)
                .contact(STUDY_UPDATE_CONTACT)
                .size(STUDY_UPDATE_SIZE)
                .startDate(STUDY_UPDATE_START_DATE)
                .endDate(STUDY_UPDATE_END_DATE)
                .startTime(STUDY_UPDATE_START_TIME)
                .endTime(STUDY_UPDATE_END_TIME)
                .day(STUDY_UPDATE_DAY)
                .zone(STUDY_UPDATE_ZONE)
                .build();

        studyStartDateIsPastUpdateDto = StudyApiDto.StudyUpdateDto.builder()
                .startDate(CREATE_START_DATE_PAST)
                .endDate(STUDY_SETUP_END_DATE)
                .build();

        studyStartDateIsAfterEndDateUpdateDto = StudyApiDto.StudyUpdateDto.builder()
                .startDate(STUDY_SETUP_END_DATE)
                .endDate(STUDY_SETUP_START_DATE)
                .build();

        studyResultDto = StudyApiDto.StudyResultDto.of(setUpStudy);
        updatedStudyResultDto = StudyApiDto.StudyResultDto.of(updatedStudy);

        list = List.of(setUpStudy, updatedStudy);

        studyCommentCreateDto = StudyApiDto.StudyCommentCreateDto.builder()
                .content(STUDY_COMMENT_CONTENT)
                .build();

        studyCommentCreateWithoutContentDto = StudyApiDto.StudyCommentCreateDto.builder()
                .content("")
                .build();

        studyCommentResultDto = StudyApiDto.StudyCommentResultDto.builder()
                .id(STUDY_COMMENT_EXISTED_ID)
                .content(STUDY_COMMENT_CONTENT)
                .studyId(STUDY_SETUP_EXISTED_ID)
                .accountId(ACCOUNT_SECOND_ID)
                .nickname(ACCOUNT_SECOND_NICKNAME)
                .isWrittenByMe(true)
                .liked(false)
                .likesCount(0)
                .updatedDate(STUDY_UPDATED_DATE)
                .build();

        studyApplyResultDto = StudyApiDto.StudyApplyResultDto.builder()
                .id(STUDY_SETUP_EXISTED_ID)
                .build();

        studyLikeResultDto = StudyApiDto.StudyLikeResultDto.builder()
                .id(STUDY_LIKE_CREATE_ID)
                .build();

        studyLikesCommentResultDto = StudyApiDto.StudyLikesCommentResultDto.builder()
                .id(STUDY_COMMENT_EXISTED_ID)
                .build();

        studyFavoriteResultDto = StudyApiDto.StudyFavoriteResultDto.builder()
                .id(STUDY_FAVORITE_CREATE_ID)
                .build();

        uploadFileResultDto = UploadFileDto.UploadFileResultDto.builder()
                .id(FILE_CREATED_ID)
                .fileName(FILE_CREATED_NAME)
                .fileOriginalName(FILE_CREATED_ORIGINAL_NAME)
                .fileUrl(FILE_CREATED_URL)
                .build();
    }

    @Test
    void listAllStudies() throws Exception {
        given(studyService.getStudies()).willReturn(list);

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/study")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"name\":\"" + STUDY_SETUP_NAME)))
                .andExpect(content().string(StringContains.containsString("\"name\":\"" + STUDY_UPDATE_NAME)))
                .andDo(document("study-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("data.[].id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("data.[].name").type(STRING).description("이름"),
                                fieldWithPath("data.[].bookName").type(STRING).description("책 제목"),
                                fieldWithPath("data.[].bookImage").type(STRING).description("책 사진"),
                                fieldWithPath("data.[].email").type(STRING).description("이메일"),
                                fieldWithPath("data.[].description").type(STRING).description("설명"),
                                fieldWithPath("data.[].contact").type(STRING).description("연락처"),
                                fieldWithPath("data.[].size").type(NUMBER).description("정원 수"),
                                fieldWithPath("data.[].applyCount").type(NUMBER).description("지원 수"),
                                fieldWithPath("data.[].startDate").type(STRING).description("시작날짜"),
                                fieldWithPath("data.[].endDate").type(STRING).description("종료날짜"),
                                fieldWithPath("data.[].startTime").type(STRING).description("시작시간"),
                                fieldWithPath("data.[].endTime").type(STRING).description("종료시간"),
                                fieldWithPath("data.[].day").type(STRING).description("요일"),
                                fieldWithPath("data.[].studyState").type(STRING).description("스터디 상태"),
                                fieldWithPath("data.[].zone").type(STRING).description("지역"),
                                fieldWithPath("data.[].liked").type(BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("data.[].likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.[].commentsCount").type(NUMBER).description("댓글 수"),
                                fieldWithPath("data.[].favorite").type(BOOLEAN).description("즐겨찾기 여부"),
                                fieldWithPath("data.[].createdDate").type(STRING).description("생성날짜"),
                                fieldWithPath("data.[].updatedDate").type(STRING).description("수정날짜"),
                                fieldWithPath("data.[].createdBy").type(STRING).description("생성자"),
                                fieldWithPath("data.[].updatedBy").type(STRING).description("수정자"),
                                fieldWithPath("data.[].sizeFull").type(BOOLEAN).description("정원 만료 여부"),
                                fieldWithPath("data.[].notOpened").type(BOOLEAN).description("스터디 모집중 여부"),
                                fieldWithPath("data.[].alreadyStarted").type(BOOLEAN).description("스터디 진행중 여부"),
                                fieldWithPath("data.[].favorites").description("즐겨찾기 목록"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                    ));
    }

    @Test
    void detailWithExistedId() throws Exception {
        given(studyService.getStudy(STUDY_SETUP_EXISTED_ID)).willReturn(setUpStudy);

        this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("study-detail",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("data.name").type(STRING).description("이름"),
                                fieldWithPath("data.bookName").type(STRING).description("책 제목"),
                                fieldWithPath("data.bookImage").type(STRING).description("책 사진"),
                                fieldWithPath("data.email").type(STRING).description("이메일"),
                                fieldWithPath("data.description").type(STRING).description("설명"),
                                fieldWithPath("data.contact").type(STRING).description("연락처"),
                                fieldWithPath("data.size").type(NUMBER).description("정원 수"),
                                fieldWithPath("data.applyCount").type(NUMBER).description("지원 수"),
                                fieldWithPath("data.startDate").type(STRING).description("시작날짜"),
                                fieldWithPath("data.endDate").type(STRING).description("종료날짜"),
                                fieldWithPath("data.startTime").type(STRING).description("시작시간"),
                                fieldWithPath("data.endTime").type(STRING).description("종료시간"),
                                fieldWithPath("data.day").type(STRING).description("요일"),
                                fieldWithPath("data.studyState").type(STRING).description("스터디 상태"),
                                fieldWithPath("data.zone").type(STRING).description("지역"),
                                fieldWithPath("data.liked").type(BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("data.likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.commentsCount").type(NUMBER).description("댓글 수"),
                                fieldWithPath("data.favorite").type(BOOLEAN).description("즐겨찾기 여부"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void detailWithNotExistedId() throws Exception {
        given(studyService.getStudy(STUDY_NOT_EXISTED_ID)).willThrow(new StudyNotFoundException(STUDY_NOT_EXISTED_ID));

        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/study/{id}", STUDY_NOT_EXISTED_ID)
        )
                .andDo(print())
                .andExpect(content().string(containsString("Study not found")))
                .andExpect(status().isNotFound())
                .andDo(document("study-detail-not-existed-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));

        verify(studyService).getStudy(STUDY_NOT_EXISTED_ID);
    }

    @Test
    void createWithValidateAttribute() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.createStudy(eq(ACCOUNT_EMAIL), any(StudyApiDto.StudyCreateDto.class)))
                .willReturn(studyResultDto);

        this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/study")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studyCreateDto))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(studyResultDto.getId()))
                .andExpect(jsonPath("$.data.name").value(studyResultDto.getName()))
                .andExpect(jsonPath("$.data.description").value(studyResultDto.getDescription()))
                .andDo(document("study-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("Content-Type 헤더")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("bookName").description("책 제목"),
                                fieldWithPath("bookImage").description("책 사진"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("data.name").type(STRING).description("이름"),
                                fieldWithPath("data.bookName").type(STRING).description("책 제목"),
                                fieldWithPath("data.bookImage").type(STRING).description("책 사진"),
                                fieldWithPath("data.email").type(STRING).description("이메일"),
                                fieldWithPath("data.description").type(STRING).description("설명"),
                                fieldWithPath("data.contact").type(STRING).description("연락처"),
                                fieldWithPath("data.size").type(NUMBER).description("정원 수"),
                                fieldWithPath("data.applyCount").type(NUMBER).description("지원 수"),
                                fieldWithPath("data.startDate").type(STRING).description("시작날짜"),
                                fieldWithPath("data.endDate").type(STRING).description("종료날짜"),
                                fieldWithPath("data.startTime").type(STRING).description("시작시간"),
                                fieldWithPath("data.endTime").type(STRING).description("종료시간"),
                                fieldWithPath("data.day").type(STRING).description("요일"),
                                fieldWithPath("data.studyState").type(STRING).description("스터디 상태"),
                                fieldWithPath("data.zone").type(STRING).description("지역"),
                                fieldWithPath("data.liked").type(BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("data.likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.commentsCount").type(NUMBER).description("댓글 수"),
                                fieldWithPath("data.favorite").type(BOOLEAN).description("즐겨찾기 여부"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void createWithStartDateIsTodayOrBeforeInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.createStudy(eq(ACCOUNT_EMAIL), any(StudyApiDto.StudyCreateDto.class)))
                .willThrow(new StudyStartDateInThePastException());

        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/study")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studyStartDateIsPastCreateDto))
            )
                .andDo(print())
                .andExpect(content().string(containsString("Study startDate in the past")))
                .andExpect(status().isBadRequest())
                .andDo(document("study-create-invalid-start-date",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("Content-Type 헤더")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("bookName").description("책 제목"),
                                fieldWithPath("bookImage").description("책 사진"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createWithStartDateIsAfterEndDateInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.createStudy(eq(ACCOUNT_EMAIL), any(StudyApiDto.StudyCreateDto.class)))
                .willThrow(new StudyStartAndEndDateNotValidException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyStartDateIsAfterEndDateCreateDto))
                )
                    .andDo(print())
                    .andExpect(content().string(containsString("Study StartDate and EndDate not valid")))
                    .andExpect(status().isBadRequest())
                    .andDo(document("study-create-late-start-date",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(CONTENT_TYPE).description("Content-Type 헤더")
                            ),
                            requestFields(
                                    fieldWithPath("name").description("이름"),
                                    fieldWithPath("email").description("이메일"),
                                    fieldWithPath("bookName").description("책 제목"),
                                    fieldWithPath("bookImage").description("책 사진"),
                                    fieldWithPath("description").description("설명"),
                                    fieldWithPath("contact").description("연락처"),
                                    fieldWithPath("size").description("정원 수"),
                                    fieldWithPath("startDate").description("시작날짜"),
                                    fieldWithPath("endDate").description("종료날짜"),
                                    fieldWithPath("startTime").description("시작시간"),
                                    fieldWithPath("endTime").description("종료시간"),
                                    fieldWithPath("day").description("요일"),
                                    fieldWithPath("zone").description("지역")
                            ),
                            responseFields(
                                    fieldWithPath("result").type(STRING).description("결과"),
                                    fieldWithPath("message").type(STRING).description("예외 메세지"),
                                    fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                    fieldWithPath("data").description("데이터")
                            )
                    ));
    }

    @Test
    void createWithStartTimeIsAfterTimeInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.createStudy(eq(ACCOUNT_EMAIL), any(StudyApiDto.StudyCreateDto.class)))
                .willThrow(new StudyStartAndEndTimeNotValidException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyStartTimeIsAfterEndTimeCreateDto))
                )
                .andDo(print())
                .andExpect(content().string(containsString("Study StartTime and EndTime not valid")))
                .andExpect(status().isBadRequest())
                .andDo(document("study-create-late-start-time",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("Content-Type 헤더")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("bookName").description("책 제목"),
                                fieldWithPath("bookImage").description("책 사진"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createWithAccountAlreadyInStudyOpenOrClose() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyService.createStudy(eq(ACCOUNT_SECOND_EMAIL), any(StudyApiDto.StudyCreateDto.class)))
                .willThrow(new StudyAlreadyInOpenOrCloseException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyCreateDto))
                )
                .andDo(print())
                .andExpect(content().string(containsString("account already has study in open or close")))
                .andExpect(status().isBadRequest())
                .andDo(document("study-create-already-has-study",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("Content-Type 헤더")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("bookName").description("책 제목"),
                                fieldWithPath("bookImage").description("책 사진"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void updateWithValidateAttribute() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyService.updateStudy(
                eq(ACCOUNT_SECOND_EMAIL), eq(STUDY_SETUP_EXISTED_ID), any(StudyApiDto.StudyUpdateDto.class))
        )
                .willReturn(updatedStudyResultDto);

        this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studyUpdateDto))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(updatedStudyResultDto.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedStudyResultDto.getDescription()))
                .andExpect(jsonPath("$.data.contact").value(updatedStudyResultDto.getContact()))
                .andDo(document("study-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("studyState").description("스터디 상태"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("data.name").type(STRING).description("이름"),
                                fieldWithPath("data.bookName").type(STRING).description("책 제목"),
                                fieldWithPath("data.bookImage").type(STRING).description("책 사진"),
                                fieldWithPath("data.email").type(STRING).description("이메일"),
                                fieldWithPath("data.description").type(STRING).description("설명"),
                                fieldWithPath("data.contact").type(STRING).description("연락처"),
                                fieldWithPath("data.size").type(NUMBER).description("정원 수"),
                                fieldWithPath("data.applyCount").type(NUMBER).description("지원 수"),
                                fieldWithPath("data.startDate").type(STRING).description("시작날짜"),
                                fieldWithPath("data.endDate").type(STRING).description("종료날짜"),
                                fieldWithPath("data.startTime").type(STRING).description("시작시간"),
                                fieldWithPath("data.endTime").type(STRING).description("종료시간"),
                                fieldWithPath("data.day").type(STRING).description("요일"),
                                fieldWithPath("data.studyState").type(STRING).description("스터디 상태"),
                                fieldWithPath("data.zone").type(STRING).description("지역"),
                                fieldWithPath("data.liked").type(BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("data.likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.commentsCount").type(NUMBER).description("댓글 수"),
                                fieldWithPath("data.favorite").type(BOOLEAN).description("즐겨찾기 여부"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void updateWithNotManager() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.updateStudy(
                eq(ACCOUNT_EMAIL), eq(STUDY_SETUP_EXISTED_ID), any(StudyApiDto.StudyUpdateDto.class))
        )
                .willThrow(new AccountNotManagerOfStudyException());

        mockMvc.perform(
                    RestDocumentationRequestBuilders.patch("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyUpdateDto))
                )
                .andDo(print())
//                .andExpect(content().string(containsString("Not Manager Of Study")))
                .andExpect(status().isForbidden())
                .andDo(document("study-update-with-not-manager",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("studyState").description("스터디 상태"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void updateWithStartDateInPastInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyService.updateStudy(
                eq(ACCOUNT_SECOND_EMAIL), eq(STUDY_SETUP_EXISTED_ID), any(StudyApiDto.StudyUpdateDto.class))
        )
                .willThrow(new StudyStartDateInThePastException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyStartDateIsPastUpdateDto))
                )
                .andDo(print())
                //.andExpect(content().string(containsString("Study startDate in the past")))
                .andExpect(status().isBadRequest())
                .andDo(document("study-update-start-date-past",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("studyState").description("스터디 상태"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void updateWithStartDateIsAfterEndDateInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyService.updateStudy(
                eq(ACCOUNT_SECOND_EMAIL), eq(STUDY_SETUP_EXISTED_ID), any(StudyApiDto.StudyUpdateDto.class))
        )
                .willThrow(new StudyStartAndEndDateNotValidException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyStartDateIsPastUpdateDto))
                )
                .andDo(print())
//                .andExpect(content().string(containsString("Study StartDate and EndDate not valid")))
                .andExpect(status().isBadRequest())
                .andDo(document("study-update-start-date-late",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("studyState").description("스터디 상태"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void updateWithStartTimeIsAfterEndTimeInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyService.updateStudy(
                eq(ACCOUNT_SECOND_EMAIL), eq(STUDY_SETUP_EXISTED_ID), any(StudyApiDto.StudyUpdateDto.class))
        )
                .willThrow(new StudyStartAndEndDateNotValidException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyStartDateIsPastUpdateDto))
                )
                .andDo(print())
                //.andExpect(content().string(containsString("Study StartDate and EndDate not valid")))
                .andExpect(status().isBadRequest())
                .andDo(document("study-update-start-time-late",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("contact").description("연락처"),
                                fieldWithPath("size").description("정원 수"),
                                fieldWithPath("startDate").description("시작날짜"),
                                fieldWithPath("endDate").description("종료날짜"),
                                fieldWithPath("startTime").description("시작시간"),
                                fieldWithPath("endTime").description("종료시간"),
                                fieldWithPath("day").description("요일"),
                                fieldWithPath("studyState").description("스터디 상태"),
                                fieldWithPath("zone").description("지역")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteByExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyService.deleteStudy(eq(ACCOUNT_SECOND_EMAIL), eq(STUDY_SETUP_EXISTED_ID)))
                .willReturn(studyResultDto);

        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
        )
                .andDo(print())
                .andExpect(jsonPath("$.data.name").value(setUpStudy.getName()))
                .andExpect(jsonPath("$.data.description").value(setUpStudy.getDescription()))
                .andExpect(status().isNoContent())
                .andDo(document("study-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("data.name").type(STRING).description("이름"),
                                fieldWithPath("data.bookName").type(STRING).description("책 제목"),
                                fieldWithPath("data.bookImage").type(STRING).description("책 사진"),
                                fieldWithPath("data.email").type(STRING).description("이메일"),
                                fieldWithPath("data.description").type(STRING).description("설명"),
                                fieldWithPath("data.contact").type(STRING).description("연락처"),
                                fieldWithPath("data.size").type(NUMBER).description("정원 수"),
                                fieldWithPath("data.applyCount").type(NUMBER).description("지원 수"),
                                fieldWithPath("data.startDate").type(STRING).description("시작날짜"),
                                fieldWithPath("data.endDate").type(STRING).description("종료날짜"),
                                fieldWithPath("data.startTime").type(STRING).description("시작시간"),
                                fieldWithPath("data.endTime").type(STRING).description("종료시간"),
                                fieldWithPath("data.day").type(STRING).description("요일"),
                                fieldWithPath("data.studyState").type(STRING).description("스터디 상태"),
                                fieldWithPath("data.zone").type(STRING).description("지역"),
                                fieldWithPath("data.liked").type(BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("data.likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("data.commentsCount").type(NUMBER).description("댓글 수"),
                                fieldWithPath("data.favorite").type(BOOLEAN).description("즐겨찾기 여부"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void deleteWithNotManager() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.deleteStudy(eq(ACCOUNT_EMAIL), eq(STUDY_SETUP_EXISTED_ID)))
                .willThrow(AccountNotManagerOfStudyException.class);

        mockMvc.perform(
                        delete("/api/study/{id}", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
//                .andExpect(content().string(containsString("Not Manager Of Study")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteByNotExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyService.deleteStudy(eq(ACCOUNT_SECOND_EMAIL), eq(STUDY_NOT_EXISTED_ID)))
                .willThrow(new StudyNotFoundException(STUDY_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/{id}", STUDY_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-delete-not-existed-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void applyStudyByExistedAccount() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.applyStudy(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willReturn(studyApplyResultDto);

        this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/study/apply/{id}", STUDY_SETUP_EXISTED_ID)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("study-apply",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void applyStudyByNotExistedStudy() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.applyStudy(any(UserAccount.class), eq(STUDY_NOT_EXISTED_ID)))
                .willThrow(new StudyNotFoundException(STUDY_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/apply/{id}", STUDY_NOT_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-apply-not-existed-study",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void applyStudyNotInOpenState() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithClosedStudyToken);
        given(studyService.applyStudy(any(UserAccount.class), eq(STUDY_CLOSED_ID)))
                .willThrow(new StudyNotInOpenStateException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/apply/{id}", STUDY_CLOSED_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-apply-not-opened",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void applyStudyWithAlreadyHasStudy() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.applyStudy(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willThrow(new StudyAlreadyExistedException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/apply/{id}", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-apply-already-has-study",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void applyStudyWithFullSize() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.applyStudy(any(UserAccount.class), eq(STUDY_FULL_SIZE_ID)))
                .willThrow(new StudySizeFullException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/apply/{id}", STUDY_FULL_SIZE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-apply-size-full",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void cancelStudyByExistedAccount() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.cancelStudy(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willReturn(studyApplyResultDto);

        this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/study/cancel/{id}", STUDY_SETUP_EXISTED_ID)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("study-cancel",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void cancelStudyNotOpenedStudy() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithClosedStudyToken);
        given(studyService.cancelStudy(any(UserAccount.class), eq(STUDY_CLOSED_ID)))
                .willThrow(new StudyNotInOpenStateException());

        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/study/cancel/{id}", STUDY_CLOSED_ID)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-cancel-not-opened",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void cancelStudyByNotExistedStudy() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.cancelStudy(any(UserAccount.class), eq(STUDY_NOT_EXISTED_ID)))
                .willThrow(new StudyNotFoundException(STUDY_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/cancel/{id}", STUDY_NOT_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-cancel-not-existed-study",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void cancelStudyByNotAppliedStudy() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithoutStudyToken);
        given(studyService.cancelStudy(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willThrow(new StudyNotAppliedBefore());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/cancel/{id}", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-cancel-not-applied",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createLikeStudyWithExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyLikeService.like(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willReturn(studyLikeResultDto);

        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/study/like/{studyId}", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("study-like",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void createLikeStudyWithNotExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyLikeService.like(any(UserAccount.class), eq(STUDY_NOT_EXISTED_ID)))
                .willThrow(new StudyNotFoundException(STUDY_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/like/{studyId}", STUDY_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-like-not-existed-study",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createLikeStudyWithAlreadyExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyLikeService.like(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willThrow(new StudyLikeAlreadyExistedException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/like/{studyId}", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-like-already-existed",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteLikeStudyWithExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyLikeService.unLike(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willReturn(studyLikeResultDto);

        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/study/like/{studyId}", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("study-unlike",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void deleteLikeStudyWithNotExistedStudyId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyLikeService.unLike(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willThrow(new StudyNotFoundException(STUDY_SETUP_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/like/{studyId}", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-unlike-not-existed-study",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteLikeStudyLikeWithNotExistedStudyLikeId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyLikeService.unLike(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willThrow(new StudyLikeNotExistedException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/like/{studyId}", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-unlike-not-existed-study-like",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createStudyComment() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentService.createStudyComment(any(UserAccount.class),
                eq(STUDY_SETUP_EXISTED_ID), any(StudyApiDto.StudyCommentCreateDto.class)))
                .willReturn(studyCommentResultDto);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/{studyId}/comment", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyCommentCreateDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.content").value(studyCommentResultDto.getContent()))
                .andDo(document("study-comment-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        requestFields(
                                fieldWithPath("content").type(STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 댓글 식별자"),
                                fieldWithPath("data.content").type(STRING).description("내용"),
                                fieldWithPath("data.studyId").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("data.accountId").type(NUMBER).description("사용자 식별자"),
                                fieldWithPath("data.nickname").type(STRING).description("닉네임"),
                                fieldWithPath("data.writtenByMe").type(BOOLEAN).description("댓글 주인 여부"),
                                fieldWithPath("data.updatedDate").type(STRING).description("수정 날짜"),
                                fieldWithPath("data.liked").type(BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("data.likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void createStudyCommentWithoutContent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentService.createStudyComment(any(UserAccount.class),
                eq(STUDY_SETUP_EXISTED_ID), any(StudyApiDto.StudyCommentCreateDto.class)))
                .willThrow(new StudyCommentContentNotExistedException());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/{studyId}/comment", STUDY_SETUP_EXISTED_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyCommentCreateWithoutContentDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-comment-create-without-content",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 식별자")
                        ),
                        requestFields(
                                fieldWithPath("content").type(STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteStudyCommentWithExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentService.deleteStudyComment(any(UserAccount.class), eq(STUDY_COMMENT_EXISTED_ID)))
                .willReturn(studyCommentResultDto);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/comment/{studyCommentId}", STUDY_COMMENT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("study-comment-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyCommentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 댓글 식별자"),
                                fieldWithPath("data.content").type(STRING).description("내용"),
                                fieldWithPath("data.studyId").type(NUMBER).description("스터디 식별자"),
                                fieldWithPath("data.accountId").type(NUMBER).description("사용자 식별자"),
                                fieldWithPath("data.nickname").type(STRING).description("닉네임"),
                                fieldWithPath("data.writtenByMe").type(BOOLEAN).description("댓글 주인 여부"),
                                fieldWithPath("data.updatedDate").type(STRING).description("수정 날짜"),
                                fieldWithPath("data.liked").type(BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("data.likesCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void deleteStudyCommentWithNotExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentService.deleteStudyComment(any(UserAccount.class), eq(STUDY_COMMENT_NOT_EXISTED_ID)))
                .willThrow(new StudyCommentNotFoundException(STUDY_COMMENT_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/comment/{studyCommentId}", STUDY_COMMENT_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-comment-delete-not-existed-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyCommentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteStudyCommentWithNotAccountId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentService.deleteStudyComment(any(UserAccount.class), eq(STUDY_COMMENT_NOT_EXISTED_ID)))
                .willThrow(new StudyCommentDeleteBadRequest());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/comment/{studyCommentId}", STUDY_COMMENT_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-comment-delete-not-account",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyCommentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createStudyCommentLikeWithValidAttribute() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentLikeService.likeComment(any(UserAccount.class), eq(STUDY_COMMENT_EXISTED_ID)))
                .willReturn(studyLikesCommentResultDto);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/comment/{commentId}/like", STUDY_COMMENT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                //.andExpect(content().string(String.valueOf(STUDY_COMMENT_LIKE_CREATE_ID)))
                .andDo(document("study-comment-like",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("commentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 댓글 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void createStudyCommentLikeAlreadyExisted() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentLikeService.likeComment(any(UserAccount.class), eq(STUDY_COMMENT_EXISTED_ID)))
                .willThrow(StudyCommentLikeAlreadyExistedException.class);

        mockMvc.perform(
                        post("/api/study/comment/{commentId}/like", STUDY_COMMENT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createStudyCommentLikeWithNotExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentLikeService.likeComment(any(UserAccount.class), eq(STUDY_COMMENT_NOT_EXISTED_ID)))
                .willThrow(new StudyCommentNotFoundException(STUDY_COMMENT_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/comment/{commentId}/like", STUDY_COMMENT_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-comment-like-not-existed-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("commentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteStudyCommentLikeWithExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentLikeService.unlikeComment(any(UserAccount.class), eq(STUDY_COMMENT_EXISTED_ID)))
                .willReturn(studyLikesCommentResultDto);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/comment/{commentId}/unlike", STUDY_COMMENT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("study-comment-unlike",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("commentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 댓글 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void deleteStudyCommentLikeWithNotExistedStudyCommentId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentLikeService.unlikeComment(any(UserAccount.class), eq(STUDY_COMMENT_NOT_EXISTED_ID)))
                .willThrow(new StudyCommentNotFoundException(STUDY_COMMENT_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/comment/{commentId}/unlike", STUDY_COMMENT_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-comment-unlike-not-existed-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("commentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteStudyCommentLikeWithNotExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyCommentLikeService.unlikeComment(any(UserAccount.class), eq(STUDY_COMMENT_EXISTED_ID)))
                .willThrow(new StudyCommentLikeNotFoundException(STUDY_COMMENT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/comment/{commentId}/unlike", STUDY_COMMENT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-comment-like-not-existed-like",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("commentId").description("스터디 댓글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createFavoriteStudyWithExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyFavoriteService.favoriteStudy(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willReturn(studyFavoriteResultDto);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/{id}/favorite", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("study-favorite",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 즐겨찾기 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void createFavoriteStudyAlreadyExisted() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyFavoriteService.favoriteStudy(any(UserAccount.class), eq(STUDY_SETUP_EXISTED_ID)))
                .willThrow(new StudyFavoriteAlreadyExistedException(STUDY_SETUP_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/{id}/favorite", STUDY_SETUP_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("study-favorite-already-existed",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void createFavoriteStudyWithNotExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyFavoriteService.favoriteStudy(any(UserAccount.class), eq(STUDY_NOT_EXISTED_ID)))
                .willThrow(new StudyNotFoundException(STUDY_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/study/{id}/favorite", STUDY_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-favorite-not-existed-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteFavoriteStudyWithExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyFavoriteService.unFavoriteStudy(any(UserAccount.class), eq(STUDY_FAVORITE_CREATE_ID)))
                .willReturn(studyFavoriteResultDto);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/{id}/favorite", STUDY_FAVORITE_CREATE_ID)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("study-unFavorite",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type(NUMBER).description("스터디 즐겨찾기 식별자"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void deleteFavoriteStudyWithNotExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyFavoriteService.unFavoriteStudy(any(UserAccount.class), eq(STUDY_FAVORITE_NOT_EXISTED_ID)))
                .willThrow(new StudyFavoriteNotExistedException(STUDY_FAVORITE_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/{id}/favorite", STUDY_FAVORITE_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-unfavorite-not-existed-like",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }

    @Test
    void deleteFavoriteStudyWithNotExistedStudyId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountWithSetupStudyToken);
        given(studyFavoriteService.unFavoriteStudy(any(UserAccount.class), eq(STUDY_NOT_EXISTED_ID)))
                .willThrow(new StudyNotFoundException(STUDY_NOT_EXISTED_ID));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/study/{id}/favorite", STUDY_NOT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("study-unfavorite-not-existed-study",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("스터디 식별자")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }
}
