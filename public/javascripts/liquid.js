$(function(){
    var $ul = $('#selectCategory');
    var $li = $ul.find('li');
    var lw = $li.width();
    var lh = $li.height();
    var m = 30;
    var s = 500;
    var initWidth = $ul.width();
    var initHeight = $ul.height();

    function init() {
        $ul.addClass('jsReady');
        $li.css({
            'left': ( initWidth  - lw ) * 0.5,
            'top':  ( initHeight - lh ) * 0.5
        });
    }
    function layout() {
        var uw = initWidth || $ul.width();
        var cols = Math.floor( ( uw + m ) / ( lw + m ) );
        var rows = Math.ceil( $li.size() / cols );
        var _m = Math.floor( ( uw - cols * lw ) / ( cols + 1 ) );
        $ul.css({ 'height': rows * ( lh + _m ) - _m });
        $li.each(function(i){
            $(this)
                .stop(true)
                .animate({
                    'left': Math.round(( i % cols ) * ( lw + _m )) + _m,
                    'top': Math.floor( i / cols ) * ( lh + _m )
                }, s, 'easeInOutExpo' );
        });
        initWidth = undefined;
    }
    $(window).bind('load resize', layout);
    init();
});
