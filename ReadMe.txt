Sena Vetus

Il file con l´elenco dei pois si scarica da:
https://sites.google.com/site/ducciofabbri/home/sv-1/downloads.xml
Immagini
https://sites.google.com/site/ducciofabbri/pics/
MP3
https://sites.google.com/site/ducciofabbri/audio/ita

Installato Eclipse ADT Bundle da:
http://developer.android.com/sdk/installing/index.html?pkg=adt
fatte prime installazioni da:
http://developer.android.com/sdk/installing/adding-packages.html
Probabilmente da fare:
http://developer.android.com/google/play-services/setup.html


Installato GitHub sa:
https://github.com/

user: DuccioFabbri
psw: socinus65

Repository: C:\GitHub
Repository locale: C:\GitHub\SenaVetus\SenaVetusApp
C:\Program Files (x86)\Java\jre7\bin>
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
C:\GitHub\SenaVetus\SenaVetusApp\bin\SenaVetusApp.apk




L'applicazione è composta da 2 progetti:
- client Android (AndroidBuildingMusicPlayer):
	Sono presenti 2 activities, la primo PlayListActivity è ilplayer vero e proprio che gestisce la riproduzione dei brani.
	Il progetto è stato scaricato da qui:
	http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/
	La seconda, Download, scarica i brani dal server e invia feedback al server.
	Qui ho seguito diversi esempi tra cui:
	https://github.com/commonsguy/cw-omnibus/blob/master/Internet/Download/src/com/commonsware/android/downmgr/DownloadDemo.java
	http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
	Per l'invio del feedback:
	http://theopentutorials.com/tutorials/android/http/android-how-to-send-http-get-request-to-servlet-using-apache-http-client/
	http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
	Per il setup dell'ambiente di sviluppo:
	http://developer.android.com/google/play-services/setup.html
	---------------------------------------------------------------------
	|  Per il download:                                                  |
	|   http://developer.android.com/google/play/expansion-files.html    |
	|---------------------------------------------------------------------
	
- Servlet (SenaVetus):
	Si tratta di una applicazione che gira su tomcat e attualmente ha l'unico scopo di ricevere ed immagazzinare i feedback dai client Android.
	Il download dei brani avviene direttamente richiedendo al risorsa al server web, non sono fatti controlli di sicurezza.
	Una volta compilata l'applicazione ne va fatto il war e deployato sul tomcat.
	La modifica a context.xml è nelal seguente riga:
	 <Resource name="jdbc/senavetus" auth="Container"
        type="javax.sql.DataSource"
        maxActive="100" maxIdle="30" maxWait="10000"
        username="root"
        password="root"
        driverClassName="com.mysql.jdbc.Driver"
        url="jdbc:mysql://localhost:3306/SenaVetus"/>

	Per accedere al Tomcat sul mio pc, occorre configurare il router e il pc con le opportuneregole di firewall.
	Per il router occorre andare sulla MyFastPage: http://www.fastweb.it/myfastpage/abbonamento/#mConfig
	cliccare su "Configura router" e poi su "modifica". A questo punto si fa un port mapping sull'IP della macchina con Tomcat sulla porta 8080.
Poi occorre creare una regola di ingresso e una di uscita sul firewall del pc, consentendo il traffico sulla porta 8080.
		
	Ho seguito questi tutorial:
	http://theopentutorials.com/examples/java-ee/servlet/how-to-create-a-servlet-with-eclipse-and-tomcat/
	http://theopentutorials.com/examples/java-ee/servlet/servlet-jndi-datasource-in-tomcat/
	La funzione per recuperare l'Ip è èresa da qui:
	http://stackoverflow.com/questions/4678797/how-do-i-get-the-remote-address-of-a-client-in-servlet/15323776#15323776
	
	Attenzione:
	- Una volta scaricato il connector da http://dev.mysql.com/downloads/connector/j/ va salvato nella cartella WEB-INF\lib come indicato nella guida, ma occorre anche importarlo in "java build path" - "libraries"
	- QUando si crea un anuova servlet, ricordarsi di dirgli di creare anche il web.xml
	

Ulteriori installazioni
Tomcat: è necessario per consentire il download dei brani e per rivcevere i feedback. Attualmente è aperta la porta 8080.
Per il debug occorre usare Tomcat locale a Eclipse e creare una configurazione di tipo tomcat di debug per lanciare l'instanza locale.
Una volta avviato Tomcat con la configurazione di debug, si può chiamare direttamente la servlet da un qualsiasi browser.
Attenzione: per non incasinarmi con il Tomcat esterno, configurato sulla 8080, ho configurato il tomcat locale sulla 6060.
Attenzione: il tomcat locale gira come javaw.exe, ma per stopparlo è sempre meglio chiuderlo dalla console "servers" di eclipse, altrimenti si può incasinare e può rimanere bloccata la prta 6060. Facendo kill di tutte le istanze di javaw.exe si dovrebbe ripristinare la situazione.

Firewall: occorre abilitare il traffico sulla porta 8080. Attualmente funziona solo se il pC è colelgato alla Fonera che riceve il segnale dal rootere Fastweb.
Ho rovato a collegare il PC direttamente al router, dato che a volte la fonera si impalla e deve essere riavviata, internet funziona, ma non riesco a collegarmi a tomcat, probabilmente c'è qualche firewall o forwarding da sistemare.

