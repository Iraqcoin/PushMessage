$('#quiz-1 #ans-a').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-1 .title-question').addClass("hide");
    $('#quiz-1 .title-error').addClass("show");
    $('.list-q .b-1').addClass('no-active');
    $('#quiz-1 .layer').addClass("show");
    $('#quiz-1 .btn-disabled').addClass("hide");
    $('#quiz-1 .btn-next').toggle('slide', { direction: 'right' }, 400);
});
$('#quiz-1 #ans-b').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-1 .title-question').addClass("hide");
    $('#quiz-1 .title-error').addClass("show");
    $('.list-q .b-1').addClass('no-active');
    $('#quiz-1 .layer').addClass("show");
    $('#quiz-1 .btn-disabled').addClass("hide");
    $('#quiz-1 .btn-next').toggle('slide', { direction: 'right' }, 400);
});
$('#quiz-1 #ans-c').click(function() {
    $(this).parent('li').addClass( "active" );
    $('#quiz-1 .title-question').addClass("hide");
    $('#quiz-1 .title-yes').addClass("show");
    $('.list-q .b-1').addClass('active');
    $('#quiz-1 .layer').addClass("show");
    $('#quiz-1 .btn-disabled').addClass("hide");
    $('#quiz-1 .btn-next').toggle('slide', { direction: 'right' }, 400);
    updatePoint();
});
$('#quiz-1 #ans-d').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-1 .title-question').addClass("hide");
    $('#quiz-1 .title-error').addClass("show");
    $('.list-q .b-1').addClass('no-active');
    $('#quiz-1 .layer').addClass("show");
    $('#quiz-1 .btn-disabled').addClass("hide");
    $('#quiz-1 .btn-next').toggle('slide', { direction: 'right' }, 400);
});

$('#quiz-2 #ans-a').click(function() {
    $(this).parent('li').addClass( "active" );
    $('#quiz-2 .title-question').addClass("hide");
    $('#quiz-2 .title-yes').addClass("show");
    $('.list-q .b-2').addClass('active');
    $('#quiz-2 .layer').addClass("show");
    $('#quiz-2 .btn-disabled').addClass("hide");
    $('#quiz-2 .btn-next').toggle('slide', { direction: 'right' }, 400);  
    updatePoint();
});
$('#quiz-2 #ans-b').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-2 .title-question').addClass("hide");
    $('#quiz-2 .title-error').addClass("show");
    $('.list-q .b-2').addClass('no-active');
    $('#quiz-2 .layer').addClass("show");
    $('#quiz-2 .btn-disabled').addClass("hide");
    $('#quiz-2 .btn-next').toggle('slide', { direction: 'right' }, 400);
});
$('#quiz-2 #ans-c').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-2 .title-question').addClass("hide");
    $('#quiz-2 .title-error').addClass("show");
    $('.list-q .b-2').addClass('no-active');
    $('#quiz-2 .layer').addClass("show");
    $('#quiz-2 .btn-disabled').addClass("hide");
    $('#quiz-2 .btn-next').toggle('slide', { direction: 'right' }, 400);
});

$('#quiz-3 #ans-a').click(function() {
    $(this).parent('li').addClass( "active" );
    $('#quiz-3 .title-question').addClass("hide");
    $('#quiz-3 .title-yes').addClass("show");
    $('.list-q .b-3').addClass('active');
    $('#quiz-3 .layer').addClass("show");
    $('#quiz-3 .btn-disabled').addClass("hide");
    $('#quiz-3 .btn-next').toggle('slide', { direction: 'right' }, 400);
    updatePoint();
});
$('#quiz-3 #ans-b').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-3 .title-question').addClass("hide");
    $('#quiz-3 .title-error').addClass("show");
    $('.list-q .b-3').addClass('no-active');
    $('#quiz-3 .layer').addClass("show");
    $('#quiz-3 .btn-disabled').addClass("hide");
    $('#quiz-3 .btn-next').toggle('slide', { direction: 'right' }, 400);
});
$('#quiz-3 #ans-c').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-3 .title-question').addClass("hide");
    $('#quiz-3 .title-error').addClass("show");
    $('.list-q .b-3').addClass('no-active');
    $('#quiz-3 .layer').addClass("show");
    $('#quiz-3 .btn-disabled').addClass("hide");
    $('#quiz-3 .btn-next').toggle('slide', { direction: 'right' }, 400);
});
$('#quiz-3 #ans-d').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-3 .title-question').addClass("hide");
    $('#quiz-3 .title-error').addClass("show");
    $('.list-q .b-3').addClass('no-active');
    $('#quiz-3 .layer').addClass("show");
    $('#quiz-3 .btn-disabled').addClass("hide");
    $('#quiz-3 .btn-next').toggle('slide', { direction: 'right' }, 400);
});

