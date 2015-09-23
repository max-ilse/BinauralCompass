# BinauralCompass

The goal is to locate the north pole using the sensors of a smart phone and headphones. No display should be used.

Smartphone sensors are used to determine the orientation of the device.
Additionally the north pole is located using the magnetometer.
The raw directory contains 361 sounds (0° - 360°). 
The sound has a duration of 100 ms. It was filtered with a HRTF data set (Kemar, d = 2 m).
According to the angle difference of the device orientation and the north pole
a sound is selected and played back.
The advantages of this lies in the fact that the sound are not generate in realtime,
no dynamic binaural synthesis is needed.

ToDo:
- Using a BRIR data set instead of a HRTF set
- Waiting for sound files to load
- Using a better low-pass algorithm


