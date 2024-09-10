/////////////////////////////////////////////////////////////////////////
//  Created by Walter Zorn
//  ( http://www.kreuzotter.de )
//                 09. 2000
//  Last modified: 22. 9. 2003
/////////////////////////////////////////////////////////////////////////




function CATCH_ERROR()
{
  return true;
}
window.onerror = CATCH_ERROR;



//////////////////////////////////////////////////////////////////////////
//  Globals:

var nn4 = false, nn6 = false, ie4 = false, ie5 = false, ie55 = false, ie6 = false, op4 = false, op402 = false, op5 = false, op6 = false; konq = false; w3c = false, linux = false;
var u = 'undefined';
var n = navigator;
var nua = n.userAgent.toLowerCase();
var w = window;
var d = document;
var db = (d.compatMode && d.compatMode!='BackCompat')? d.documentElement : d.body? d.body : false;
var f;                   // form
var engl = (self.location.href.indexOf('english')>-1);   // inside "english" folder?
var fran = (self.location.href.indexOf('francaise')>-1); // inside "franc" folder?
var applet = false;
var stop;                // stops calculator main function if invalid input is detected
var wnd_i = 0;           // indexes of opened windows
var winTitl;
var winHtm;
var ttObj = null;        // ID of activated tooltip
var wait = false;        // delays access to MOVE_TT for NN6 and Konqueror to avoid overflow of function calls
var tObjOffX;
var tObjOffX;

// Browser sniffer
linux = (nua.indexOf('linux')!=-1);
if (parseFloat(-1)==1)
{
  op4 = true;
  op402 = (nua.indexOf('opera 4.02')!=-1);
}
else if (w.opera && d.getElementById)
{
  op5 = true;
  op6 = (parseInt(nua.charAt(nua.indexOf('opera')+6))>=6);
}
else if (d.layers) nn4 = true;
else if (d.all && nua.indexOf('msie')!=-1)
{
  ie4 = true;
  ie5 = d.getElementById;
  ie55 = (parseFloat(nua.substring(nua.indexOf('msie ')+5))>=5.5);
  ie6 = (parseFloat(nua.substring(nua.indexOf('msie ')+5))>=6.0);
}
else if (d.getElementById)
{
  w3c = true;
  nn6 = (nua.indexOf('gecko')!=-1);
  konq = (nua.indexOf('konqueror')!=-1);
}
//alert('ie4=' + ie4 + ', ie5=' + ie5 + ', ie55=' + ie55 + ', ie6=' + ie6 + ', nn4=' + nn4 + ', op5=' + op5 + ', op6=' + op6 + ', nn6=' + nn6 + ', w3c=' + w3c + ', konq=' + konq);
//alert(n.userAgent);
//alert(n.appVersion);

//  end global variables declaration
//////////////////////////////////////////////////////////////////////////




/////////////////////////////////////////////////////////////////////////
//  Variables which may (or must) be changed to one's desires:

var ttWidth        = 300; // should be equal to width of the tooltip background image
var ttPadding      = 3;
var ttBorderColor  = '#990099';
var ttBorderWidth  = 1;
var ttBgColor      = '#dce9ff';
var ttBgImg        = 'images/tooltip_bg.jpg';
// path to the image folder. Must be accomodated:
var ttBgImgPath    = (location.href.indexOf('\/index.htm')!=-1 || location.href.indexOf('\\index.htm')!=-1)? '' : '..\/';
var ttFontColor    = '#770077';
var ttFontSize     = '10px';
var ttFontWeight   = 'normal';
var ttOffX         = 10;
var ttOffY         = 19;

var ttIds = new Array(
  'bilder',
  'cadence',
  'steigung',
  'feder',
  'home',
  'lenk',
  'schwinge',
  'speed',
  'speich',
  'english',
  'deutsch'
  );
