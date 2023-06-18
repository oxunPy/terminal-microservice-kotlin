$(document).ready(function () {
    // Task: pastki menyu tanlanganda parent obektga sinf qo'shish
    // nav-main
    // nav-main-item        -  open -> nav-main-link ni active qilganizda open qialsiz
    // nav-main-submenu     - submenu
    // nav-main-item        - bu sub menu da ishlatilgan
    // nav-main-link        - active -> click bolganida buni active qialsiz,
    $('#nav-main').find('li').click(function(){
        //removing the previous selected menu state
        $('#nav-main').find('li').removeClass('active');

        //is this element from the second level menu?
        if($(this).closest('ul').hasClass('nav-main-submenu')){
            $(this).parents('li').addClass('active');

            //this is a parent element
        }else{
            $(this).addClass('active');
        }
    });
});

function escapeHtml(unsafe) {
    return unsafe != null ?
        unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#x27;")
        .replace(/\//g, "&#x2F;") : null;
}

