// AddressBar
window.onload = function() { 


setTimeout(scrollTo, 100, 0, 1); }


// SignIn Modal
$(document).ready(function(){
    $("#showSignInModal").click(function() {
        $("div#signInModal").addClass("show");
            return false;
    });

    $("#closeSignIn").click(function() {
        $("div#signInModal").removeClass("show");
            return false;
    });
});


// Back to Top
$(function() {
    $('a[href*=#wrap]').click(function() {
        if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
        location.pathname.replace
            var target = $(this.hash);
            target = target.length && target;
            if (target.length) {
                var sclpos = 30;
                var scldurat = 1200;
                var targetOffset = target.offset().top - sclpos;
                $('html,body')
                    .animate({scrollTop: targetOffset}, {duration: scldurat, easing: "easeOutExpo"});
                return false;
            }
        }
    });
});


// Modal DialogBox
$('.modalDialog').simpleDialog({
    showCloseLabel: false
});