var ttTxts = new Array(
  engl? 'Summary page, 50 KB' : 'Bilder-&Uuml;bersicht (ca. 50 KB)',
  engl? '1.) Cadence Calculator: Relation between gearing, wheel-diameter, velocity and pedaling-cadence<br>2.) Development-Calculator' : '1.) Trittfrequenz-Rechner: Zusammenhang zw. &Uuml;bersetzung, Laufraddurchmesser, Geschwindigkeit und Trittfrequenz.<br>2.) Entfaltungs-Rechner.',
  engl? 'Road Slope Calculator:<br>Calculates either slope, elevation difference or distance along the read' : 'Fahrbahnsteigungs-Rechner:<br>Steigung, H&ouml;henfifferenz oder Fahrstrecke berechnen.',
  engl? 'Bicycle Suspension: Graphical calculator for swingarm design and shock-element properties<br>(Java)' : 'Fahrrad-Federung:<br>Grafisches Programm f&uuml;r Konstruktion und Dimensionierung<br>(Java)',
  engl? 'Home' : 'Startseite',
  engl? 'Bicycle-Steering-Geometry Calculator:<br>Geometrical relation between head-tube angle, trail and fork offset (fork rake)' : 'Online-Rechner:<br>Geometrischer Zusammenhang zwischen<br>Steuerkopfwinkel, Nachlauf und Gabelvorbiegung',
  engl? 'Pedal-\"kick-back\" and "pogo"-effect (influence of transmission on rear-wheel suspension).<br>Graphical calculator (Java).' : 'Pedal-&quot;R&uuml;ckschlag&quot; und Einfluss des Antriebs auf die Hinterradfederung (grafisches Programm, Java)',
  engl? 'Bicycle Speed and Power Calculator,<br>comparing different kinds of bicycles' : 'Online-Rechner:<br>Erreichbare Geschwindigkeit und aufzubringende Leistung auf verschiedenen Fahrr&auml;dern. BMI-Rechner.',
  engl? 'Spoke-Length Calculator' : 'Speichenl&auml;nge berechnen',
  'English version',
  'Deutsche Version'
  );

//  End accommodatable variables
/////////////////////////////////////////////////////////////////////////



/////////////////////////////////////////////////////////////////////////
// Constants to find out if additional tooltip formatting arguments
// have been passed from the html page. Must not be changed here:

// optional W_TT arguments
var WIDTH = 1;
// optional TT arguments
var OFFSETX = 2;
var OFFSETY = 3;

// End optional arguments
//////////////////////////////////////////////////////////////////////////



// Not shure if value is integer? And doubting if "parseInt()" accepts arguments being already integers? Call this!
function TO_INT(arg)
{
  return (arg==null)? 0 : (typeof arg!='number')? parseInt(arg) : arg;
}



// Substitute for the Math.pow() method absent in Konqueror 2.2.1.
function MATH_POW(bs, ep)
{
  var y = null;
  if (!isNaN(bs))
  {
    if (ep==0) y = 1;
    else if (ep==1) y = bs;
    else if (bs<0)
    {
      if ((ep%1>.999999 || ep%1<.000001) && (ep%2<1.999999 && ep%2>.000001)) y = -Math.exp(ep*Math.log(-bs));
      else if (ep%2==0) y = Math.exp(ep*Math.log(-bs));
    }
    else y = Math.exp(ep*Math.log(bs));
  }
  if (y==null) y = 'NaN';
  return y;
}



function FREE_OP()
{
  d.links['free'].click();
}



if (top!=self)
{
  if (w.opera) w.onload = FREE_OP;
  else top.location = self.location;
}



function ALERT()
{
  if (op5 && !op6 && linux)
  {
    if (engl) alert('Opera 5 Linux Users:\nPlease type an \" x \" into the field you wish to be calculated\n(don\'t leave it empty!).');
    else if (fran) ;
    else alert('Hallo Opera 5 Linux - Benutzer:\nIn das Feld, das Sie berechnet haben möchten, ein \' x \' eingeben\n(nicht leer lassen).');
  }
}



// Force NN4 to reload the page if window is resized (NN4 resize bug)
function N4_RESIZ(x)
{
  if (x==true)
  {
    if (nn4)
    {
      d.iW = innerWidth;
      d.iH = innerHeight;
      onresize = N4_RESIZ;
    }
  }
  else if (innerWidth!=d.iW || innerHeight!=d.iH) location.reload();
}
N4_RESIZ(true);



