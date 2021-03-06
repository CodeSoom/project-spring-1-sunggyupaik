var main = {
    init : function () {
        var _this = this;
        $('#btn-login').on('click', function () {
            _this.login();
        });

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

    cancelStudy : function() {
        var id = $('#id').val()
        $.ajax({
            type: 'DELETE',
            url: '/api/study/apply/' + id,
            dataType: 'json',
            contentType:'application/json;',
        }).done(function(data) {
            alert("취소가 완료되었습니다.");
            location.href = '/studys/open';
        }).fail(function (request) {
            alert(request.responseText);
            location.herf = '/studys/open';
        });
    },

    applyStudy : function() {
        var id = $('#id').val()
        $.ajax({
            type: 'POST',
            url: '/api/study/apply/' + id,
            dataType: 'json',
            contentType:'application/json;',
        }).done(function() {
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

    login : function() {
        var data = {
            email: $('#email').val(),
            password: $('#password').val()
        }
        $.ajax({
            type: 'POST',
            url: '/login/signup',
            dataType: 'json',
            contentType:'application/json;',
            data: JSON.stringify(data)
        }).done(function(data) {
            alert("로그인이 완료되었습니다.");
            location.href = '/';
        }).fail(function (request) {
            alert("아이디 혹은 비밀번호를 확인하세요.");
        });
    },

    sendMessage : function() {
        var data = {
            email: $('#email').val()
        }
        $("#email").attr("readonly",true);
        alert('메일 전송을 시작합니다.');

        $.ajax({
            type: 'POST',
            url: '/api/email/authentication',
            dataType: 'text',
            contentType:'application/json;',
            data: JSON.stringify(data)
        }).done(function() {
            alert("메일 전송이 완료되었습니다.");
        }).fail(function (request) {
            $("#email").attr("readonly",false);
            alert(request.responseText);
        });
    },

    saveUser : function () {
        var formData = new FormData();
        var uploadFile = document.getElementById("uploadFile").files[0];
        var name = $('#name').val();
        var email = $('#email').val();
        var nickname = $('#nickname').val();
        var password = $('#password').val();
        var authenticationNumber = $('#authenticationNumber').val();
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
        }).done(function() {
            alert('회원가입이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    updateUser : function () {
        var id = $('#id').val();
        var formData = new FormData();
        var uploadFile = document.getElementById("uploadFile").files[0];
        var nickname = $('#nickname').val();
        var password = $('#password').val();
        formData.append("uploadFile", uploadFile);
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
        }).done(function() {
            alert('회원정보가 수정되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            if(request.responseText.match("in the past")) {
                alert("시작날짜를 다시 확인해주세요");
            } else if(request.responseText.match("StartDate and EndDate")) {
                alert("시작날짜와 종료날짜를 다시 확인해주세요");
            } else if(request.responseText.match("StartTime and EndTime")) {
                alert("시작시간과 종료시간을 다시 확인해주세요");
            }  else if(request.responseText.match("Password")) {
                alert("비밀번호가 틀렸습니다.");
            } else if(request.responseText.match("Nickname")) {
                alert("닉네임이 중복되었습니다.");
            } else {
                alert(request.responseText);
            }
        });
    },

    updateUserPassword : function() {
        var id = $('#id').val();
        var data = {
            password: $('#password').val(),
            newPassword: $('#newPassword').val(),
            newPasswordConfirmed: $('#newPasswordConfirmed').val()
        };

        $.ajax({
            type: 'PATCH',
            url: 'api/users/password/' + id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('비밀번호 수정이 완료되었습니다.');
            window.location.href = '/users/update/' + id;
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    deleteUser : function () {
        var id = $('#id').val();
        var result = confirm("정말로 탈퇴하시겠습니까?");
        if(!result) {
            return;
        }

        $.ajax({
            type: 'DELETE',
            url: '/api/users/' + id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function() {
            alert('사용자 탈퇴가 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    saveStudy : function () {
        var data = {
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
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('스터디 생성이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            if(request.responseText.match("in the past")) {
                alert("시작날짜를 다시 확인해주세요");
            } else if(request.responseText.match("StartDate and EndDate")) {
                alert("시작날짜와 종료날짜를 다시 확인해주세요");
            } else if(request.responseText.match("StartTime and EndTime")) {
                alert("시작시간과 종료시간을 다시 확인해주세요");
            } else {
                alert(request.responseText);
            }
        });
    },

    updateStudy : function () {
        var id = $('#id').val();
        var data = {
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
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('스터디 수정이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            if(request.responseText.match("in the past")) {
                alert("시작날짜를 다시 확인해주세요");
            } else if(request.responseText.match("StartDate and EndDate")) {
                alert("시작날짜와 종료날짜를 다시 확인해주세요");
            } else if(request.responseText.match("StartTime and EndTime")) {
                alert("시작시간과 종료시간을 다시 확인해주세요");
            } else {
                alert(request.responseText);
            }
        });
    },

    deleteStudy : function () {
        var id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/study/' + id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function() {
            alert('스터디 삭제가 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    }
};

main.init();
