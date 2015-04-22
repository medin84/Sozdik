//utf-8 құ

var loaderNode;
var dictionaryNode;
var historyNode;
var debugNode;

var historyObj;

dojo.ready(function(){

	loaderNode = dojo.query("#loaderNode");
	dictionaryNode = dojo.query("#dictionaryNode");
	historyNode = dojo.query("#historyNode");
	debugNode = dojo.query("#debugNode");

	dojo.connect(dojo.body(), "click", function(e){
		e.preventDefault();

		if(e.target.nodeName == "A"){
			var langFrom = dojo.attr(e.target, "langFrom");
			if(langFrom){
				toggleLoader(true);
				var langTo = dojo.attr(e.target, "langTo");
				var word = e.target.innerText;
				window.sozdik.translate(langFrom + "/" + langTo, word);
			}
		}
	});

	toggleLoader(false);

	//
	//var jsonstr = "{found: 1, time: 1358250486, lang_from: 'kk', lang_to: 'ru', word: 'мә', article: '<h2>мә</h2><p><abbr title=\"междометие\">межд.</abbr><br />1) <a href=\"/ru/dictionary/translate/ru/kk/на/\" langfrom=\"ru\" langto=\"kk\">на</a>; на возьми!<br /><span>мә, ал да кет → на, возьми и уходи</span><br /><span>мә саған → на тебе</span><br /><span>ә десе мә дейді → скажешь слово — ответит двумя <em>(букв. скажешь «а» в ответ получишь «на»)</em> </span><br />2) <em>(возглас, выражающий удивление)</em><br /><span>мә, мұнысы несі! → вот это да!</span></p><div id=\"dictionary_article_toolbar\" class=\"ig_dictionary_article_toolbar\"><a href=\"/print/ru/dictionary/translate/kk/ru/мә/\"><img class=\"ig_dictionary_print\" src=\"/images/ig_print.png\" width=\"16\" height=\"16\" alt=\"Версия для печати\" title=\"Версия для печати\" /></a></div><div class=\"ig_dictionary_article_url\"><span>Короткая ссылка:</span><input type=\"text\" id=\"dictionary_article_url\" class=\"ig_dictionary_article_url\" value=\"http://sozdik.kz/ru/hash/80111d0ed/\" /></div><div class=\"ig_dictionary_article_share\"><span>Поделиться страницей:</span><a href=\"http://www.facebook.com/sharer/sharer.php?u=http://sozdik.kz/ru/hash/80111d0ed/&amp;t=%D0%BC%D3%99\" rel=\"nofollow\"><img src=\"/images/social_networks/facebook.png\" width=\"16\" height=\"16\" alt=\"Facebook\" title=\"Facebook\" /></a><a href=\"http://twitter.com/home?status=%D0%BC%D3%99 http://sozdik.kz/ru/hash/80111d0ed/\" rel=\"nofollow\"><img src=\"/images/social_networks/twitter.png\" width=\"16\" height=\"16\" alt=\"Twitter\" title=\"Twitter\" /></a><a href=\"http://connect.mail.ru/share?share_url=http://sozdik.kz/ru/hash/80111d0ed/\" rel=\"nofollow\"><img src=\"/images/social_networks/mailru.png\" width=\"16\" height=\"16\" alt=\"Мой Мир\" title=\"Мой Мир\" /></a><a href=\"http://vk.com/share.php?url=http://sozdik.kz/ru/hash/80111d0ed/&amp;title=%D0%BC%D3%99\" rel=\"nofollow\"><img src=\"/images/social_networks/vk.png\" width=\"16\" height=\"16\" alt=\"ВКонтакте\" title=\"ВКонтакте\" /></a><a href=\"http://www.odnoklassniki.ru/dk?st.cmd=addShare&amp;st._surl=http://sozdik.kz/ru/hash/80111d0ed/\" rel=\"nofollow\"><img src=\"/images/social_networks/odnoklassniki.png\" width=\"16\" height=\"16\" alt=\"Одноклассники\" title=\"Одноклассники\" /></a></div><div class=\"ig_dictionary_article_orphus\">Нашли ошибку? Выделите ее мышью!</div>', history: 'История Ваших переводов: <a href=\"/ru/dictionary/translate/kk/ru/мә/\" langfrom=\"kk\" langto=\"ru\">мә</a>'}";

	//setTimeout(function(){ showResult(jsonstr) }, 500);
});

function toggleLoader(b){
	if(b){
		loaderNode.style("display", "block");
	} else {
		loaderNode.style("display", "none");
	}
}

function clearBody(){
	dictionaryNode.attr("innerHTML", "");
	historyNode.attr("innerHTML", "");
	debugNode.attr("innerHTML", "");
}

function showResult(jsonStr){

	try {
		var respObj = eval("(" + jsonStr + ")");

		if(respObj.found > 0){
			dictionaryNode.attr("innerHTML", respObj.article);
			//historyNode.attr("innerHTML", respObj.history);
		} else {
			dictionaryNode.attr("innerHTML", "<p class='not-found'>Слово <strong>" + respObj.word + "</strong> не найдено</p>");
		}
	} catch(e) {
		dictionaryNode.attr("innerHTML", "error<br/>" + jsonStr);
		historyNode.attr("innerHTML", "");
		debugNode.attr("innerHTML", e);
	}

	document.body.scrollTop = 0;

	toggleLoader(false);
}