function WRITE(htm)
{
  d.write(htm);
  return true;
}



function MlAdr()
{
    return String.fromCharCode(119, 97, 108, 116, 101, 114, 50, 54, 49, 53, 256>>2, 103, 109, 120, 46, 100, 101);
}

function Ml()
{
    return String.fromCharCode(109, 97, 105, 108, 116, 111, 58) + MlAdr();
}



// Return complete outer + inner tooltip html
function TT_HTM(id, width, txt, bg_color, bg_image)
{
    if (width==null || width==0) width = ttWidth;
    var y = '<div id="' + id + '" style="position:absolute;z-index:99;left:0px;top:' + (!nn4? '-800' : '0') + 'px;width:' + width + 'px;visibility:hidden;';
    //if (ie55) y += 'filter:Alpha(style=1, opacity=100, finishOpacity=100, startX=0, finishX=1);';
    y += '">\n<table bgcolor="' + ttBorderColor + '" border="0" cellpadding="0" cellspacing="0" width="' + width + '"><tr><td>\n';
    y += '<table border="0" cellpadding="' + ttPadding + '" cellspacing="' + ttBorderWidth + '" width="100%"><tr><td bgcolor="' + bg_color + '"' + ((bg_image!=null)? ' background="' + bg_image + '"' : '') + (nn6? ' style="line-height:11px;"' : '') + '>\n';
    y += '<small><font style="color:' + ttFontColor + ';' + ((typeof ttFontFace!=u)? ('font-family:' + ttFontFace + ';') : '') + ((typeof ttFontSize!=u)? ('font-size:' + ttFontSize + ';') : '') + ((typeof ttFontWeight!=u)? ('font-weight:' + ttFontWeight + ';') : '') + '">' + ((typeof ttFontWeight!=u && ttFontWeight.indexOf('bold')!=-1)? '<b>' : '') + txt + ((typeof ttFontWeight!=u && ttFontWeight.indexOf('bold')!=-1)? '<\/b>' : '') + '<br><\/font><\/small>\n';
    y += '<\/td><\/tr><\/table>\n<\/td><\/tr><\/table>\n';
    y += '<\/div>\n';
    return y;
}



