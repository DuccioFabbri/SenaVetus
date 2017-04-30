API googleMAps v2
Ho seguito questo tutorial:
https://developers.google.com/maps/documentation/android/start#the_google_maps_api_key

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

----------------------------------------------------
Una volta generata la chiave, occorre effettuare le modifiche per gestire piú app (una per città)
Ho trovato questo articolo:

http://stackoverflow.com/questions/17296226/using-one-google-maps-api-key-for-different-android-applications

Secondo quanto descritto non occorre generare una nuova chiave per ogni app, basta censire sulla console la stessa chiave su pi´ü app.

You can use the same keystore and the API key for different applications.

Use the same key to sign your apps.
Add a line for each application in the Console page.
So, something like this:

BB:0D:AC:74:D3:21:E1:43:67:71:9B:62:91:AF:A1:66:6E:44:5D:75;com.example.android.mapexample
BB:0D:AC:74:D3:21:E1:43:67:71:9B:62:91:AF:A1:66:6E:44:5D:75;com.example.android.anotherapp
BB:0D:AC:74:D3:21:E1:43:67:71:9B:62:91:AF:A1:66:6E:44:5D:75;com.example.temp.lastapp
And now, you can use the same API key with all these apps.

