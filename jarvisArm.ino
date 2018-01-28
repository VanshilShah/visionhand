#include <Servo.h>
char incomingByte;  // incoming data
Servo servoArm;
Servo servoBase;

int angle = 45; // starts in the middle of 0 .. 90
int armUp = 120; //preset value for servo
int armDown = 40;
int val;


void setup() {
    Serial.begin(9600); // initialization
    
    servoArm.attach(9); //servo attached to pin 9
    servoBase.attach(10); //motor attached to pin 10
    
}
 
 
  
void loop() {
    Serial.println((int)incomingByte); 

    // if the data came    
    if (Serial.available() > 0) {
        incomingByte = Serial.read(); // read byte
        Serial.println((int) incomingByte); 
        
        if((int) incomingByte <= 90){
            angle = incomingByte; //dont need this because using map function;
            turnBase();
        }
        else if((int) incomingByte  == 100){//predefined state to lower arm
            lowerArm();
        }
        else if((int) incomingByte  == 110){
            raiseArm();
        }
    } 
    
    // if not recieveing any data from BLUETOOTH
    else{
        useJoystickToControlArm();
        usePIDtoControlBase();
    }
    
}

void usePIDtoControlBase(){
    int val2 = analogRead(1);            // reads the value of the potentiometer (value between 0 and 1023) 
    Serial.println(val2);
    val2 = map(val2, 0, 1023, 0, 179);     // scale it to use it with the servo (value between 0 and 180) 
    servoBase.write(val2);                  // sets the servo position according to the scaled value 
    delay(80);  
}

void useJoystickToControlArm(){
    val = analogRead(0);            // reads the value of the potentiometer (value between 0 and 1023) 
    Serial.println(val);
    val = map(val, 357, 647, 0, 179);     // scale it to use it with the servo (value between 0 and 180) 
    servoArm.write(val);                  // sets the servo position according to the scaled value 
    delay(100);  
}


void turnBase(){
    val = angle;                         // reads the value of the potentiometer (value between 0 and 1023) 
    val = map(val, 0, 90, 0, 179);     // scale it to use it with the servo (value between 0 and 180) 
    servoBase.write(val);                  // sets the servo position according to the scaled value 
    delay(15);                           // waits for the servo to get there 
}
void lowerArm(){
    servoArm.write(armDown); 
}
void raiseArm(){
    servoArm.write(armUp); 
}
  