function W_TT()
{
  if (nn4 || nn6 || ie4 || op5 || konq)
  {
    var arg = W_TT.arguments;
    if (d.getElementsByTagName && d.getElementsByTagName("applet").length>0) applet = true;

    var htm = nn4? '<div style="position:absolute;">&nbsp;<\/div>\n' : '';

    // always:
    // create Navi Div
    htm += '<div id="navi" style="position:' + ((konq || nn6 || (op5 && !(op6 && applet)))? 'fixed' : 'absolute') + ';left:0px;top:0px;" oncontextmenu="return false"><table border="0" cellspacing="0" cellpadding="2"><tr>';
    //htm += '<br><a onmouseover="TT(\'mail\', OFFSETX, 18, OFFSETY, -2)" onmouseout="HIDE_TT()" href="javascript:void(0)" onclick="this.href=Ml()"><img border="0" src="' + ttBgImgPath + 'images\/envelope_blue.gif" alt="" width="26" height="18"><\/a>';
    htm += '<td><a onmouseover="TT(\'topofpage\', OFFSETX, 14, OFFSETY, -2)" onmouseout="HIDE_TT()" href="#top"><img border="0" src="' + ttBgImgPath + 'images\/topOfPage_blue.gif" alt="" width="13" height="14"><\/a><\/td>';
    var iehp = false, zoom = false;
    if (ie4 && typeof db.style.behavior=='string')
    {
        db.style.behavior = 'url(#default#homepage)';
        if (db.style && db.style.behavior && db.style.behavior=='url(#default#homepage)' && typeof db.setHomePage!=u)
        {
            iehp = true;
            htm += '<td onmouseover="this.style.background=\'#dddddd\';" onmouseout="this.style.background=\'\'" onclick="HP()"><img onmouseover="TT(\'sethomepage\', OFFSETX, 14, OFFSETY, -2)" onmouseout="HIDE_TT()" border="0" src="' + ttBgImgPath + 'images\/homeico.gif" alt="" width="14" height="14"><\/td>';
        }    
    }
    if (!applet && d.getElementsByTagName && typeof d.getElementsByTagName("body")[0].style.zoom!=u)
    {
        zoom = true;
        d.getElementsByTagName("body")[0].style.zoom = '100%';
        htm += '<td oncontextmenu="return false" onmouseover="this.style.background=\'#dddddd\'" onmouseout="this.style.background=\'\'" onmouseup="ZOOM(-12);return false;"><img onmouseover="TT(\'zoomout\', OFFSETX, 14, OFFSETY, -2)" onmouseout="HIDE_TT()" src="' + ttBgImgPath + 'images\/zoomout.gif" alt="" width="14" height="15"><\/td>';
        htm += '<td oncontextmenu="return false" onmouseover="this.style.background=\'#dddddd\'" onmouseout="this.style.background=\'\'" onmouseup="ZOOM(12);return false;"><img onmouseover="TT(\'zoomin\', OFFSETX, 14, OFFSETY, -2)" onmouseout="HIDE_TT()" src="' + ttBgImgPath + 'images\/zoomin.gif" alt="" width="14" height="15"><\/td>';
    }
    htm += '<\/tr><\/table><\/div>\n';
    // create TopOfPage tooltip
    htm += TT_HTM('topofpage', 130, engl? 'Top of page' : fran? 'Retour au début' : 'Nach oben', ttBgColor, ttBgImgPath + ttBgImg);
    // create E-mail tooltip
    htm += TT_HTM('mail', 0, 'Walter Zorn &lt;'+MlAdr()+'&gt;', ttBgColor, ttBgImgPath + ttBgImg);
    if (iehp)
    {
        htm += TT_HTM('sethomepage', 0, engl? 'Make this page your homepage?' : fran? '' : 'Diese Seite als Homepage festlegen', ttBgColor, ttBgImgPath + ttBgImg);
    }
    if (zoom)
    {
        htm += TT_HTM('zoomin', 230, engl? 'Zoom in<br>(Right mousebutton: back to normal)' : fran? '' : 'Ansicht verg&ouml;&szlig;ern<br>(Re. Maustaste: normale Ansicht)', ttBgColor, ttBgImgPath + ttBgImg);
        htm += TT_HTM('zoomout', 230, engl? 'Zoom out<br>(Right mousebutton: back to normal)' : fran? '' : 'Ansicht verkleinern<br>(Re. Maustaste: normale Ansicht)', ttBgColor, ttBgImgPath + ttBgImg);
    }    

    // create tooltips passed as arguments from the html page
    var icrmt;
    var i = 0;
    while (i<arg.length)
    {
      icrmt = 2;
      if (arg[i]!=null && arg[i].length>0)
      {
        var tWidth = ttWidth;
        if (arg[i+2]==WIDTH)
        {
          tWidth = arg[i+3];
          icrmt += 2;
        }
        else icrmt = 2;
        // use default tooltip-txt if no txt has been submitted from the html-page
        if (arg[i+1].length==0)
        {
          for (var j = 0; j<ttIds.length; j++)
          {
            if (ttIds[j]==arg[i])
            {
              arg[i+1] = ttTxts[j];
            }
          }  
        }  
        htm += TT_HTM(arg[i], tWidth, arg[i+1], ttBgColor, ttBgImgPath+ttBgImg);
      }
      i += icrmt;
    }

    if (op4 || op5) htm += '<a name="free" href="' + self.location + '" target="_top"></a>';

    WRITE(htm);

    // IE: set opacity of any tooltip to zero
    /*if (ie55)
    {
      var ttDivs = d.getElementsByTagName('div');
      if (ttDivs.length > 0)
      {
        for (var i = 0; i < ttDivs.length; i++)
        {
          if (ttDivs[i].filters) if (ttDivs[i].filters[0])
          {
            ttDivs[i].filters[0].opacity = 0;
            ttDivs[i].filters[0].finishOpacity = 0;
          }
        }
      }
    }*/
    
    var i = 0, x; while (i<d.links.length)
    {
      if ((x = d.links[i++]).captureEvents) x.captureEvents(Event.MOUSEOUT);
      x.onmouseout = HIDE_TT;
    }    
    
  }

  // automatically set page-title as subject of mail-links
  var mail = Ml();
  for (var i = 0; i<d.links.length; i++)
  {
    if (d.links[i].href && d.links[i].href==mail && d.title)
    {
      d.links[i].href = mail + '?subject=' + d.title;
    }
  }

  return 0;
}



