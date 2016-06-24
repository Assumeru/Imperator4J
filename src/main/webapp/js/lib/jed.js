/**
 * @preserve jed.js https://github.com/SlexAxton/Jed
 */
/*
-----------
A gettext compatible i18n library for modern JavaScript Applications

by Alex Sexton - AlexSexton [at] gmail - @SlexAxton
WTFPL license for use
Dojo CLA for contributions

Jed offers the entire applicable GNU gettext spec'd set of
functions, but also offers some nicer wrappers around them.
The api for gettext was written for a language with no function
overloading, so Jed allows a little more of that.

Many thanks to Joshua I. Miller - unrtst@cpan.org - who wrote
gettext.js back in 2008. I was able to vet a lot of my ideas
against his. I also made sure Jed passed against his tests
in order to offer easy upgrades -- jsgettext.berlios.de
*/
!function(t,e){function r(t){return u.PF.compile(t||"nplurals=2; plural=(n != 1);")}function n(t,e){this._key=t,this._i18n=e}var i=Array.prototype,s=Object.prototype,o=i.slice,l=s.hasOwnProperty,a=i.forEach,h={},c={forEach:function(t,e,r){var n,i,s;if(null!==t)if(a&&t.forEach===a)t.forEach(e,r);else if(t.length===+t.length){for(n=0,i=t.length;i>n;n++)if(n in t&&e.call(r,t[n],n,t)===h)return}else for(s in t)if(l.call(t,s)&&e.call(r,t[s],s,t)===h)return},extend:function(t){return this.forEach(o.call(arguments,1),function(e){for(var r in e)t[r]=e[r]}),t}},u=function(t){if(this.defaults={locale_data:{messages:{"":{domain:"messages",lang:"en",plural_forms:"nplurals=2; plural=(n != 1);"}}},domain:"messages",debug:!1},this.options=c.extend({},this.defaults,t),this.textdomain(this.options.domain),t.domain&&!this.options.locale_data[this.options.domain])throw new Error("Text domain set to non-existent domain: `"+t.domain+"`")};u.context_delimiter=String.fromCharCode(4),c.extend(n.prototype,{onDomain:function(t){return this._domain=t,this},withContext:function(t){return this._context=t,this},ifPlural:function(t,e){return this._val=t,this._pkey=e,this},fetch:function(t){return"[object Array]"!={}.toString.call(t)&&(t=[].slice.call(arguments,0)),(t&&t.length?u.sprintf:function(t){return t})(this._i18n.dcnpgettext(this._domain,this._context,this._key,this._pkey,this._val),t)}}),c.extend(u.prototype,{translate:function(t){return new n(t,this)},textdomain:function(t){return t?void(this._textdomain=t):this._textdomain},gettext:function(t){return this.dcnpgettext.call(this,e,e,t)},dgettext:function(t,r){return this.dcnpgettext.call(this,t,e,r)},dcgettext:function(t,r){return this.dcnpgettext.call(this,t,e,r)},ngettext:function(t,r,n){return this.dcnpgettext.call(this,e,e,t,r,n)},dngettext:function(t,r,n,i){return this.dcnpgettext.call(this,t,e,r,n,i)},dcngettext:function(t,r,n,i){return this.dcnpgettext.call(this,t,e,r,n,i)},pgettext:function(t,r){return this.dcnpgettext.call(this,e,t,r)},dpgettext:function(t,e,r){return this.dcnpgettext.call(this,t,e,r)},dcpgettext:function(t,e,r){return this.dcnpgettext.call(this,t,e,r)},npgettext:function(t,r,n,i){return this.dcnpgettext.call(this,e,t,r,n,i)},dnpgettext:function(t,e,r,n,i){return this.dcnpgettext.call(this,t,e,r,n,i)},dcnpgettext:function(t,e,n,i,s){i=i||n,t=t||this._textdomain;var o;if(!this.options)return o=new u,o.dcnpgettext.call(o,void 0,void 0,n,i,s);if(!this.options.locale_data)throw new Error("No locale data provided.");if(!this.options.locale_data[t])throw new Error("Domain `"+t+"` was not found.");if(!this.options.locale_data[t][""])throw new Error("No locale meta information provided.");if(!n)throw new Error("No translation key found.");var l,a,h,c=e?e+u.context_delimiter+n:n,p=this.options.locale_data,f=p[t],d=(p.messages||this.defaults.locale_data.messages)[""],g=f[""].plural_forms||f[""]["Plural-Forms"]||f[""]["plural-forms"]||d.plural_forms||d["Plural-Forms"]||d["plural-forms"];if(void 0===s)h=0;else{if("number"!=typeof s&&(s=parseInt(s,10),isNaN(s)))throw new Error("The number that was passed in is not a number.");h=r(g)(s)}if(!f)throw new Error("No domain named `"+t+"` could be found.");return l=f[c],!l||h>l.length?(this.options.missing_key_callback&&this.options.missing_key_callback(c,t),a=[n,i],this.options.debug===!0&&console.log(a[r(g)(s)]),a[r()(s)]):(a=l[h],a?a:(a=[n,i],a[r()(s)]))}});var p=function(){function t(t){return Object.prototype.toString.call(t).slice(8,-1).toLowerCase()}function e(t,e){for(var r=[];e>0;r[--e]=t);return r.join("")}var r=function(){return r.cache.hasOwnProperty(arguments[0])||(r.cache[arguments[0]]=r.parse(arguments[0])),r.format.call(null,r.cache[arguments[0]],arguments)};return r.format=function(r,n){var i,s,o,l,a,h,c,u=1,f=r.length,d="",g=[];for(s=0;f>s;s++)if(d=t(r[s]),"string"===d)g.push(r[s]);else if("array"===d){if(l=r[s],l[2])for(i=n[u],o=0;o<l[2].length;o++){if(!i.hasOwnProperty(l[2][o]))throw p('[sprintf] property "%s" does not exist',l[2][o]);i=i[l[2][o]]}else i=l[1]?n[l[1]]:n[u++];if(/[^s]/.test(l[8])&&"number"!=t(i))throw p("[sprintf] expecting number but found %s",t(i));switch(("undefined"==typeof i||null===i)&&(i=""),l[8]){case"b":i=i.toString(2);break;case"c":i=String.fromCharCode(i);break;case"d":i=parseInt(i,10);break;case"e":i=l[7]?i.toExponential(l[7]):i.toExponential();break;case"f":i=l[7]?parseFloat(i).toFixed(l[7]):parseFloat(i);break;case"o":i=i.toString(8);break;case"s":i=(i=String(i))&&l[7]?i.substring(0,l[7]):i;break;case"u":i=Math.abs(i);break;case"x":i=i.toString(16);break;case"X":i=i.toString(16).toUpperCase()}i=/[def]/.test(l[8])&&l[3]&&i>=0?"+"+i:i,h=l[4]?"0"==l[4]?"0":l[4].charAt(1):" ",c=l[6]-String(i).length,a=l[6]?e(h,c):"",g.push(l[5]?i+a:a+i)}return g.join("")},r.cache={},r.parse=function(t){for(var e=t,r=[],n=[],i=0;e;){if(null!==(r=/^[^\x25]+/.exec(e)))n.push(r[0]);else if(null!==(r=/^\x25{2}/.exec(e)))n.push("%");else{if(null===(r=/^\x25(?:([1-9]\d*)\$|\(([^\)]+)\))?(\+)?(0|'[^$])?(-)?(\d+)?(?:\.(\d+))?([b-fosuxX])/.exec(e)))throw"[sprintf] huh?";if(r[2]){i|=1;var s=[],o=r[2],l=[];if(null===(l=/^([a-z_][a-z_\d]*)/i.exec(o)))throw"[sprintf] huh?";for(s.push(l[1]);""!==(o=o.substring(l[0].length));)if(null!==(l=/^\.([a-z_][a-z_\d]*)/i.exec(o)))s.push(l[1]);else{if(null===(l=/^\[(\d+)\]/.exec(o)))throw"[sprintf] huh?";s.push(l[1])}r[2]=s}else i|=2;if(3===i)throw"[sprintf] mixing positional and named placeholders is not (yet) supported";n.push(r)}e=e.substring(r[0].length)}return n},r}(),f=function(t,e){return e.unshift(t),p.apply(null,e)};u.parse_plural=function(t,e){return t=t.replace(/n/g,e),u.parse_expression(t)},u.sprintf=function(t,e){return"[object Array]"=={}.toString.call(e)?f(t,[].slice.call(e)):p.apply(this,[].slice.call(arguments))},u.prototype.sprintf=function(){return u.sprintf.apply(this,arguments)},u.PF={},u.PF.parse=function(t){var e=u.PF.extractPluralExpr(t);return u.PF.parser.parse.call(u.PF.parser,e)},u.PF.compile=function(t){function e(t){return t===!0?1:t?t:0}var r=u.PF.parse(t);return function(t){return e(u.PF.interpreter(r)(t))}},u.PF.interpreter=function(t){return function(e){switch(t.type){case"GROUP":return u.PF.interpreter(t.expr)(e);case"TERNARY":return u.PF.interpreter(t.expr)(e)?u.PF.interpreter(t.truthy)(e):u.PF.interpreter(t.falsey)(e);case"OR":return u.PF.interpreter(t.left)(e)||u.PF.interpreter(t.right)(e);case"AND":return u.PF.interpreter(t.left)(e)&&u.PF.interpreter(t.right)(e);case"LT":return u.PF.interpreter(t.left)(e)<u.PF.interpreter(t.right)(e);case"GT":return u.PF.interpreter(t.left)(e)>u.PF.interpreter(t.right)(e);case"LTE":return u.PF.interpreter(t.left)(e)<=u.PF.interpreter(t.right)(e);case"GTE":return u.PF.interpreter(t.left)(e)>=u.PF.interpreter(t.right)(e);case"EQ":return u.PF.interpreter(t.left)(e)==u.PF.interpreter(t.right)(e);case"NEQ":return u.PF.interpreter(t.left)(e)!=u.PF.interpreter(t.right)(e);case"MOD":return u.PF.interpreter(t.left)(e)%u.PF.interpreter(t.right)(e);case"VAR":return e;case"NUM":return t.val;default:throw new Error("Invalid Token found.")}}},u.PF.extractPluralExpr=function(t){t=t.replace(/^\s\s*/,"").replace(/\s\s*$/,""),/;\s*$/.test(t)||(t=t.concat(";"));var e,r=/nplurals\=(\d+);/,n=/plural\=(.*);/,i=t.match(r),s={};if(!(i.length>1))throw new Error("nplurals not found in plural_forms string: "+t);if(s.nplurals=i[1],t=t.replace(r,""),e=t.match(n),!(e&&e.length>1))throw new Error("`plural` expression not found: "+t);return e[1]},u.PF.parser=function(){var t={trace:function(){},yy:{},symbols_:{error:2,expressions:3,e:4,EOF:5,"?":6,":":7,"||":8,"&&":9,"<":10,"<=":11,">":12,">=":13,"!=":14,"==":15,"%":16,"(":17,")":18,n:19,NUMBER:20,$accept:0,$end:1},terminals_:{2:"error",5:"EOF",6:"?",7:":",8:"||",9:"&&",10:"<",11:"<=",12:">",13:">=",14:"!=",15:"==",16:"%",17:"(",18:")",19:"n",20:"NUMBER"},productions_:[0,[3,2],[4,5],[4,3],[4,3],[4,3],[4,3],[4,3],[4,3],[4,3],[4,3],[4,3],[4,3],[4,1],[4,1]],performAction:function(t,e,r,n,i,s,o){var l=s.length-1;switch(i){case 1:return{type:"GROUP",expr:s[l-1]};case 2:this.$={type:"TERNARY",expr:s[l-4],truthy:s[l-2],falsey:s[l]};break;case 3:this.$={type:"OR",left:s[l-2],right:s[l]};break;case 4:this.$={type:"AND",left:s[l-2],right:s[l]};break;case 5:this.$={type:"LT",left:s[l-2],right:s[l]};break;case 6:this.$={type:"LTE",left:s[l-2],right:s[l]};break;case 7:this.$={type:"GT",left:s[l-2],right:s[l]};break;case 8:this.$={type:"GTE",left:s[l-2],right:s[l]};break;case 9:this.$={type:"NEQ",left:s[l-2],right:s[l]};break;case 10:this.$={type:"EQ",left:s[l-2],right:s[l]};break;case 11:this.$={type:"MOD",left:s[l-2],right:s[l]};break;case 12:this.$={type:"GROUP",expr:s[l-1]};break;case 13:this.$={type:"VAR"};break;case 14:this.$={type:"NUM",val:Number(t)}}},table:[{3:1,4:2,17:[1,3],19:[1,4],20:[1,5]},{1:[3]},{5:[1,6],6:[1,7],8:[1,8],9:[1,9],10:[1,10],11:[1,11],12:[1,12],13:[1,13],14:[1,14],15:[1,15],16:[1,16]},{4:17,17:[1,3],19:[1,4],20:[1,5]},{5:[2,13],6:[2,13],7:[2,13],8:[2,13],9:[2,13],10:[2,13],11:[2,13],12:[2,13],13:[2,13],14:[2,13],15:[2,13],16:[2,13],18:[2,13]},{5:[2,14],6:[2,14],7:[2,14],8:[2,14],9:[2,14],10:[2,14],11:[2,14],12:[2,14],13:[2,14],14:[2,14],15:[2,14],16:[2,14],18:[2,14]},{1:[2,1]},{4:18,17:[1,3],19:[1,4],20:[1,5]},{4:19,17:[1,3],19:[1,4],20:[1,5]},{4:20,17:[1,3],19:[1,4],20:[1,5]},{4:21,17:[1,3],19:[1,4],20:[1,5]},{4:22,17:[1,3],19:[1,4],20:[1,5]},{4:23,17:[1,3],19:[1,4],20:[1,5]},{4:24,17:[1,3],19:[1,4],20:[1,5]},{4:25,17:[1,3],19:[1,4],20:[1,5]},{4:26,17:[1,3],19:[1,4],20:[1,5]},{4:27,17:[1,3],19:[1,4],20:[1,5]},{6:[1,7],8:[1,8],9:[1,9],10:[1,10],11:[1,11],12:[1,12],13:[1,13],14:[1,14],15:[1,15],16:[1,16],18:[1,28]},{6:[1,7],7:[1,29],8:[1,8],9:[1,9],10:[1,10],11:[1,11],12:[1,12],13:[1,13],14:[1,14],15:[1,15],16:[1,16]},{5:[2,3],6:[2,3],7:[2,3],8:[2,3],9:[1,9],10:[1,10],11:[1,11],12:[1,12],13:[1,13],14:[1,14],15:[1,15],16:[1,16],18:[2,3]},{5:[2,4],6:[2,4],7:[2,4],8:[2,4],9:[2,4],10:[1,10],11:[1,11],12:[1,12],13:[1,13],14:[1,14],15:[1,15],16:[1,16],18:[2,4]},{5:[2,5],6:[2,5],7:[2,5],8:[2,5],9:[2,5],10:[2,5],11:[2,5],12:[2,5],13:[2,5],14:[2,5],15:[2,5],16:[1,16],18:[2,5]},{5:[2,6],6:[2,6],7:[2,6],8:[2,6],9:[2,6],10:[2,6],11:[2,6],12:[2,6],13:[2,6],14:[2,6],15:[2,6],16:[1,16],18:[2,6]},{5:[2,7],6:[2,7],7:[2,7],8:[2,7],9:[2,7],10:[2,7],11:[2,7],12:[2,7],13:[2,7],14:[2,7],15:[2,7],16:[1,16],18:[2,7]},{5:[2,8],6:[2,8],7:[2,8],8:[2,8],9:[2,8],10:[2,8],11:[2,8],12:[2,8],13:[2,8],14:[2,8],15:[2,8],16:[1,16],18:[2,8]},{5:[2,9],6:[2,9],7:[2,9],8:[2,9],9:[2,9],10:[2,9],11:[2,9],12:[2,9],13:[2,9],14:[2,9],15:[2,9],16:[1,16],18:[2,9]},{5:[2,10],6:[2,10],7:[2,10],8:[2,10],9:[2,10],10:[2,10],11:[2,10],12:[2,10],13:[2,10],14:[2,10],15:[2,10],16:[1,16],18:[2,10]},{5:[2,11],6:[2,11],7:[2,11],8:[2,11],9:[2,11],10:[2,11],11:[2,11],12:[2,11],13:[2,11],14:[2,11],15:[2,11],16:[2,11],18:[2,11]},{5:[2,12],6:[2,12],7:[2,12],8:[2,12],9:[2,12],10:[2,12],11:[2,12],12:[2,12],13:[2,12],14:[2,12],15:[2,12],16:[2,12],18:[2,12]},{4:30,17:[1,3],19:[1,4],20:[1,5]},{5:[2,2],6:[1,7],7:[2,2],8:[1,8],9:[1,9],10:[1,10],11:[1,11],12:[1,12],13:[1,13],14:[1,14],15:[1,15],16:[1,16],18:[2,2]}],defaultActions:{6:[2,1]},parseError:function(t,e){throw new Error(t)},parse:function(t){function e(t){i.length=i.length-2*t,s.length=s.length-t,o.length=o.length-t}function r(){var t;return t=n.lexer.lex()||1,"number"!=typeof t&&(t=n.symbols_[t]||t),t}var n=this,i=[0],s=[null],o=[],l=this.table,a="",h=0,c=0,u=0,p=2,f=1;this.lexer.setInput(t),this.lexer.yy=this.yy,this.yy.lexer=this.lexer,"undefined"==typeof this.lexer.yylloc&&(this.lexer.yylloc={});var d=this.lexer.yylloc;o.push(d),"function"==typeof this.yy.parseError&&(this.parseError=this.yy.parseError);for(var g,y,x,m,_,b,w,P,E,k={};;){if(x=i[i.length-1],this.defaultActions[x]?m=this.defaultActions[x]:(null==g&&(g=r()),m=l[x]&&l[x][g]),"undefined"==typeof m||!m.length||!m[0]){if(!u){E=[];for(b in l[x])this.terminals_[b]&&b>2&&E.push("'"+this.terminals_[b]+"'");var v="";v=this.lexer.showPosition?"Parse error on line "+(h+1)+":\n"+this.lexer.showPosition()+"\nExpecting "+E.join(", ")+", got '"+this.terminals_[g]+"'":"Parse error on line "+(h+1)+": Unexpected "+(1==g?"end of input":"'"+(this.terminals_[g]||g)+"'"),this.parseError(v,{text:this.lexer.match,token:this.terminals_[g]||g,line:this.lexer.yylineno,loc:d,expected:E})}if(3==u){if(g==f)throw new Error(v||"Parsing halted.");c=this.lexer.yyleng,a=this.lexer.yytext,h=this.lexer.yylineno,d=this.lexer.yylloc,g=r()}for(;;){if(p.toString()in l[x])break;if(0==x)throw new Error(v||"Parsing halted.");e(1),x=i[i.length-1]}y=g,g=p,x=i[i.length-1],m=l[x]&&l[x][p],u=3}if(m[0]instanceof Array&&m.length>1)throw new Error("Parse Error: multiple actions possible at state: "+x+", token: "+g);switch(m[0]){case 1:i.push(g),s.push(this.lexer.yytext),o.push(this.lexer.yylloc),i.push(m[1]),g=null,y?(g=y,y=null):(c=this.lexer.yyleng,a=this.lexer.yytext,h=this.lexer.yylineno,d=this.lexer.yylloc,u>0&&u--);break;case 2:if(w=this.productions_[m[1]][1],k.$=s[s.length-w],k._$={first_line:o[o.length-(w||1)].first_line,last_line:o[o.length-1].last_line,first_column:o[o.length-(w||1)].first_column,last_column:o[o.length-1].last_column},_=this.performAction.call(k,a,c,h,this.yy,m[1],s,o),"undefined"!=typeof _)return _;w&&(i=i.slice(0,-1*w*2),s=s.slice(0,-1*w),o=o.slice(0,-1*w)),i.push(this.productions_[m[1]][0]),s.push(k.$),o.push(k._$),P=l[i[i.length-2]][i[i.length-1]],i.push(P);break;case 3:return!0}}return!0}},e=function(){var t={EOF:1,parseError:function(t,e){if(!this.yy.parseError)throw new Error(t);this.yy.parseError(t,e)},setInput:function(t){return this._input=t,this._more=this._less=this.done=!1,this.yylineno=this.yyleng=0,this.yytext=this.matched=this.match="",this.conditionStack=["INITIAL"],this.yylloc={first_line:1,first_column:0,last_line:1,last_column:0},this},input:function(){var t=this._input[0];this.yytext+=t,this.yyleng++,this.match+=t,this.matched+=t;var e=t.match(/\n/);return e&&this.yylineno++,this._input=this._input.slice(1),t},unput:function(t){return this._input=t+this._input,this},more:function(){return this._more=!0,this},pastInput:function(){var t=this.matched.substr(0,this.matched.length-this.match.length);return(t.length>20?"...":"")+t.substr(-20).replace(/\n/g,"")},upcomingInput:function(){var t=this.match;return t.length<20&&(t+=this._input.substr(0,20-t.length)),(t.substr(0,20)+(t.length>20?"...":"")).replace(/\n/g,"")},showPosition:function(){var t=this.pastInput(),e=new Array(t.length+1).join("-");return t+this.upcomingInput()+"\n"+e+"^"},next:function(){if(this.done)return this.EOF;this._input||(this.done=!0);var t,e,r;this._more||(this.yytext="",this.match="");for(var n=this._currentRules(),i=0;i<n.length;i++)if(e=this._input.match(this.rules[n[i]]))return r=e[0].match(/\n.*/g),r&&(this.yylineno+=r.length),this.yylloc={first_line:this.yylloc.last_line,last_line:this.yylineno+1,first_column:this.yylloc.last_column,last_column:r?r[r.length-1].length-1:this.yylloc.last_column+e[0].length},this.yytext+=e[0],this.match+=e[0],this.matches=e,this.yyleng=this.yytext.length,this._more=!1,this._input=this._input.slice(e[0].length),this.matched+=e[0],t=this.performAction.call(this,this.yy,this,n[i],this.conditionStack[this.conditionStack.length-1]),t?t:void 0;return""===this._input?this.EOF:void this.parseError("Lexical error on line "+(this.yylineno+1)+". Unrecognized text.\n"+this.showPosition(),{text:"",token:null,line:this.yylineno})},lex:function(){var t=this.next();return"undefined"!=typeof t?t:this.lex()},begin:function(t){this.conditionStack.push(t)},popState:function(){return this.conditionStack.pop()},_currentRules:function(){return this.conditions[this.conditionStack[this.conditionStack.length-1]].rules},topState:function(){return this.conditionStack[this.conditionStack.length-2]},pushState:function(t){this.begin(t)}};return t.performAction=function(t,e,r,n){switch(r){case 0:break;case 1:return 20;case 2:return 19;case 3:return 8;case 4:return 9;case 5:return 6;case 6:return 7;case 7:return 11;case 8:return 13;case 9:return 10;case 10:return 12;case 11:return 14;case 12:return 15;case 13:return 16;case 14:return 17;case 15:return 18;case 16:return 5;case 17:return"INVALID"}},t.rules=[/^\s+/,/^[0-9]+(\.[0-9]+)?\b/,/^n\b/,/^\|\|/,/^&&/,/^\?/,/^:/,/^<=/,/^>=/,/^</,/^>/,/^!=/,/^==/,/^%/,/^\(/,/^\)/,/^$/,/^./],t.conditions={INITIAL:{rules:[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17],inclusive:!0}},t}();return t.lexer=e,t}(),"undefined"!=typeof exports?("undefined"!=typeof module&&module.exports&&(exports=module.exports=u),exports.Jed=u):("function"==typeof define&&define.amd&&define(function(){return u}),t.Jed=u)}(this);