function fadeInModal(id) {
    $(id).fadeIn();
    $('.modal').css({
        "top": (($(window).height())/2+$(window).scrollTop())+"px",
        "left": (($(window).width())/2+$(window).scrollLeft())+"px"
    });
}

function fadeOutModal(id) {
    $(id).fadeOut();
}
