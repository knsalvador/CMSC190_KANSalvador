#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h> //nemo-7037b.firebaseio.com/
#include <SoftwareSerial.h>

#define FIREBASE_HOST "nemo-7037b.firebaseio.com" //Without http:// or https:// schemes
#define FIREBASE_AUTH "AcUIulK4okO9seH3H5VMTTD9AOF3xTI5WUiSpZvX"
#define WIFI_SSID "k8"
#define WIFI_PASSWORD "password"

FirebaseData firebaseData;

//SoftwareSerial ArduinoS(2, 3); // RX, TX

String path = "/sensorvalues/";

void setup()
{

  Serial.begin(115200);
  //  ArduinoS.begin(9600);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  firebaseData.setBSSLBufferSize(1024, 1024);

  firebaseData.setResponseSize(1024);
  if (Firebase.setDouble(firebaseData, path , 0 )){}
}
int i = 0;
void loop()
{
  while (!Serial.available()) {
    ;
  }
  while (Serial.available())
  {
    i++;
    String DataFromArduino = Serial.readStringUntil('\n'); // val1 Val1 val2 Val2 val3 Val3

    String ValName1 = getValue(DataFromArduino, ' ', 2);
    String Val1 = getValue(DataFromArduino, ' ', 3);

    String ValName2 = getValue(DataFromArduino, ' ', 4);
    String Val2 = getValue(DataFromArduino, ' ', 5);

    String ValName3 = getValue(DataFromArduino, ' ', 6);
    String Val3 = getValue(DataFromArduino, ' ', 7);

    if (Firebase.setDouble(firebaseData, path + ValName1, Val1.toInt() ))
    {
      //  Serial.println("PASSED");
      //  Serial.println("PATH: " + firebaseData.dataPath());
    }
    else
    {
      //  Serial.println("FAILED");
      //  Serial.println("REASON: " + firebaseData.errorReason());
    }

    if (Firebase.setDouble(firebaseData, path + ValName2, Val2.toInt() ))
    {
      //  Serial.println("PASSED");
      //  Serial.println("PATH: " + firebaseData.dataPath());
    }
    else
    {
      //  Serial.println("FAILED");
      //  Serial.println("REASON: " + firebaseData.errorReason());
    }

    if (Firebase.setDouble(firebaseData, path + ValName3, Val3.toInt() ))
    {
      //   Serial.println("PASSED");
      //   Serial.println("PATH: " + firebaseData.dataPath());
    }
    else
    {
      //  Serial.println("FAILED");
      //   Serial.println("REASON: " + firebaseData.errorReason());
    }
    if (Firebase.getInt(firebaseData, "/light")) {
      Serial.println(firebaseData.intData());

    }
  }
  delay(1000);
}
String getValue(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }

  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}
