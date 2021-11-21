let changeNum = 0;

$(document).on("click", "i.del" , function() {
    $(this).parent().remove();
});

$(function() {
    $(document).on("change",".uploadFile", function() {
        changeNum += 1;
        const uploadFile = $(this);
        const files = !!this.files ? this.files : [];
        if (!files.length || !window.FileReader) return;

        if (/^image/.test( files[0].type)){
            const reader = new FileReader();
            reader.readAsDataURL(files[0]);

            reader.onloadend = function(){
                uploadFile.closest(".imgUp").find('.imagePreview').css("background-image", "url("+this.result+")");
                $(".imgPreview").hide();
            }
        }
    });
});
