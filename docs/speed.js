var angl=(engl)?1:0;
var di=document.images, bi, sum=0;
var domn=2, 
    kdomn=1, 
    bdomn=3, 
    t_inf=0,
    prefCad=90, 
    cCad=.002, 
    vmund=0;

var bikeArr = new Array( 'roadster', 'mtb', 'tandem', 'racetops', 'racedrops', 'tria', 'superman', 'lwbuss', 'swbuss', 'swbass', 'ko4', 'ko4tailbox', 'whitehawk', 'questclosed', 'handtrike' );
var Cw      = new Array( .9,         .75,   .33,      .78,        .57,         .505,   .45,        .79,      .63,      .55,      .48,   .405,         0,           0,             .59 );
var sin     = new Array( .95,        .85,   .7,       .89,        .67,         .64,    .55,        .64,      .51,      .44,      .37,   .37,          0,           0,             .55 );
var CwBike  = new Array( 1.6,        1.23,  1.4,      1.2,        1.2,         1,      .75,        1.4,      1.3,      1 ,       .8,    .7,           .03,         .066,          1.2 );
//var aFrame  = new Array( .07,        .06,   .07,      .055,       .055,        .055,   .05,        .045,     .042,     .036,     .027,  .03,          1,           1,             .05 );
var aFrame  = new Array( .06,        .052,  .06,      .048,       .048,        .048,   .044,       .039,     .036,     .031,     .023,  .026,         1,           1,             .046 );
var CATireV = new Array( 1.1,        1.1,   1.1,      1.1,        1.1,         1.1,    .9,         .66,      .8,       .85,      .77,   .77,          .1,          .26,           .9 );
var CATireH = new Array( .9,         .9,    .9,       .9,         .9,          .7,     .7,         .9,       .80,      .84,      .49,   .3,           .13,         .16,           2 );
var FV      = new Array( .33,        .45,   .5,       .37,        .45,         .47,    .48,        .34,      .65,      .65,      .78,   .78,          .715,        .88,           .5 );
var FH      = new Array( .67,        .55,   .5,       .63,        .55,         .53,    .52,        .72,      .5,       .5,       .4,    .4,           .45,         .4,            .55 );
var ks      = new Array( 1.04,       1.035, 1.06,     1.03,       1.03,        1.03,   1.03,       1.05,     1.05,     1.05,     1.06,  1.06,         1.07,        1.09,          1.04 );
var def_mr  = new Array( 18,         12,    17.8,     9.5,        9.5,         9.5,    8,          18,       15.5,     11.5,     11.8,  13.5,         18,          32,            18 );
var i_tireF = new Array( 3,          5,     1,        0,          0,           0,      0,          1,        2,        0,        0,     0,            0,           0,             0 );
var i_tireR = new Array( 3,          5,     1,        0,          0,           0,      0,          3,        3,        0,        0,     0,            0,           0,             0 );



function _LOAD_IMG()
{
  if(di)
  {
    bi=new Array();
    for(j=0;j<_LOAD_IMG.arguments.length;j++)
    {
       bi[j]=new Image();
       bi[j].src=eval('"../images/'+_LOAD_IMG.arguments[j]+'.gif"');
       sum=bi.length;
    }
  }
}

if (engl) _LOAD_IMG('in','cem','lb','kg','deg_F','deg_C','ft','m','mph','kmh','miles','km','sqft','sqm');



function _UNIT_IMAGE()
{
  var ba=_UNIT_IMAGE.arguments;
  var lg=ba.length;
  for(j=0;j<lg-2;j++) eval('di["'+ba[j]+'"].src=(angl==1)?bi['+ba[lg-2]+'].src:bi['+ba[lg-1]+'].src;');
}


