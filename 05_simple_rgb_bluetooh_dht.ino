#include <Adafruit_NeoPixel.h>
#include "DHT.h"
#ifdef __AVR__
 #include <avr/power.h> // Required for 16 MHz Adafruit Trinket
#endif



#define DHTPIN 4     // Digital pin connected to the DHT sensor
// Feather HUZZAH ESP8266 note: use pins 3, 4, 5, 12, 13 or 14 --
// Pin 15 can work but DHT must be disconnected during program upload.

// Uncomment whatever type you're using!
//#define DHTTYPE DHT11   // DHT 11
#define DHTTYPE DHT22   // DHT 22  (AM2302), AM2321

#define PIN        6 // On Trinket or Gemma, suggest changing this to 1
#define LED_PIN    6

// How many NeoPixels are attached to the Arduino?
#define LED_COUNT 8
// How many NeoPixels are attached to the Arduino?
//#define NUMPIXELS 8 // Popular NeoPixel ring size
// When setting up the NeoPixel library, we tell it how many pixels,
// and which pin to use to send signals. Note that for older NeoPixel
// strips you might need to change the third parameter -- see the
// strandtest example for more information on possible values.

Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);

// Time (in milliseconds) to pause between pixels

//Salida 
float h,t,f;

// String que recibe
char letra;
int orden,r,g,b,l=50,tsimu;
int tempo;
DHT dht(DHTPIN, DHTTYPE);
//Confuguracion
void setup() {
  // Any other board, you can remove this part (but no harm leaving it):
#if defined(__AVR_ATtiny85__) && (F_CPU == 16000000)
  clock_prescale_set(clock_div_1);
#endif
  // END of Trinket-specific code.


  Serial.begin(9600);
  dht.begin();
  strip.begin();           // INITIALIZE NeoPixel strip object (REQUIRED)
  strip.show();            // Turn OFF all pixels ASAP
  strip.setBrightness(l);
  
}

//Programa en ciclo

