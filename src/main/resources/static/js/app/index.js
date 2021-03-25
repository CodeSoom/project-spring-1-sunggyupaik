var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });

        $('#btn-sendMessage').on('click', function () {
            _this.sendMessage();
        });
    },

    sendMessage : function() {
        var data = {
            email: $('#email').val()
        }
        alert('메일을 전송하였습니다')

        $.ajax({
            type: 'POST',
            url: '/api/email/authentication',
            dataType: 'json',
            contentType:'application/json;',
            data: JSON.stringify(data)
        }).done(function() {
            alert("메일 전송이 완료되었습니다.");
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },

    save : function () {
        var data = {
            name: $('#name').val(),
            email: $('#email').val(),
            nickname: $('#nickname').val(),
            password: $('#password').val(),
            profileImage: $('#profileImage').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/users',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('회원가입이 완료되었습니다.');
            window.location.href = '/';
        }).fail(function (request) {
            alert(request.responseText);
        });
    }
};

main.init();
