var main = {
    init : function () {
        var _this = this;
        $('#btn-login').on('click', function () {
            _this.login();
        });

        $('#btn-sendMessage').on('click', function () {
            _this.sendMessage();
        });

        $('#btn-save').on('click', function () {
            _this.save();
        });

        $('#btn-update').on('click', function () {
            _this.update();
        });

        $('#btn-user-delete').on('click', function () {
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
            location.href = '/studys';
        }).fail(function (request) {
            alert(request.responseText);
            location.herf = '/studys';
        });
    },

    applyStudy : function() {
        var id = $('#id').val()
        $.ajax({
            type: 'POST',
            url: '/api/study/apply/' + id,
            dataType: 'json',
            contentType:'application/json;',
        }).done(function(data) {
            alert("신청이 완료되었습니다.");
            location.href = '/studys';
        }).fail(function (request) {
            alert(request.responseText);
            location.herf = '/studys';
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
            location.href = data;
        }).fail(function (request) {
            location.href = '/';
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

    save : function () {
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

    update : function () {
        var id = $('#id').val();
        var formData = new FormData();
        var uploadFile = document.getElementById("uploadFile").files[0];
        var nickname = $('#nickname').val();
        var password = $('#password').val();
        var newPassword = $('#newPassword').val();
        formData.append("uploadFile", uploadFile);
        formData.append("nickname", nickname);
        formData.append("password", password);
        formData.append("newPassword", newPassword);

        $.ajax({
            type: 'PATCH',
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
            alert(request.responseText);
        });
    },

    deleteUser : function () {
        var id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/users/' + id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function() {
            alert('사용자 삭제가 완료되었습니다.');
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
            alert(request.responseText);
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
            alert(request.responseText);
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