function _UNITS(arg)
{
  if(op402&&arg==angl) return 0;
  angl=arg;

  if(di&&sum>11)
  {
    _UNIT_IMAGE('b0','b10',0,1);
    _UNIT_IMAGE('b1','b2','b9',2,3);
    _UNIT_IMAGE('b3',4,5);
    _UNIT_IMAGE('b4','b8',6,7);
    _UNIT_IMAGE('b5','b6',8,9);
    _UNIT_IMAGE('b7',10,11);
    _UNIT_IMAGE('b11',12,13);
  }
  else if(angl==0) alert('Your Browser does not yet support the document.images-object. If you select the metric-units-mode you regrettably won\'t see the proper unit-signs behind the input fields.\nrider\'s height: cm\nrider\'s weight: kg\nbicycle weight: kg\ntemperature: deg C\nheight above sea level: m\nslope of road: %\nwind speed: km/h\nbicycle speed: km/h');

  var i='racedrops';
  for (var j=0;j<f.Ra.length;j++) if(f.Ra[j].checked) i=f.Ra[j].value;

  f.h.value=f.Bh.value=(angl==1)?'67.7':'172';
  f.M.value=f.Bm.value=(angl==1)?'157.2':'71,3';
  f.T.value=(angl==1)?'68':'20';
  f.Hn.value=(angl==1)?'1150':'350';

  CHANGE_INPUTVALUES(i);
  return 0;
}



function _TANDEM()
{
  if (engl) CREATE_WINDOW('<center><h3><font color="#990099">About the Tandem Calculation:<\/font><\/h3><\/center><p>The values of &quot;<b>Rider\'s&nbsp;Height<\/b>&quot;, &quot;<b>Rider\'s&nbsp;Weight<\/b>&quot; and &quot;<b>Power<\/b>&quot; refer to just one of the tandem riders. If Captain and Stoker differ in any of these values, take the mean value of the couple. (The program will internally multiply the given values by 2 <small><b>[1]<\/b><\/small>.)<\/p>\n<p>Adequately, if calculating the power the program will deliver the mean value of the two persons, i. e. the half of the added total input power.<\/p>\n<p>The type of tandem the calculation is based on:<br>Cannondale Aluminum frame. High performance equipment. 28 inch wheels. Equipped with lightweight mudguards and carrier, total weight is 39.2 lb (17.8 kg). Quite big timing rings (40 teeth) to reduce chain friction losses. Racing handle bars for Captain and Stoker. Riding position: hands on the drops (bottom section of the handle bars) front and rear, the upper part of the riders\' bodies not being extremely bent, about 40 degrees to the ground.<\/p>\n<p><small><b>[1]<\/b><\/small> In reality, the total frontal area of a tandem riding team is not twice that of a single rider. The program\'s algorithm regards this fact.<br><\/p>',      'Explanations to Tandem Speed&amp;Power Calculation','#ffffff',500,500,0,0);
  else if (fran) ;
  else CREATE_WINDOW('<center><h3><font color="#990099">Tandemberechnung<\/font><\/h3><\/center>\n<p>Die Werte von &quot;<b>Gr&ouml;&szlig;e Fahrer<\/b>&quot;, &quot;<b>Gewicht Fahrer<\/b>&quot; und &quot;<b>Leistung<\/b>&quot; beziehen sich auf <b>eine(n)<\/b> der beiden Fahrer(innen). Sind diese Werte bei beiden Personen unterschiedlich: den Mittelwert nehmen <small><b>[1]<\/b><\/small>.<\/p>\n<p>Entsprechend gilt: Wird die Leistung berechnet, liefert das Programm den Mittelwert beider Personen, also die halbe Gesamtleistung.<\/p>\n<p>Das Tandem:<br>Cannondale Alu. 28-Zoll-Laufr&auml;der. Gesamtgewicht mit leichten schmalen Schutzblechen und Gep&auml;cktr&auml;ger 17,8 kg. Sehr hochwertige Ausstattung, relativ gro&szlig;e Kettenbl&auml;tter (40 Z&auml;hne) am vorderen Antriebsteil, um die Wirkungsgrad-Verluste gering zu halten. Rennlenker vorne und hinten. Sitzposition: vorne und hinten Untenlenkerhaltung, aber nicht allzu flach. R&uuml;cken jeweils ungef&auml;hr 40 Grad zur Horizontalen.<\/p>\n<p><small><b>[1]<\/b><\/small> Das Programm multipliziert diese Eingabewerte intern mit 2. Insbesondere bei stark differierenden Werten ist dieses Verfahren sicher nicht ideal. Leider sind mir bislang keine exakt aufschl&uuml;sselnden Messungen bekannt, welchen Anteil &quot;Captain&quot; und &quot;Stoker&quot; jeweils am Gesamtluftwiderstand eines Tandems haben.<br>Die Gesamt-Fahrerstirnfl&auml;che ist selbstverst&auml;ndlich nicht doppelt so gro&szlig; wie bei einem Nicht-Tandem. Der Algorithmus des Programms nimmt darauf R&uuml;cksicht.<br><\/p>',       'Info zur Tandem-Berechnung','#ffffff',500,500,0,0);
  t_inf=1;
  return 0;
}


