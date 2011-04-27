	function fnOpenHelpWin(url) {
			window.open(url,"help","width=450,height=500,resizable=yes,scrollbars=yes,menubar=no,toolbar=yes");
		window.close;
		}  
/*		

	function fnValRecsPerPage (elem) {
		var val = elem.value;
		if (isNaN(val)) {
			alert("Records per page must be numeric");
			elem.value = 10;
			}
		if ((val < 10) || (val > 100)) {
			alert("Records per page must be between 10 and 100");
			elem.value = 10;
			}
		}
*/
//<!--		

function fnIsBlank(s)
{
	for(var i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if((c != ' ') && (c != '\n') && (c != '\t')) return false;
	}
	return true;
}
function fnIsValidForm (theForm) {
		var msg = "";
		var isValid = true;
		
		var HasSubject = true;
		
		if (theForm.eSubject.value == "") {
			HasSubject = false;
			msg += " - missing message subject\n"
			}
			
		if (HasSubject && fnIsBlank(theForm.eSubject.value)) {
			HasSubject = false;
			msg += " - subject is blank\n"
			}
				
		if (theForm.eSubject.value.length > 150) {
			HasSubject = false;
			msg += " - message title is too long (150 character limit)"
			}
				
		//verify the body is between the min an max characters
		var ValidBody = true;
		if (theForm.eBody.value.length  == 0) {
			ValidBody = false;
			
			msg += " - missing message body\n"
		}else if(theForm.eBody.value.length > 8000)
		{
			ValidBody = false;
			msg += " - message body is limited to 8000 characters. You have entered " + theForm.eBody.value.length + " characters (includes whitespaces).\n";
		}	
		
		var CodeLengthOk = true;
		if (theForm.ePreText.value.length > 7500) {
			CodeLengthOk = false;
			msg += " - code samples are limited to 7500 characters\n"
			}
		
		if (HasSubject && ValidBody && CodeLengthOk) {isValid = true;}
		else {isValid = false;}
		
		if (!isValid) {
			msg = "The form is in error\n" + msg
			alert(msg)	
			}
		return isValid;
		}  	

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.0
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}


function getLocation () {
	var loc
	loc = document.location.protocol
	loc += '//' + document.location.host
	loc += document.location.pathname
	loc += escape(document.location.search)
	
	var navVer = navigator.appVersion;
	navVer = navVer.split(" ");
	navVer = navVer[0];
	if ((navigator.appName == "Netscape") && (parseFloat(navVer) < 4.5 )) {
		loc = 1
		}
	return loc;	
	}
	

/*
Subscribe.aspx takes three of four parameters
	1. t = ThreadId
	2. u = UserDbid
	3. w = 1 or 0 to toggle the Watch
	4. s = 1 or 0 to toggle the Subscribe
*/

//subscribe means user will get email when there is a new post
//watch means the object will appear in a list in their options page

function fnSubscribeThread (doSubscribe, threadId, userDbid, confId) {
	var loc = getLocation();
	document.location.href = "/Actions/SubscribeThread.asp?u=" + userDbid + "&t=" + threadId + "&s=" + doSubscribe + "&r=" + loc + "&c=" + confId;
	}

		
function fnWatchThread (doWatch, threadId, userDbid, confId) {
	var loc = getLocation()
	var qs = "u=" + userDbid + "&t=" + threadId + "&w=" + doWatch + "&r=" + loc + "&c=" + confId;
	document.location.href = "/Actions/SubscribeThread.asp?" + qs
	}

function fnCloseThread(threadId) {
	var loc = getLocation();
	document.location.href = document.location.href = "/Actions/CloseThread.asp?t=" + threadId + "&r=" + loc;	
	}


/*function fnWatchForum (doWatch, forumId, userDbid) {
	var loc = getLocation()
	document.location.href = "/Actions/SubscribeForum.asp?u=" + userDbid + "&f=" + forumId + "&w=" + doWatch + "&r=" + loc;	
	}
	
function fnSubscribeForum (doSubscribe, forumId, userDbid) {	
	var loc = getLocation()
	document.location.href = "/Actions/SubscribeForum.asp?u=" + userDbid + "&f=" + forumId + "&s=" + doSubscribe + "&r=" + loc;	
	}
*/	
/*
function fnScoreMessage (msgId, score) {
	var loc = getLocation()

	var qs = "ms=" + (msgId*3) + "ad" + (score*3)
	var url = "/Actions/ScoreMessage.asp?" + qs + "&r=" + loc;
	
	document.location.href = url
	}
*/

function fnScoreMessage (qs) {
	var loc = getLocation()
	var url = "/Actions/ScoreMessage.asp?ms=" + qs + "&r=" + loc;
	document.location.href = url
	}


function fnHasInputName(eName) {
	if (document.Form1.elements[eName].value.length < 1) {
		alert("Error: Please provide a name string");
		return false
		}
	else {return true;}
	}
function fnStripHtml(eElement) {
	str = eElement.value;
	var re1 = new RegExp("<[a-z][^>]*>", "gi")
	var re2 = new RegExp("</[a-z][^>]*>", "gi")
	str = str.replace(re1,"")
	str = str.replace(re2,"")
	eElement.value = str
	}	
//-->