function go_targetPage(num) {
    document.frmList.target = '';
    document.frmList.page.value = num - 1;
    document.frmList.action = "/interviews";
    document.frmList.submit();
}

function go_previous() {
    document.frmList.target = '';
    document.frmList.page.value = $(".previous").val() -1;
    document.frmList.action = "/interviews";
    document.frmList.submit();
}

function go_next() {
    document.frmList.target = '';
    document.frmList.page.value = $(".next").val() -1;
    document.frmList.action = "/interviews";
    document.frmList.submit();
}