void loop() {

// 01 Sensor de temperatura ---------------------------------------------------------------------------------------------------
  delay(2000); //Esperamos por que el sensor tarda en tomar mediciones

      h = dht.readHumidity();
    // Read temperature as Celsius (the default)
     t = dht.readTemperature();
    // Read temperature as Fahrenheit (isFahrenheit = true)
     f = dht.readTemperature(true);

    // Check if any reads failed and exit early (to try again).
    if (isnan(h) || isnan(t) || isnan(f)) {
      //Serial.println(F("Failed to read from DHT sensor!"));
    Serial.print(F("Failed"));  //------------------------------------------------ 01 STATUS DHT
    return;
    }else{
      Serial.print(F("Success"));//------------------------------------------------ 01 STATUS DHT
    }
    //Calculo de la sensación del calor
    // Compute heat index in Fahrenheit (the default)
    float hif = dht.computeHeatIndex(f, h);
    // Compute heat index in Celsius (isFahreheit = false)
    float hic = dht.computeHeatIndex(t, h, false);

    Serial.print(F(","));         
    Serial.print(h);              //------------------------------------------------ 02 Humedad %
    Serial.print(F(","));
    Serial.print(t);              //------------------------------------------------ 03 Temperatura °C 
    Serial.print(F(","));     
    Serial.print(f);              //------------------------------------------------ 04 Temperatura °F 
    Serial.print(F(","));
    Serial.print(hic);            //------------------------------------------------ 05 Sesación de calor °C 
    Serial.print(F(","));
    Serial.print(hif);            //------------------------------------------------ 05 Sesación de calor °F
    Serial.println(F(","));
   //Serial.println(F(","));
// 01 FIN Sensor de temperatura ---------------------------------------------------------------------------------------------------

 //esto siempre se imprime
 //queremos que la temperatura siempre se envie

 //dentro del while solo debe estar la funcion para recibir datos
 // afuera debe estar el codigo que envia mensajes

 // 02 Recibir ORDEN RGB del telefono  ---------------------------------------------------------------------------------------------------
while(Serial.available() > 0) //Mientras lo recibido sea mayor a 0
  {
    String cadena = Serial.readStringUntil('\n');
    cadena = cadena.substring(0, cadena.length());
    //Serial.println("Cadena: " + cadena);

  //  Serial.println("Cantidad de caracteres");
   // Serial.println(cadena.length());
   // Serial.println("PrimeraPosición");
   // Serial.println(cadena.indexOf(","));
    //nos permite extraer los caracteres de un string
    char char_array[cadena.length() + 1];
    cadena.toCharArray(char_array, cadena.length() + 1);

   // Serial.println("Caracteres: ");
   // for (int i = 0; i < cadena.length(); i++) {
    //  Serial.print(char_array[i]);
   // }
   // Serial.println();
    char *token = strtok(char_array, ",");
    if (token != NULL) {
      orden = atoi(token);
      token = strtok(NULL, ",");
    }
    if (token != NULL) {
      r = atoi(token);
      token = strtok(NULL, ",");
    }

    if (token != NULL) {
      g = atoi(token);
      token = strtok(NULL, ",");
    }
    if (token != NULL) {
      b = atoi(token);
      token = strtok(NULL, ",");
    }
    if (token != NULL) {
      l = atoi(token);
      token = strtok(NULL, ",");
    }
    if (token != NULL) {
      tsimu = atoi(token);
      
    }
/*
      Serial.print("Orden: ");
      Serial.println(orden);
              // Ahora puedes usar r, g y b como desees
      Serial.print("Red: ");
      Serial.println(r);
      Serial.print("Green: ");
      Serial.println(g);
      Serial.print("Blue: ");
      Serial.println(b);
      Serial.print("Intensidad: ");
      Serial.println(l);
*/
      
    strip.setBrightness(l);
    switch(orden){
       case 0:
        colorWipe(strip.Color(0,   0,   0), 50); 
        break;
      case 21:
        colorWipe(strip.Color(r,   g,   b), 50); 
        break;
      case 22:
        theaterChase(strip.Color(r,   g,   b), 50);
        break;
      case 23:
        rainbow(10); 
        break;
      case 24:
        theaterChaseRainbow(50);
        break;
      case 25://Sonidero
        colorWipe(strip.Color(255,   0,   0)     , 50); // Red/*
        colorWipe(strip.Color(  0, 255,   0)     , 50); // Green
        colorWipe(strip.Color(255,   0,   0)     , 50); 
        break;
      case 31:
        //if(l<=10){
        if(t<=10){
          colorWipe(strip.Color(255, 0, 0), 50); 

        } else if(t>11 & t<25){
          colorWipe(strip.Color(255, 255, 0), 50); 
        } else if(t>25){
          colorWipe(strip.Color(0, 255, 0), 50); 

        }

        break;
        case 32:
        if (tsimu <= -40) {
    colorWipe(strip.Color(0, 0, 255), 50);  // Azul
} else if (tsimu > -40 && tsimu <= -20) {
    colorWipe(strip.Color(0, 128, 255), 50);  // Azul claro
} else if (tsimu > -21 && tsimu <= 0) {
    colorWipe(strip.Color(255, 255, 255), 50);  // Blanco
} else if (tsimu > 1 && tsimu <= 10) {
    colorWipe(strip.Color(255, 0, 0), 50);  // Rojo
} else if (tsimu > 11 && tsimu <= 25) {
    colorWipe(strip.Color(255, 255, 0), 50);  // Amarillo
} else if (tsimu > 26 && tsimu <= 45) {
    colorWipe(strip.Color(0, 255, 0), 50);  // Verde
} else if (tsimu > 46 && tsimu <= 60) {
    colorWipe(strip.Color(255, 165, 0), 50);  // Naranja
} else if (tsimu > 61 && tsimu <= 80) {
    colorWipe(strip.Color(255, 200, 0), 50);  // Naranja
}

        break;
      

}



/******************************************************************/

    Serial.println();
    cadena = "";
  }
//entra a un case como un meno  
  
    // Set all pixel colors to 'off'

  // The first NeoPixel in a strand is #0, second is 1, all the way up
  // to the count of pixels minus one.
  

}


