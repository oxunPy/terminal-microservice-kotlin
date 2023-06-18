##localeplanet - Localization va Internationalization uchun yaxshi kutubxona
  
##_**http://www.localeplanet.com/**_ - kutubxona websayti

##**icu.js** - JavaScript uchun International kompanentalar

_misol 1_

**oddiy xolda:** today.getDay() + '/' + today.getMonth() + '/' + today.getFullYear()

**kutubxona bilan:** icu.getDateFormat('SHORT_CENTURY').format(today)


##**translate.js** - Teks tarjima qilish funksiylari

_misol 1_

**oddiy xolda:** alert('Hello, world!');

**kutubxona bilan:** alert(_('Hello, world!'));.

_misol 2_

**oddiy xolda:** alert('Today is ' + today.getDay() + '/' + today.getMonth() + '/' + today.getFullYear());

**kutubxona bilan:** alert(_("Today is {0}", icu.getDateFormat('SHORT_CENTURY').format(today)));