function ZOOM(x)
{
  d.dbz=d.getElementsByTagName("body")[0].style;
  if (w.event && w.event.button && w.event.button>1)
    setTimeout("d.dbz.zoom='100%';d.dbz=null", 50);
  else 
  {
    d.dbz.zoom = (TO_INT(d.dbz.zoom)+x)+"%";
    d.dbz = null;
  }  
}



function HP()
{
  db.setHomePage(self.location.href);
}



function FRESET()
{
  if (f) f.reset();
}



function INIT()
{
    if (d.forms[0])
    {
        f = d.forms[0];
        f.reset();
        if (ie5) w.onload = FRESET;
    }
    if (!(nn6 || konq || op5 && !(op6 && applet))) MOVE_NAVIBUTTON();
    return 0;
}



// Return Event's coordinates related to document
function EVENT(e, dim)
{
  var y = 0;
  if (e)
  {
    if (typeof e.pageX=='number') y = TO_INT((dim == 'x')? e.pageX : e.pageY);
    else if (e.clientX)
    {
      y = TO_INT((dim == 'x')? e.clientX : e.clientY);
      var bod;
      if (d.getElementsByTagName && (bod = d.getElementsByTagName("body")[0]) && bod.style.zoom) y = Math.round(y*100/TO_INT(bod.style.zoom));
      if (ie4 || konq && (dim=='x' && e.x && e.x==e.clientX || dim=='y' && e.y && e.y==e.clientY)) y += TO_INT((dim == 'x')? (db.scrollLeft? db.scrollLeft : 0) : (db.scrollTop? db.scrollTop : 0));
    }
  }
  return y;
}



function CREATE_WINDOW(bdy, titl, bgCol, ww, wh, wx, wy, unresz)
{
  winTitl = titl;
  winHtm = bdy;
  eval('wnd'+wnd_i+' = window.open("../newWindow.htm","wnd'+wnd_i+'","toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=' + ((nn4 && unresz == 1)? 'no' : 'yes') + ',copyhistory=no,width=' + ww + ',height=' + wh + ',screenX=' + wx + ',screenY=' + wy + ',left=' + wx + ',top=' + wy + '");');
  wnd_i++;
  eval('wnd'+(wnd_i-1)+'.window.focus();');
  return 0;
}



function GRAPH_DIV(dx, dy, dw, dh, dColor, dFontFace, dFontSize, dFontColor, dHtm)
{
  var y = '<div style="position:absolute;';
  if (dx!=null) y += 'left:' + dx + 'px;';
  if (dy!=null) y += 'top:' + dy + 'px;';
  if (dw!=null) y += 'width:' + dw + 'px;';
  if (dh!=null) y += 'height:' + dh + 'px;';
  if (dw!=null && dh!=null) y += 'clip:rect(0,'+ dw + 'px,' + dh + 'px,0);';
  if (dColor!=null) y += 'background-color:' + dColor + ';' + (nn4? ('layer-background-color:' + dColor + ';') : '');
  if (dFontFace!=null) y += 'font-family:' + dFontFace + ';';
  if (dFontSize!=null) y += 'font-size:' + dFontSize + ';';
  if (dFontColor!=null) y += 'color:' + dFontColor + ';';
  if (!nn4) y += 'overflow:hidden;';
  y += '">\n';
  if (dHtm!=null) y += (dHtm + '\n');
  y += '<\/div>\n';
  return y;
}