$('#quiz-4 #ans-a').click(function() {
    $(this).parent('li').addClass( "active" );
    $('#quiz-4 .title-question').addClass("hide");
    $('#quiz-4 .title-yes').addClass("show");
    $('.list-q .b-4').addClass('active');
    $('#quiz-4 .layer').addClass("show");
    $('#quiz-4 .btn-disabled').addClass("hide");
    $('#quiz-4 .btn-next').toggle('slide', { direction: 'right' }, 400);
    updatePoint();
});
$('#quiz-4 #ans-b').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-4 .title-question').addClass("hide");
    $('#quiz-4 .title-error').addClass("show");
    $('.list-q .b-4').addClass('no-active');
    $('#quiz-4 .layer').addClass("show");
    $('#quiz-4 .btn-disabled').addClass("hide");
    $('#quiz-4 .btn-next').toggle('slide', { direction: 'right' }, 400);
});

$('#quiz-5 #ans-a').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-5 .title-question').addClass("hide");
    $('#quiz-5 .title-error').addClass("show");
    $('.list-q .b-5').addClass('no-active');
    $('#quiz-5 .layer').addClass("show");
    $('#quiz-5 .btn-disabled').addClass("hide");
    $('#quiz-5 .btn-next').toggle('slide', { direction: 'right' }, 400);        
});
$('#quiz-5 #ans-b').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-5 .title-question').addClass("hide");
    $('#quiz-5 .title-error').addClass("show");
    $('.list-q .b-5').addClass('no-active');
    $('#quiz-5 .layer').addClass("show");
    $('#quiz-5 .btn-disabled').addClass("hide");
    $('#quiz-5 .btn-next').toggle('slide', { direction: 'right' }, 400);
});
$('#quiz-5 #ans-c').click(function() {
    $(this).parent('li').addClass( "no-active" );
    $('#quiz-5 .title-question').addClass("hide");
    $('#quiz-5 .title-error').addClass("show");
    $('.list-q .b-5').addClass('no-active');
    $('#quiz-5 .layer').addClass("show");
    $('#quiz-5 .btn-disabled').addClass("hide");
    $('#quiz-5 .btn-next').toggle('slide', { direction: 'right' }, 400);
});
$('#quiz-5 #ans-d').click(function() {
    $(this).parent('li').addClass( "active" );
    $('#quiz-5 .title-question').addClass("hide");
    $('#quiz-5 .title-yes').addClass("show");
    $('.list-q .b-5').addClass('active');
    $('#quiz-5 .layer').addClass("show");
    $('#quiz-5 .btn-disabled').addClass("hide");
    $('#quiz-5 .btn-next').toggle('slide', { direction: 'right' }, 400);
    updatePoint();
});
function updatePoint() {
    var point =  $('.list-q .active').length;
    $("#point").val(point);
    $("#show-point").html( point );
    $("#show-point-end").html( point );
}
$(function() {
    $('#quiz-1 .btn-next').click(function() {
        $('#quiz-2').toggle('slide', { direction: 'right' }, 400);
        $('#quiz-1').hide();
    });
    $('#quiz-2 .btn-next').click(function() {
        $('#quiz-3').toggle('slide', { direction: 'right' }, 400);
        $('#quiz-2').hide();
    });
    $('#quiz-3 .btn-next').click(function() {
        $('#quiz-4').toggle('slide', { direction: 'right' }, 400);
        $('#quiz-3').hide();
    });
    $('#quiz-4 .btn-next').click(function() {
        $('#quiz-5').toggle('slide', { direction: 'right' }, 400);
        $('#quiz-4').hide();
    });
    $('#quiz-5 .btn-next').click(function() {
        $('#quiz-end').toggle('slide', { direction: 'right' }, 400);
        $('#quiz-5').hide();
        $('.quiz-page').addClass("stick-quiz-final");
    });
});