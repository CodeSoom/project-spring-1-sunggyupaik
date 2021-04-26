function go_targetPage(num) {
    document.frmList.target = '';
    document.frmList.targetPage.value = num;
    document.frmList.action = "/interviews";
    document.frmList.submit();
}

$(document).ready(function(){
    var targetPage = $(".targetPage").val();
    $('.page' + targetPage).html('<strong>' + targetPage + '</strong>');

    var totalPage = $(".totalPage").val();
    var next = $(".next").val();
    if(totalPage < next) {
        $('.btn_next').hide();
        $('.btn_last').hide();
    }

    if(targetPage < 11) {
        $('.btn_prev').hide();
        $('.btn_first').hide();
    }
})