Per raccogliere i feedback deveo avere installato MySql, deve essere accedibile ed avere le tabelle create.
Attualmente ho tuto suuna unica istanza di MySql (Local MySQL), che fa sia da sviluppo e da produzione.
Ho creato una nuova DbConnection dal nome SenaVetus, user: root/root, Host: 127.0.0.1:3306 Schema di default: SenaVetus


Pubblicazione su Google Play:
http://developer.android.com/distribute/googleplay/publish/register.html+-7.

http://developer.android.com/distribute/googleplay/quality/core.html

Prima di pubblicare l'apk su GooglePlay occorre firmarlo fare l'allineamento.
Per farlo si va sul progeto, tx destro-Android Tools- Export signed Application Package

Non trovo più il keystore che ho usato per le prime pubblicazioni, quindi ne ho creato uno nuovo: C:\Users\Duccio\Lavori\Android\SenaVetus.keystore
psw: 123456

chiave: senavetus
destination path:C:\Users\Duccio\Lavori\Android\AndroidBuildingMusicPlayer\AndroidBuildingMusicPlayer\bin

Console per la pubblicazione:
https://play.google.com/apps/publish/?dev_acc=01508487635089532122#AppListPlace

Si clicca su "All application" - "APK" - "Upload new APK to Alpha"

In relatà non posso fare l'upload di un apk che ho firmato con un certigficato differente, devo continuare a firmare con quello riginale.
Cercando meglio ho ritrovato il keystore originale:
C:\Users\Duccio\Lavori\Android\AndroidBuildingMusicPlayer\AndroidBuildingMusicPlayer\bin\SenaVetusKeyStore.apk
psw: 123456

----------------------------------
API googleMAps v2
Ho seguito questo tutorial:
https://developers.google.com/maps/documentation/android/start#the_google_maps_api_key

ATTENZIONE: il tool zipalign si trova in C:\eclipse-ADT\adt-bundle-windows-x86_64-20140702\sdk\build-tools\android-4.4W no in C:\eclipse-ADT\adt-bundle-windows-x86_64-20140702\sdk\tools, quindi per allinearsi alle varie guide occorre spostarlo nella cartella tools.
Occorre poi modificare il path per includere proprio C:\eclipse-ADT\adt-bundle-windows-x86_64-20140702\sdk

Ho creato la Android Key di produzione chiamando: keytool -list -keystore C:\Users\Duccio\Lavori\Android\AndroidBuildingMusicPlayer\AndroidBuildingMusicPlayer\bin\SenaVetusKeyStore.apk -alias senavetus
psw: 123456
............................
C:\Windows\system32>keytool -list -keystore C:\Users\Duccio\Lavori\Android\AndroidBuildingMusicPlaye
r\AndroidBuildingMusicPlayer\bin\SenaVetusKeyStore.apk -alias senavetus
Immettere la password del keystore:
senavetus, 17-set-2013, PrivateKeyEntry,
Impronta digitale certificato (SHA1): 80:94:E7:E0:63:D2:4A:89:93:EB:B7:22:1C:1B:15:D3:90:7F:25:E7

C:\Windows\system32>

............................

con la firma del certificato ci si collega alla console Google:
https://code.google.com/apis/console/?noredirect#project:306122904772
e si clicca su "API Access" e poi "Create Android Key", poi inserire: 80:94:E7:E0:63:D2:4A:89:93:EB:B7:22:1C:1B:15:D3:90:7F:25:E7;it.duccius.musicplayer
come risultato ho:
............................
Key for Android apps (with certificates)
API key:	
AIzaSyCubgNLCBkM6M2LZtOYKx7huCgy5FxmZ6Y
Android apps:	
80:94:E7:E0:63:D2:4A:89:93:EB:B7:22:1C:1B:15:D3:90:7F:25:E7;it.duccius.musicplayer
Activated on:	Oct 10, 2013 2:39 PM
Activated by:	 duccio.fabbri@gmail.com – you
.............................
Rifacendo tutto per l'ambiente di debug (usando il keystore di debug: C:\Users\Duccio\.android\debug.keystore psw:android)

Key for Android apps (with certificates)
API key:	
AIzaSyAGmNi-tMiLLiYiepq_gB6pBKDhBEaGd0k
Android apps:	
4D:23:0D:8D:DA:50:4C:51:30:98:D9:F5:B0:96:71:25:4E:1F:78:3E;it.duccius.musicplayer
Activated on:	Oct 5, 2013 2:39 PM
Activated by:	 duccio.fabbri@gmail.com – you
.................................................

RIcordarsi di aggiungere la chiave al manifest:
http://stackoverflow.com/questions/6424853/error-inflating-class-fragment
Anche così non ha funzionato, ottenendo l'errore:Failed to load map. Could not contact Google servers

allora ho riletot questo blog:
http://blog-emildesign.rhcloud.com/?p=435

ed in effetti mancava:
<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>


Per i tutorial sulle mappe :
https://developers.google.com/maps/documentation/android/infowindows
- http://blog-emildesign.rhcloud.com/?p=822
- (aggiungere una route:
http://stackoverflow.com/questions/3109158/how-to-draw-a-path-on-a-map-using-kml-file/3109723#3109723