function MOVE_NAVIBUTTON()
{
  if (ie4 || nn4 || d.getElementById)
  {
    var obj = null;
    // ask if 'navi' exists
    if (nn4)
    {
      if (d.navi) obj = d.navi;
    }
    else if (ie4)
    {
      if (d.all['navi']) obj = navi.style;
    }
    else if (d.getElementById('navi')) obj = d.getElementById('navi').style;


    if (obj)
    {
      if (obj.setExpression && !applet)
      {
        obj.setExpression('top', 'TO_INT((w.pageYOffset)? w.pageYOffset : (db.scrollTop)? db.scrollTop : 0)');
        obj.setExpression('left', 'TO_INT((w.pageXOffset)? w.pageXOffset : (db.scrollLeft)? db.scrollLeft : 0)');
      }
      else
      {
        obj.top = TO_INT((w.pageYOffset)? w.pageYOffset : (db.scrollTop)? db.scrollTop : 0) + (!(nn4 || op5)? 'px' : null);
        obj.left = TO_INT((w.pageXOffset)? w.pageXOffset : (db.scrollLeft)? db.scrollLeft : 0) + (!(nn4 || op5)? 'px' : null);
        w.setTimeout('MOVE_NAVIBUTTON()', 20);
      }
    }
  }
  return 0;
}



/*function FADE_IN()
{
  if (ttObj)
  {
    var filt = ttObj.filters[0];
    if (filt.finishX < 20)
    {
      filt.finishX += ((filt.finishX == 11)? 9 : 10);
      filt.opacity += 50;
      setTimeout('FADE_IN()', 50);
    }
    else if (filt.startX < 100)
    {
      filt.startX += 10;
      filt.finishX += ((filt.finishX < 100)? 10 : 0);
      setTimeout('FADE_IN()', 50);
    }
  }
}*/



function TT()
{
  if (nn4 || nn6 || op5 || ie4 || konq)
  {
    if (ttObj) HIDE_TT();
    tObjOffX = ttOffX;
    tObjOffY = ttOffY;

    var args = TT.arguments;
    var id = args[0];

    for (var i = 0; i<args.length; i++)
    {
      if (args[i]==OFFSETX) tObjOffX = args[i+1];
      if (args[i]==OFFSETY) tObjOffY = args[i+1];
    }

    if (nn4) ttObj = d.layers[id]? d.layers[id] : null;
    if (ie4) ttObj = d.all[id]? d.all[id] : null;
    if (op5 || w3c) ttObj = d.getElementById(id)? d.getElementById(id) : null;
    if (typeof ttObj==u) ttObj = null;
    if (ttObj)
    {
      var bod;
      if (d.getElementsByTagName && (bod=d.getElementsByTagName("body")[0]) && bod.style && bod.style.zoom)
      {
        tObjOffX = Math.round(tObjOffX*100/TO_INT(bod.style.zoom));
        tObjOffY = Math.round(tObjOffY*100/TO_INT(bod.style.zoom));
      } 
      d.onmousemove = MOVE_TT;
      if (nn4) d.captureEvents(Event.MOUSEMOVE);
    }    
    //if (ie55) if (ttObj) if (ttObj.filters && ttObj.filters[0]) FADE_IN();
  }
  return 0;
}