function CHANGE_INPUTVALUES(bike)
{
  if (f!=null)
  {
    angl=(f.E&&f.E[0].checked)?1:0;
    for (var i=0; i<bikeArr.length; i++) if (bike==bikeArr[i])
    {
      WRITE_OUTPUT(f.mr, 1, def_mr[i] * ((angl==1)? 2.2 : 1), 'noColor');
      f.VR.selectedIndex = i_tireF[i];
      f.HR.selectedIndex = i_tireR[i];
    }
  }
  return 0;
}



function PV(mode)
{
  if (f==null) return 0;
  stop = 0;
  var bike = null; for (var j=0; j<f.Ra.length; j++) if (f.Ra[j].checked) bike = f.Ra[j].value;
  if (bike==null)
  {
    alert(engl? 'Select a kind of bicycle.' : fran? 'Choisissez un type de bicyclette.' : 'Bitte erst Fahrradtyp auswählen.');
    return 0;
  }
  for (var bikeI=0; bikeI<bikeArr.length; bikeI++) if (bike==bikeArr[bikeI]) break;

  angl = (f.E&&f.E[0].checked)?1:0;
  var hRider = READ_INPUT(f.h) * (engl? ((angl==1)? .0254 : .01) : 1) * ((bike=='tandem')? 2 : 1);
  var M = READ_INPUT(f.M) * ((angl==1)? .453592 : 1) * ((bike=='tandem')? 2 : 1);
  var mBik = READ_INPUT(f.mr) * ((angl==1)? .453592 : 1);
  var T = (angl==1)? 5 * (READ_INPUT(f.T)-32)/9 : READ_INPUT(f.T);
  var Hn = READ_INPUT(f.Hn)*((angl==1)? .3048 : 1);
  var stg = Math.atan(READ_INPUT(f.stg) * .01);
  var W = READ_INPUT(f.W) * ((angl==1)? .44704 : .27778);
  var P = READ_INPUT(f.P) * ((bike=='tandem')? 2 : 1);
  var V = READ_INPUT(f.V) * ((angl==1)? .44704 : .27778);
  var cad = READ_INPUT(f.Cadc);
  var diagrCad;
  var Pin = f.P.value, Vin = f.V.value;
  if(stop==1) return 0;

  if (!engl) hRider /= ((hRider<10)? 1 : ((10<=hRider&&hRider<400)? 100 : 1000));

  f.Cadc.value = diagrCad = cad = Math.abs(cad);
  if (cad > 10) prefCad = cad;

  var Cr = new Array(.006, .0055, .005, .0075, .003, .007);
  var ATire = new Array(.021, .031, .042, .048, .042, .055)
  var j = f.VR.selectedIndex;
  var CrV = Cr[j], ATireV = ATire[j];
  j = f.HR.selectedIndex;
  var CrH = Cr[j], ATireH = ATire[j];
  var CrEff = FV[bikeI] * CrV + FH[bikeI] * CrH;
  var adipos = (bike=='whitehawk'||bike=='questclosed')? 0: Math.sqrt(M/(hRider*750));
  var CwaBike = CwBike[bikeI] * (CATireV[bikeI] * ATireV + CATireH[bikeI] * ATireH + aFrame[bikeI]);
  var Cstg = 9.81 * (mBik + M) * (CrEff * Math.cos(stg) + Math.sin(stg));
  
  var windtxt = engl? 'Tail-wind speed is bigger than bicycle-speed.\nThus the air drag could not be evaluated correctly.\nTry it again with a \"less negative\" wind speed.' : fran? 'Vitesse de vent arrière surpasse la vitesse de la bicyclette.\nPar conséquent, la résistance à l\'air ne pouvait pas être calculée correctement.\nIndiquéez une vitesse de vent que est \"moins negative\".' : 'Rückenwindgeschwindigkeit ist größer als Fahrgeschwindigkeit.\nDeshalb konnte kein realistischer Luftwiderstandswert ermittelt werden.\nVersuchen Sie es mit einer \"weniger negativen\" Windgeschwindigkeit.';
  var P0txt = engl? 'Rider\'s Power <= 0' : fran? 'Puissance <= 0' : 'Fahrerleistung <= 0';
  var P1txt = engl? 'Rider\'s Power > 0' : fran? 'Puissance > 0' : 'Fahrerleistung > 0';
  var Ptxt = engl? ' Watt.\nThe program has presumed to reset the Cadence to ' : fran? ' Watt.\nLe programme a présumé de remettre ... à ' : ' Watt.\nDeshalb wurde die Trittfrequenz automatisch gesetzt: auf ';

  if (Vin.length==0 && Pin.length==0)
  {
    alert(engl? 'Calculation impossible.\nEither speed or power must be declared.' : fran? 'Calculation impossible: La vitesse ou la puissance doit etre indiquée.' : 'Berechnung nicht möglich.\nEntweder Fahrerleistung oder Geschwindigkeit angeben.');
    return 0;
  }
  if (Pin.length==0 || Vin.length>0 && Pin.length>0 && domn==1)
  {
    domn=1;
    if (W<-V)
    {
       f.P.value='';
       alert(windtxt + ((engl || fran)? '' : '\n( Anmerkung: Radfahrer sind, entgegen einer weitverbreiteten Ansicht,\nmeist schneller als der Rückenwind.\nDaher hat ein aerodynamisch günstigeres Rad auch bei Rückenwind Vorteile. )'));
       return 0;
    }
    else
    {
      var y = 7979;
      while (y == 7979 || Math.round(y) <= 0 && cad > 0 || Math.round(y) > 0 && cad == 0)
      {
        if (y != 7979 && Math.round(y) <= 0 && cad > 0)
        {
          cad = 0;
          WRITE_OUTPUT(f.Cadc, 0, cad);
          alert(P0txt + Ptxt + '0.');
        }
        if (y != 7979 && Math.round(y) > 0 && cad == 0)
        {
          cad = prefCad;
          WRITE_OUTPUT(f.Cadc, 0, cad);
          alert(P1txt + Ptxt + cad + '.');
        }
        var CwaRider = (1 + cad * cCad) * Cw[bikeI] * adipos * (((hRider - adipos) * sin[bikeI]) + adipos);
        var Kw = 176.5 * Math.exp(-Hn * .0001253) * (CwaRider + CwaBike) / (273 + T);
        var vw=V+W;
        y = (ks[bikeI] * V * (Kw * (vw * ((vw<0)? -vw : vw)) + Cstg)) * ((bike=='tandem')? .5 : 1);
      }
      WRITE_OUTPUT(f.P, 0, y);
    }
  }
  else
  {
    domn=2;
    if (P > 0 && cad == 0)
    {
      cad = prefCad;
      WRITE_OUTPUT(f.Cadc, 0, cad);
      alert(P1txt + Ptxt + cad + '.');
    }
    if (P <= 0 && cad > 0)
    {
      WRITE_OUTPUT(f.Cadc, 0, 0);
      cad = 0;
      alert(P0txt + Ptxt + '0.');
    }
    var CwaRider = (1 + cad * cCad) * Cw[bikeI] * adipos * (((hRider - adipos) * sin[bikeI]) + adipos);
alert(CwaRider);
    var Kw = 176.5 * Math.exp(-Hn * .0001253) * (CwaRider + CwaBike) / (273 + T);
alert(Kw);    
    var expA = W*W/9-Cstg/(3*Kw), expB=W*W*W/27+W*Cstg/(3*Kw)+P/(2*Kw*ks[bikeI]), iwurz=expB*expB-expA*expA*expA, ire=expB-Math.sqrt(iwurz);
alert(expA);    
alert(expB);
alert(iwurz);
alert(ire);
    if (iwurz >= 0) var Vms = MATH_POW(expB+Math.sqrt(iwurz),1/3)+((ire<0)?-MATH_POW(-ire,1/3):MATH_POW(ire,1/3));
    else var Vms = 2*Math.sqrt(expA)*Math.cos(Math.acos(expB/Math.sqrt(MATH_POW(expA,3)))/3);
    WRITE_OUTPUT(f.V, 1, ((angl==1)?2.2369:3.6)*(Vms-2*W/3));
    if(Vms-2*W/3<-W)
    {
       alert((engl? 'Probably wrong speed result.\n' : fran? 'Résultat de vitesse probablement incorrecte.\n' : 'Wahrscheinlich falsches Geschwindigkeits-Ergebnis.\n') + windtxt);
       return 0;
    }
  }
  WRITE_OUTPUT(f.CwA, 4, (CwaBike + CwaRider) * ((angl == 1)? 10.764 : 1));
  WRITE_OUTPUT(f.Cr, 5, CrEff);

  P=READ_INPUT(f.P,1);
  V=READ_INPUT(f.V,1)*((angl==1)?.4470389:.277777777778);
  var dst=READ_INPUT(f.d1)*((angl==1)?1609.34:1000)+READ_INPUT(f.d2)*((angl==1)?.3048:1), dur=READ_INPUT(f.t1)*3600+READ_INPUT(f.t2)*60+READ_INPUT(f.t3);
  if(stop==1) return 0;
  if(dst<=0&&dur<=0) f.kal.value='';
  else if(dur<=0||dst>0&&dur>0&&kdomn==1)
  {
    kdomn=1;
    f.t1.value=f.t2.value=f.t3.value='';
    yt=(Math.round(10*dst/V))*.1;
    if(yt>=3600) WRITE_OUTPUT(f.t1, 0, parseInt(yt/3600));
    if(yt>=60) WRITE_OUTPUT(f.t2, 0, parseInt((yt%3600)/60));
    WRITE_OUTPUT(f.t3, 1, yt%60);
    WRITE_OUTPUT(f.kal, 0, (P>0)? (P*dst/(V*1047)) : 0);
  }
  else
  {
    kdomn=2;
    f.d1.value=f.d2.value='';
    yd=(Math.round(10*V*dur))*((angl==1)?.06213727:.1);
    if(yd>=1000) WRITE_OUTPUT(f.d1, 0, parseInt(yd/1000));
    WRITE_OUTPUT(f.d2, 1, ((angl==1)?(yd*5.28)%5280:yd%1000));
    WRITE_OUTPUT(f.kal, 0, (P>0)? (P*dur/1047) : 0);
  }
  if (mode == 'Details') alert('Body angle: ' + (Math.asin(sin[bikeI])*180/Math.PI) + '  °\n(this is an average of all parts of the rider\'s body,\nnot just the angle of the upper body)\n\nCd bike: ' + CwBike[bikeI] + '\nA bike: ' + (CATireV[bikeI]*ATireV+CATireH[bikeI]*ATireH + aFrame[bikeI]) + '  m^2\nCdA bike: ' + CwaBike + '  m^2\n\nCd rider: ' + ((1+cad*cCad)*Cw[bikeI]) + '\nA rider: ' + (adipos*(((hRider-adipos)*sin[bikeI])+adipos)) + '  m^2\nCdA rider: ' + CwaRider + '  m^2\n\nCdA rider: ' + (100*CwaRider/(CwaRider+CwaBike)) + '  %\n\nRolling drag coefficients:\nCr front (w\/o wheeldiameter correction): ' + CrV + '\nCr rear: ' + CrH);

  if(mode=='Graph')
  {
      if (document.getElementById||document.all||document.layers)
      {
      //////  Diagram vars which may be accommodated  ///////
          if (engl)
          {
            var titelRad=new Array('Roadster Bicycle','MTB','Tandem (racing bars)','Racing Bicycle (hands on the tops)','Racing Bicycle (hands on the drops)','Triathlon Bicycle','Racing Bicycle - Superman-Position','Long Wheel Base Recumbent (Under Seat Steering)','Short Wheel Base Recumbent (Under Seat Steering)','Short Wheel Base Recumbent (Above Seat Steering)','Lowracer Recument (Kreuzotter race)','Lowracer Recumbent with Aero Tailbox','Fully streamlined Lowracer (White Hawk; 1h World Record)','Streamlined Trike &quot;Quest&quot;');
            var gVarX=(domn==1)?'Speed':'Power';
            var gVarY=(domn==1)?'Power':'Speed';
            var gUnitX=(domn==1)?((angl==1)?'mph':'km/h'):'Watts';
            var gUnitY=(domn==1)?'Watts':((angl==1)?'mph':'km/h');
            var graph='<center><form><input class="bt3" type="button" value="Close this Diagram Window" onclick="window.close()"><\/form><\/center>\n';
          }
          else
          {
            var titelRad=new Array('Hollandrad','MTB','Tandem (Rennlenker)','Rennrad Obenlenker-Haltung','Rennrad Unterlenker-Haltung','Triathlon-Rennrad','Rennrad Superman-Position','Langliegerad Untenlenker (Alltags-Ausstattung)','Kurzliegerad Untenlenker (Alltagsausstattung)','Kurzliegerad Obenlenker (Rennausstattung)','Tiefliegerad Obenlenker','Tiefliegerad mit Heckflosse','Tiefliegerad mit Vollverschalung (White Hawk)','Velomobil Quest','Handbike 3 R&auml;der');
            var gVarX=(domn==1)?'Geschwindigkeit':'Leistung';
            var gVarY=(domn==1)?'Leistung':'Geschwindigkeit';
            var gUnitX=(domn==1)?'km/h':'Watt';
            var gUnitY=(domn==1)?'Watt':'km/h';
            var graph='<center><form><input class="bt3" type="button" value="Dieses Diagrammfenster schlie&szlig;en" onclick="window.close()"><\/form><\/center>\n';
          }
          var gColor=(domn==1)?'#cc66bb':'#7c90f3';
          var gFontColorBold=(domn==1)?'#000099':'#660066';
          var gFontColorLight='#99cc99';
          var gFontSize='10px';
          var gSteps=(domn==1)?((angl==1)?1000:800):1200;
          var gIntervalX=(domn==1)?((angl==1)?2:5):50; //Intervall (in Werten) für X-Beschriftung
          var gIntervalY=(domn==1)?50:((angl==1)?2:5); //Intervall für Y-Beschriftung
          var gFactorX=(domn==1)?((angl==1)?.05:.1):1; //Wertänderung pro Schritt
          var gZoomY=(domn==1)?.3:((angl==1)?7:4); //y-Pix pro Wertänderung
          var gZoomX=.5; //x-Pix pro Schritt
          var gWidth=4;
          var gPaddingTop=60;
          var gPaddingLeft=50;
          var gPaddingRight=(domn == 1)? 160 : 110;
          var diagramLeftMargin=11;
          var diagramBottomMargin=10;
      
      //////  End accomodatable diagram vars  ////////
      
          var titel=titelRad[bikeI]+': '+gVarY+' / '+gVarX;
          var Y_0;
      
          var iJump, Y=new Array(2);
          var i=gSteps;
          while(i>-2)
          {
            Y[0]=Y[1];
            var x = i*gFactorX;
            if(domn==1)
            {
              x *= (angl==1)?.44704:.27778;
              y = 7979;
              while (y == 7979 || Math.round(y) <= 0 && diagrCad > 0 || Math.round(y) > 0 && diagrCad == 0)
              {
                if (y != 7979 && Math.round(y) <= 0 && diagrCad > 0) diagrCad = 0;
                if (y != 7979 && Math.round(y) > 0 && diagrCad == 0) diagrCad = prefCad;
                CwaRider = (1 + diagrCad * cCad) * Cw[bikeI] * adipos * (((hRider - adipos) * sin[bikeI]) + adipos);
                Kw = 176.5 * Math.exp(-Hn * .0001253) * (CwaRider + CwaBike) / (273 + T);
                vw = x + W;
                y = (ks[bikeI] * x * (Kw * (vw * ((vw<0)? -vw : vw)) + Cstg)) * ((bike=='tandem')? .5 : 1);
              }
              Y[1] = y;
            }
            else
            {
              x *= ((bike=='tandem')? 2 : 1);
              if (x > 0 && diagrCad == 0) diagrCad = prefCad;
              var CwaRider = (1 + diagrCad * cCad) * Cw[bikeI] * adipos * (((hRider - adipos) * sin[bikeI]) + adipos);
              var Kw = 176.5 * Math.exp(-Hn * .0001253) * (CwaRider + CwaBike) / (273 + T);
              expB = W*W*W/27+W*Cstg/(3*Kw)+x/(2*Kw*ks[bikeI]);
              iwurz = expB*expB-expA*expA*expA;
              ire = expB-Math.sqrt(iwurz);
              if (iwurz >= 0) Y[1] = MATH_POW(expB+Math.sqrt(iwurz),1/3)+((ire<0)?-MATH_POW(-ire,1/3):MATH_POW(ire,1/3));
              else Y[1] = 2*Math.sqrt(expA)*Math.cos(Math.acos(expB/Math.sqrt(MATH_POW(expA,3)))/3);
              Y[1] = ((angl==1)?2.2369:3.6)*(Y[1]-2*W/3);
            }
            Y[1] = Math.round(Y[1]*gZoomY);
            if (i<gSteps-1&&i!=-1&&Y[0]-Y[1]==0&&Math.ceil(iJump*gZoomX)<gWidth)
            {
              iJump++;
            }
            else
            {
              var iJump=0;
              if (i==gSteps)
              {
                Y_0=Y[1];
                i_0=i*gZoomX+10;
                graph += GRAPH_DIV(gSteps*gZoomX+gPaddingLeft+30, Y_0+gPaddingTop-4, null, null, null, 'verdana,geneva,helvetica,sans-serif', '12px', gFontColorBold, '<b>'+gVarX+'<br>'+gUnitX+'<br>&nbsp;<br>&nbsp;<\/b>');
                graph += GRAPH_DIV(gPaddingLeft-40, gPaddingTop-33, null, null, null, 'verdana,geneva,helvetica,sans-serif', '12px', gFontColorBold, '<b>'+gVarY+'<br>'+gUnitY+'<\/b>');
                for(var xz=Math.round(gSteps*gFactorX);xz>=0;xz-=gIntervalX)
                {
                  graph += GRAPH_DIV(Math.round(gPaddingLeft+xz*gZoomX/gFactorX), gPaddingTop, 1, Y_0, (Math.round(xz/gIntervalX) % 2 == 0)? gFontColorBold : gFontColorLight, null, null, null, null);
                  if (Math.round(xz/gIntervalX) % 2 == 0) graph += GRAPH_DIV(Math.round(gPaddingLeft-5+xz*gZoomX/gFactorX), Math.round(Y_0+gPaddingTop+diagramBottomMargin), null, null, null, 'verdana,geneva,helvetica,sans-serif', gFontSize, gFontColorBold, xz);
                }
                for(var yz=0;yz<Math.round(Y_0/gZoomY);yz+=gIntervalY)
                {
                  graph += GRAPH_DIV(gPaddingLeft, Math.round(Y_0+gPaddingTop-yz*gZoomY), i_0, 1, (Math.round(yz/gIntervalY) % 2 == 0)? gFontColorBold : gFontColorLight, null, null, null);
                  graph += GRAPH_DIV(0, Math.round(Y_0+gPaddingTop-yz*gZoomY-4), gPaddingLeft-diagramLeftMargin, null, null, 'verdana,geneva,helvetica,sans-serif;text-align:right', gFontSize, (Math.round(yz/gIntervalY) % 2 == 0)? gFontColorBold : gFontColorLight, yz);
                }
              }
              else
              {
                graph += GRAPH_DIV(Math.round(gPaddingLeft-gWidth/2+(i+1)*gZoomX), Math.round(Y_0+gPaddingTop-gWidth/2-Y[0]), gWidth, gWidth, gColor, null, null, null, null);
                if (domn == 1 && Y[0] >= 0 && Y[1] < 0)
                {
                  graph += GRAPH_DIV(Math.round(gPaddingLeft+(i+1)*gZoomX)-50, Y_0+gPaddingTop-30, 176, 16, null, null, null, null, '<small>' + (engl? 'Pedaling Cadence' : 'Trittfrequenz') + ' = ' + prefCad + ' \/ min<\/small>');
                  graph += GRAPH_DIV(Math.round(gPaddingLeft+(i+1)*gZoomX)-5, Y_0+gPaddingTop+diagramBottomMargin+ ((Y[1]<-diagramBottomMargin-11)?-Y[1]:11), 176, 16, null, null, null, null, '<small>' + (engl? 'Pedaling Cadence' : 'Trittfrequenz') + ' = 0 \/ min<\/small>');
                }
              }
            }
            i--;
          }
          CREATE_WINDOW(graph,titel,'#ffffff',gSteps*gZoomX+gPaddingLeft+gPaddingRight,((Y_0+gPaddingTop+60)<(screen.availHeight-80))?(Y_0+gPaddingTop+60):(screen.availHeight-80),0,0,1);
      }
      else alert(engl? "This Browser can't display the diagram" : fran? "" : "Dieser Browser kann das Diagramm nicht darstellen");
  }
  return 0;
}



