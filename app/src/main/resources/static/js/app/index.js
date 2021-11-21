const main = {
    init: function () {
        const _this = this;
        $('#btn-sendMessage').on('click', function () {
            _this.sendMessage();
        });

        $('#btn-account-save').on('click', function () {
            _this.saveUser();
        });

        $('#btn-account-update').on('click', function () {
            _this.updateUser();
        });

        $('#btn-account-update-password').on('click', function () {
            _this.updateUserPassword();
        });

        $('#btn-account-delete').on('click', function () {
            _this.deleteUser();
        });

        $('#btn-save-study').on('click', function () {
            _this.saveStudy();
        });

        $('#btn-update-study').on('click', function () {
            _this.updateStudy();
        });

        $('#btn-delete-study').on('click', function () {
            _this.deleteStudy();
        });

        $('#btn-apply-study').on('click', function () {
            _this.applyStudy();
        });

        $('#btn-cancel-study').on('click', function () {
            _this.cancelStudy();
        });
    },

    cancelStudy: function () {
        const id = $('#id').val();
        $.ajax({
            type: 'DELETE',
            url: '/api/study/apply/' + id,
            dataType: 'json',
            contentType: 'application/json;',
        }).done(function (data) {
            alert("취소가 완료되었습니다.");
            location.href = '/studys/open';
        }).fail(function (request) {
            alert(request.responseText);
            location.herf = '/studys/open';
        });
    },

    applyStudy: function () {
        const id = $('#id').val();
        $.ajax({
            type: 'POST',
            url: '/api/study/apply/' + id,
            dataType: 'json',
            contentType: 'application/json;',
        }).done(function () {
            alert("신청이 완료되었습니다.");
            location.href = '/studys/open';
        }).fail(function (request) {
            if (request.responseText.match("Study already existed")) {
                alert("이미 스터디에 참여하고 있어서 신청이 불가능합니다");
            } else if (request.responseText.match("Study size is full")) {
                alert("스터디 모집인원이 꽉 찼습니다");
            } else {
                alert(request.responseText);
            }
            location.herf = '/studys/open';
        });
    },

    sendMessage: function () {
        const data = {
            email: $('#email').val()
        };
        $("#email").attr("readonly", true);
        alert('메일 전송을 시작합니다.');

        $.ajax({
            type: 'POST',
            url: '/api/email/authentication',
            dataType: 'text',
            contentType: 'application/json;',
            data: JSON.stringify(data)
        }).done(function () {
            alert("메일 전송이 완료되었습니다.");
        }).fail(function (request) {
            $("#email").attr("readonly", false);
            alert(request.responseText);
        });
    },

    saveUser: function () {
        const formData = new FormData();
        const uploadFile = document.getElementById("uploadFile").files[0];
        const name = $('#name').val();
        const email = $('#email').val();
        const nickname = $('#nickname').val();
        const password = $('#password').val();
        const authenticationNumber = $('#authenticationNumber').val();
        formData.append("uploadFile", uploadFile);
        formData.append("name", name);
        formData.append("email", email);
        formData.append("nickname", nickname);
        formData.append("password", password);
        formData.append("authenticationNumber", authenticationNumber);

        $.ajax({
            type: 'POST',
            url: '/api/users',
            dataType: 'json',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData
        }).done(function () {
            alert('회원가입이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    updateUser: function () {
        const id = $('#id').val();
        const formData = new FormData();
        const uploadFile = document.getElementById("uploadFile").files[0];
        const savedFileName = $('#savedFileName').val();
        const nickname = $('#nickname').val();
        const password = $('#password').val();
        formData.append("uploadFile", uploadFile);
        formData.append("savedFileName", savedFileName);
        formData.append("nickname", nickname);
        formData.append("password", password);

        $.ajax({
            type: 'POST',
            url: '/api/users/' + id,
            dataType: 'json',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            data: formData
        }).done(function () {
            alert('회원정보가 수정되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    updateUserPassword: function () {
        const id = $('#id').val();
        const data = {
            password: $('#password').val(),
            newPassword: $('#newPassword').val(),
            newPasswordConfirmed: $('#newPasswordConfirmed').val()
        };

        $.ajax({
            type: 'PATCH',
            url: 'api/users/password/' + id,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('비밀번호 수정이 완료되었습니다.');
            window.location.href = '/users/update/' + id;
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    deleteUser: function () {
        const id = $('#id').val();
        const result = confirm("정말로 탈퇴하시겠습니까?");
        if (!result) {
            return;
        }

        $.ajax({
            type: 'DELETE',
            url: '/api/users/' + id,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
        }).done(function () {
            alert('사용자 탈퇴가 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    saveStudy: function () {
        const data = {
            name: $('#name').val(),
            bookName: $('#bookName').val(),
            bookImage: $('#bookImage').attr('src'),
            description: $('#description').val(),
            contact: $('#contact').val(),
            size: $('#size').val(),
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val(),
            startTime: $('#startTime').val(),
            endTime: $('#endTime').val(),
            day: $('#day option:selected').val(),
            studyState: $('#studyState option:selected').val(),
            zone: $('#zone option:selected').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/study',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('스터디 생성이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            if (request.responseText.match("in the past")) {
                alert("시작날짜를 다시 확인해주세요");
            } else if (request.responseText.match("StartDate and EndDate")) {
                alert("시작날짜와 종료날짜를 다시 확인해주세요");
            } else if (request.responseText.match("StartTime and EndTime")) {
                alert("시작시간과 종료시간을 다시 확인해주세요");
            } else {
                alert(request.responseText);
            }
        });
    },

    updateStudy: function () {
        const id = $('#id').val();
        const data = {
            name: $('#name').val(),
            description: $('#description').val(),
            contact: $('#contact').val(),
            size: $('#size').val(),
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val(),
            startTime: $('#startTime').val(),
            endTime: $('#endTime').val(),
            day: $('#day option:selected').val(),
            studyState: $('#studyState option:selected').val(),
            zone: $('#zone option:selected').val()
        };

        $.ajax({
            type: 'PATCH',
            url: '/api/study/' + id,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('스터디 수정이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            if (request.responseText.match("in the past")) {
                alert("시작날짜를 다시 확인해주세요");
            } else if (request.responseText.match("StartDate and EndDate")) {
                alert("시작날짜와 종료날짜를 다시 확인해주세요");
            } else if (request.responseText.match("StartTime and EndTime")) {
                alert("시작시간과 종료시간을 다시 확인해주세요");
            } else {
                alert(request.responseText);
            }
        });
    },

    deleteStudy: function () {
        const id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/study/' + id,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
        }).done(function () {
            alert('스터디 삭제가 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    }
};

main.init();