function MOVE_TT(evt)
{
  // to avoid "overflow" of function calls, NN6 and Konqueror get delayed access
  if (w3c && wait) return 0;

  if (ttObj)
  {
    evt = evt || w.event;  
    var dWidth = TO_INT((db && db.clientWidth)? db.clientWidth : w.innerWidth);
    var tObjWidth = TO_INT(ttObj.offsetWidth? ttObj.offsetWidth : ttObj.clip? ttObj.clip.width : (ttObj.style && ttObj.style.width)? ttObj.style.width : (ttObj.style && ttObj.style.pixelWidth)? ttObj.style.pixelWidth : ttObj.clientWidth? ttObj.clientWidth : ttWidth);
    var tObjLeft = EVENT(evt, 'x');
    var scrollX = TO_INT(w.pageXOffset? w.pageXOffset : ie4? db.scrollLeft : 0);
    tObjLeft = (tObjLeft > dWidth-tObjWidth+scrollX-(tObjOffX+((nn4 || nn6)? 20 : 2)))? (dWidth-tObjWidth+scrollX-(tObjOffX+((nn4 || nn6)? 20 : 2))) : tObjLeft;
    if ((tObjLeft += tObjOffX)<0) tObjLeft = 0;

    var tObjTop = EVENT(evt, 'y')+tObjOffY;

    if (nn4)
    {
      ttObj.visibility = 'show';
      ttObj.top = tObjTop;
      ttObj.left = tObjLeft;
    }
    else
    {
      ttObj.style.visibility = 'visible';
      ttObj.style.top = ''+tObjTop+(!(nn4 || op5)? 'px' : '');
      ttObj.style.left = ''+tObjLeft+(!(nn4 || op5)? 'px' : '');
    }
  }

  // set timeout for NN6 and Konqueror (see above)
  if (w3c)
  {
    wait = true;
    setTimeout('wait = false', 5);
  }

  return 0;
}



function HIDE_TT()
{
  if (ttObj)
  {
    /*if (ie55 && ttObj.filters && ttObj.filters[0])
    {
      ttObj.filters[0].startX = 0;
      ttObj.filters[0].finishX = 1;
      ttObj.filters[0].opacity = 0;
    }*/
    if (nn4)
    {
      ttObj.visibility = 'hide';
      d.releaseEvents(Event.MOUSEMOVE);
    }    
    else ttObj.style.visibility = 'hidden';
    ttObj = null;
    d.onmousemove = null;
  }
  return 0;
}



function READ_INPUT(el, noColor)
{
  if (op5 && !op6 && linux && el.value.toLowerCase()=='x') el.value = '';
  if (noColor==null) OUTPUT_COLOR(el, '#ffffff');
  var k = el.value.indexOf(',');
  var wt = (el.value==null || el.value.length<=0)? '0' : ((k!=-1)? (el.value.substring(0, k)+'.'+el.value.substring(k+1)) : el.value);

  while (wt.charAt(0)==' ') wt = wt.substring(1);
  var z0 = wt.charAt(0);
  if (z0=='+' || z0=='-') wt = wt.substring(1);
  if (stop==0)
  {
    if (f.E) if (!f.E[0].checked && !f.E[1].checked)
    {
      STOP_READ_INPUT(f.E[0], 'Anglo-american or metric units?\nSelect (in the top section of this page)!');
      return false;
    }
    for (j = 0; j<wt.length; j++) if (j!=wt.indexOf('.')) if (('0123456789').indexOf(wt.charAt(j))==-1)
    {
      if (READ_INPUT.arguments[1]!=null) stop = 1;
      else STOP_READ_INPUT(el, engl? 'Not a valid number!' : fran? 'Chiffre non valable' : 'Ungültiges Zeichen eingegeben!');
      return false;
    }
  }
  var y = ((z0=='-')? -1 : 1) * parseFloat(wt);
  return y;
}



function STOP_READ_INPUT(el, txt)
{
  stop = 1;
  alert((txt==null)? (engl? 'Nuts!' : fran? 'Valeur non valable' : 'Quark!') : txt);
  el.focus();
  el.select();
  return 0;
}



function WRITE_OUTPUT(el,dz,F, noColor)
{
  var y = '' + Math.round(F * MATH_POW(10, dz));
  if (!isNaN(y))
  {
    while (y.substring(y.indexOf('-')+1).length<(dz+1)) y = y.substring(0, y.indexOf('-')+1) + '0' + y.substring(y.indexOf('-')+1);
    y = y.substring(0, y.length-dz) + ((dz>0)? ((engl? '.' : ',')+y.substring(y.length-dz)) : '');
  }
  el.value = y;
  if (noColor==null) OUTPUT_COLOR(el, '#ffccdd');
  return 0;
}



function OUTPUT_COLOR(el, color)
{
  if (el.style) el.style.background = color;
  return 0;
}