// Some functions of our own for creating animated effects -----------------

// Fill strip pixels one after another with a color. Strip is NOT cleared
// first; anything there will be covered pixel by pixel. Pass in color
// (as a single 'packed' 32-bit value, which you can get by calling
// strip.Color(red, green, blue) as shown in the loop() function above),
// and a delay time (in milliseconds) between pixels.
void colorWipe(uint32_t color, int wait) {
  for(int i=0; i<strip.numPixels(); i++) { // For each pixel in strip...
    strip.setPixelColor(i, color);         //  Set pixel's color (in RAM)
    strip.show();                          //  Update strip to match
    delay(wait);                           //  Pause for a moment
  }
}

// Theater-marquee-style chasing lights. Pass in a color (32-bit value,
// a la strip.Color(r,g,b) as mentioned above), and a delay time (in ms)
// between frames.
void theaterChase(uint32_t color, int wait) {
  for(int a=0; a<10; a++) {  // Repeat 10 times...
    for(int b=0; b<3; b++) { //  'b' counts from 0 to 2...
      strip.clear();         //   Set all pixels in RAM to 0 (off)
      // 'c' counts up from 'b' to end of strip in steps of 3...
      for(int c=b; c<strip.numPixels(); c += 3) {
        strip.setPixelColor(c, color); // Set pixel 'c' to value 'color'
      }
      strip.show(); // Update strip with new contents
      delay(wait);  // Pause for a moment
    }
  }
}

// Rainbow cycle along whole strip. Pass delay time (in ms) between frames.
void rainbow(int wait) {
  // Hue of first pixel runs 5 complete loops through the color wheel.
  // Color wheel has a range of 65536 but it's OK if we roll over, so
  // just count from 0 to 5*65536. Adding 256 to firstPixelHue each time
  // means we'll make 5*65536/256 = 1280 passes through this loop:
  for(long firstPixelHue = 0; firstPixelHue < 5*65536; firstPixelHue += 256) {
    // strip.rainbow() can take a single argument (first pixel hue) or
    // optionally a few extras: number of rainbow repetitions (default 1),
    // saturation and value (brightness) (both 0-255, similar to the
    // ColorHSV() function, default 255), and a true/false flag for whether
    // to apply gamma correction to provide 'truer' colors (default true).
    strip.rainbow(firstPixelHue);
    // Above line is equivalent to:
    // strip.rainbow(firstPixelHue, 1, 255, 255, true);
    strip.show(); // Update strip with new contents
    delay(wait);  // Pause for a moment
  }
}

// Rainbow-enhanced theater marquee. Pass delay time (in ms) between frames.
void theaterChaseRainbow(int wait) {
  int firstPixelHue = 0;     // First pixel starts at red (hue 0)
  for(int a=0; a<30; a++) {  // Repeat 30 times...
    for(int b=0; b<3; b++) { //  'b' counts from 0 to 2...
      strip.clear();         //   Set all pixels in RAM to 0 (off)
      // 'c' counts up from 'b' to end of strip in increments of 3...
      for(int c=b; c<strip.numPixels(); c += 3) {
        // hue of pixel 'c' is offset by an amount to make one full
        // revolution of the color wheel (range 65536) along the length
        // of the strip (strip.numPixels() steps):
        int      hue   = firstPixelHue + c * 65536L / strip.numPixels();
        uint32_t color = strip.gamma32(strip.ColorHSV(hue)); // hue -> RGB
        strip.setPixelColor(c, color); // Set pixel 'c' to value 'color'
      }
      strip.show();                // Update strip with new contents
      delay(wait);                 // Pause for a moment
      firstPixelHue += 65536 / 90; // One cycle of color wheel over 90 frames
    }
  }
}
