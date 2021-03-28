var main = {
    init : function () {
        var _this = this;
        $('#btn-sendMessage').on('click', function () {
            _this.sendMessage();
        });

        $('#btn-save').on('click', function () {
            _this.save();
        });

        $('#btn-update').on('click', function () {
            _this.update();
        });

        $('#btn-save-study').on('click', function () {
            _this.saveStudy();
        });

        $('#btn-update-study').on('click', function () {
            _this.updateStudy();
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
        var data = {
            name: $('#name').val(),
            email: $('#email').val(),
            nickname: $('#nickname').val(),
            password: $('#password').val(),
            profileImage: $('#profileImage').val(),
            authenticationNumber: $('#authenticationNumber').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/study',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('회원가입이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    update : function () {
        var id = $('#id').val();
        var data = {
            nickname: $('#nickname').val(),
            password: $('#password').val(),
            profileImage: $('#profileImage').val(),
        };

        $.ajax({
            type: 'PATCH',
            url: '/api/users/' + id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('회원정보가 수정되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    },

    saveStudy : function () {
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
    }
};

main.init();
