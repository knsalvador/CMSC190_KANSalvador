#include <Adafruit_NeoPixel.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <SoftwareSerial.h>

SoftwareSerial EspSerial(2, 3); // RX, TX
#define PIN 4                 //LED on Arduino port 4
#define NUM_LEDS 8
#define ONE_WIRE_BUS 5       // Data wire is plugged into port 5 on the Arduino
#define SensorPin A0          // the pH meter Analog output is connected with the Arduino’s Analog
unsigned long int avgValue;   //Store the average value of the sensor feedback
float b;
int buf[10], temp;
String ledStatus;
unsigned long int ntuTemp; 
float ntu;
int finalLedStatus;

Adafruit_NeoPixel strip = Adafruit_NeoPixel(NUM_LEDS, PIN, NEO_GRB + NEO_KHZ800); //make an instance of a neopixel
OneWire oneWire(ONE_WIRE_BUS);        // Setup a oneWire instance to communicate
DallasTemperature sensors(&oneWire);  // Pass our oneWire reference to Dallas Temperature.

void setup(){
  Serial.begin(9600);
  sensors.begin();                    // Start up the library
  Serial.println("Ready");
  EspSerial.begin(115200);
  strip.begin();

}
void loop(){
  //PH SENSOR
  for(int i = 0; i < 10; i++){             //Get 10 sample value from the sensor for smoother value
    buf[i] = analogRead(SensorPin);
    delay(10);
  }
  for(int i = 0; i < 9; i++){              //sort the analog from small to large
    for(int j = i + 1; j < 10; j++){
      if(buf[i] > buf[j]){
        temp = buf[i];
        buf[i] = buf[j];
        buf[j] = temp;
      }
    }
  }
  avgValue = 0;
  for(int i = 2; i < 8; i++)               //take the average value of 6 center sample
    avgValue += buf[i];
float phValue = (((float)avgValue/6) * -0.0281) + 22.4831;


  //TURBIDITY SENSOR
    ntuTemp = 0;
    for(int i=0; i<800; i++){
      ntuTemp += (float)analogRead(A2);
    }
    ntuTemp = ntuTemp/800;
    ntu = (ntuTemp * -0.0676) + 61.8468;

    
  //TEMPERATURE SENSOR
  sensors.requestTemperatures();
  float temperature = sensors.getTempCByIndex(0);

  Serial.print("PH: ");  
  Serial.print(phValue);
  Serial.print("\t");
  Serial.print("Turbidity: ");
  Serial.print(ntu);
  Serial.print("\t");
  Serial.print("Temperature: ");
  Serial.print(temperature);
  Serial.println("");

  String allValues;
  allValues = "Start Sting pH "+String(phValue)+" turbidity "+String(ntu)+" temperature "+String(temperature);

  EspSerial.println(allValues);
  delay(4000);

  //LED
  ledStatus=EspSerial.readStringUntil('\n');
  Serial.println(ledStatus.toInt()); 
  finalLedStatus = ledStatus.toInt();

  if(finalLedStatus==1){
    for(int i=0;i<NUM_LEDS;i++){
      strip.setPixelColor(i, strip.Color(255,255,255));
      strip.show();
    }
  }else if(finalLedStatus==0){
    for(int i=0;i<NUM_LEDS;i++){
      strip.setPixelColor(i, strip.Color(0,0,0));     //RGB 0-255
      strip.show(); // This sends the updated pixel color to the hardware.
    }
   }
}

float round_to_dp( float in_value, int decimal_place )
{
  float multiplier = powf( 10.0f, decimal_place );
  in_value = roundf( in_value * multiplier ) / multiplier;
  return in_value;
}