function _PORTION()
{
  if(f==null) return 0;
  stop=0;
  var kal=READ_INPUT(f.kal,1);
  if(stop==1) return 0;
  if(kal>0)
  {
    var g=' Gramm', s=' St&uuml;ck', l=' Liter', tb='', ay=new Array('Waln&uuml;ssen (Kerngewicht 4 g)','Schokoladetafeln &agrave; 100 g','Energieriegeln (z.B. <i>P**erbar<\/i>) &agrave; 65 g','&Auml;pfeln &agrave; 150 g','Fruchteiskugeln &agrave; 100 g','M&uuml;sli mit Milch\, Obst und Honig','Rohkostsalat mit &Ouml;l','Spaghetti Bolognese','Pommes frites','Kartoffeln (gesch&auml;lt) &agrave; 80 g','Lammfleisch (Keule)','Vollkornbrotscheiben &agrave; 50 g (ohne Belag)','Kn&auml;ckebrotscheiben (ohne Belag)','Apfelsaft-Schorle (50/50)','Vollmilch (3.5 Prozent Fettanteil)',((vmund==0)?'(wer\'s unbedingt wissen will) ':'')+'helles Bier','Rotwein', 26,535,225,75,130,1.73,.59,2.26,2.5,68,2.4,100,30,250,640,470,700, s,s,s,s,s,g,g,g,g,s,g,s,s,l,l,l,l);
    for(var j=0; j<ay.length/3; j++)
    {
       WRITE_OUTPUT(f.o,((ay[j+2*ay.length/3]!=' Gramm')?1:0),kal/ay[j+ay.length/3], 'noColor');
       var itd = '<td align="left" bgcolor="#'+((j%2==0)?'d3e6ff':'eeddee')+'">';
       tb += '<tr>' + itd + ((j>0)?'oder ':'') + ay[j] + ': ' + '<\/td>' + itd + '<b>' + f.o.value + '<\/b>' + ay[j + 2*ay.length/3] + '<\/td><\/tr>\n';
    }
    CREATE_WINDOW('<center><b>' + kal  + ((vmund==0)?(' Kilokalorien<\/b>'):(' kcal<\/b>'))+' sind enthalten in:<\/b><br>&nbsp;<br><\/center>\n<center><table bgcolor="#990099" border="0" cellpadding="0" cellspacing="0"><tr><td><table border="0" cellpadding="3" cellspacing="1" width="100%">\n'+tb+'<\/table><\/td><\/tr><\/table>\n<\/center>',         kal + ' kcal', '#ffffff', 520, ((vmund==0)?520:480), 0, 0);
    vmund=1;
  }
  else alert('Das Kalorien-Feld enthaelt keinen gueltigen Wert');
  return 0;
}



function BMI()
{
  if(f==null) return 0;
  angl=(f.E&&f.E[0].checked)?1:0;
  stop=0;
  var M=READ_INPUT(f.Bm)*((angl==1)?.453592:1);
  var hRider=READ_INPUT(f.Bh)*(engl? ((angl==1)?.0254:.01) : 1);
  hRider=hRider/((hRider<10)?1:((10<=hRider&&hRider<400)?100:1000));
  var bmi=READ_INPUT(f.Bbmi);
  if(stop==1) return 0;

  if(M==0||M!=0&&hRider!=0&&bmi!=0&&bdomn==1)
  {
    bdomn=1;
    WRITE_OUTPUT(f.Bm,1,bmi*hRider*hRider*((angl==1)?2.2046:1));
  }
  else if(hRider==0||M!=0&&hRider!=0&&bmi!=0&&bdomn==2)
  {
    bdomn=2;
    WRITE_OUTPUT(f.Bh,((angl==1)?1:0),Math.sqrt(M/bmi)*((angl==1)?39.37:100));
  }
  else
  {
    bdomn=3;
    WRITE_OUTPUT(f.Bbmi,1,M/(hRider*hRider));
  }
  return 0;
}