Contributions have been made by Robert Estes (RRE) and Max Matveev (MM).

I've tried to mark things we don't know as UNKNOWN - some things
listed as 0 are probably also UNKNOWN.  U refers to the upper nibble
(4 bits), L the lower nibble.  [RRE] 

There are very few bytes left in the file format which we do not understand.
The exercise file format, after being separated from the packets that carry
it, is as follows:

Byte	Meaning
==============================================================================
0 	Bytes in file (LSB)
1 	Bytes in file (MSB)

2	Exercise number (0 to 5) [MM]
3	Exercise label (byte 0) [MM]
4	Exercise label (byte 1) [MM]
5	Exercise label (byte 2) [MM]
6	Exercise label (byte 3) [MM]
7	Exercise label (byte 4) [MM]
8	Exercise label (byte 5) [MM]
9	Exercise label (byte 6) [MM]

10	File date (seconds) (BCD)
11	File date (minutes) (BCD)

12	bit 7: 1 => PM, 0 => AM
	bits 6-0: File date (hours)

13	bit 7 - AM/PM mode (yes if set, no if unset)
        bits 6-0: File date (day of month)

14	File date (year, offset from 2000)

15	L: File date (month, jan = 1)
	U: Exercise duration (tenths of a second)

16	Exercise duration (seconds) (BCD)
17	Exercise duration (minutes) (BCD)
18	Exercise Duration (hours) (BCD)

19	Avg HR
20	Max HR
21	Laps in file
22	Laps in file (duplicates byte 21)
23	0
24	User ID (BCD, 0-99) [MM]
25	bit 1: Units (0 = metric, 1 = english)

26	Mode [HR is always recorded]
	bit 7: 0
	bit 6: 0
	bit 5: Bike 2 (speed)
	bit 4: Bike 1 (speed)
	bit 3: Power
	bit 2: Cadence
	bit 1: Altitude
	bit 0: UNKNOWN - may have to do with metric/English units

27	Recording interval (0 = 5s, 1 = 15s, 2 = 60s)
28	UNKNOWN - Sometimes 0, sometimes 16

29	Limits 1 (Low)
30	Limits 1 (High)
31	Limits 2 (Low)
32	Limits 2 (High)
33	Limits 3 (Low)
34	Limits 3 (High)

35	0
36	UNKNOWN - 0 or 1
37	251

38	Below Zone 1, sec (BCD)
39	Below Zone 1, min (BCD)
40	Below Zone 1, hour (BCD)
41	Within Zone 1, sec (BCD)
42	Within Zone 1, min (BCD)
43	Within Zone 1, hour (BCD)
44	Above Zone 1, sec (BCD)
45	Above Zone 1, min (BCD)
46	Above Zone 1, hour (BCD)

47	Below Zone 2, sec (BCD)
48	Below Zone 2, min (BCD)
49	Below Zone 2, hour (BCD)
50	Within Zone 2, sec (BCD)
51	Within Zone 2, min (BCD)
52	Within Zone 2, hour (BCD)
53	Above Zone 2, sec (BCD)
54	Above Zone 2, min (BCD)
55	Above Zone 2, hour (BCD)

56	Below Zone 3, sec (BCD)
57	Below Zone 3, min (BCD)
58	Below Zone 3, hour (BCD)
59	Within Zone 3, sec (BCD)
60	Within Zone 3, min (BCD)
61	Within Zone 3, hour (BCD)
62	Above Zone 3, sec (BCD)
63	Above Zone 3, min (BCD)
64	Above Zone 3, hour (BCD)

65	UNKNOWN - 1 or 3

66	[U]: Best lap tenths of a second (BCD)
	L - UNKNOWN
67	Best lap seconds (BCD)
68	Best lap minutes (BCD)
69	Best lap hours (BCD)

70	Energy * 10 (BCD) (lower 2 digits)
71	Energy * 10 (BCD) (next higher 2 digits)
72	Energy * 10 (BCD) (highest 2 digits)

        Energy = ((BCD(b70) + BCD(b71)*100 + BCD(b72)*10000)/10.0

73	Total energy (BCD) (lower 2 digits)
74	Total energy (BCD) (next higher 2 digits)
75	Total energy (BCD) (highest 2 digits)

        Total energy = (BCD(b73) + BCD(b74)*100 + BCD(b75)*10000

76	Cumulative workout time (hours) (BCD)
77	Cumulative workout time (hundreds of hours) (BCD)
78	Cumulative workout time (minutes) (BCD)
79	Cumulative ride time (hours) (BCD)
80	Cumulative ride time (hundreds of hours) (BCD)
81	Cumulative ride time (minutes) (BCD)
82	Odometer (bottom two digits) (BCD)
83	Odometer (hundreds) (BCD)
84	Odometer (tens of thousands) (BCD)
85	Exercise distance (LSB)
86	Exercise distance (MSB); exe dist = ((LSB + (MSB<<8)) / 10.0) km

87	Avg Speed (LSB) in 1/16th of the kmh
88	Avg Speed (0-3, MSB), Max Speed (7-4, LSB)
89	Max Speed (MSB), 
	in metric units, the speed is calculated as
        max_speed = ((byte[89] << 4) | byte[88][7-4]) / 16
	avg_speed = ((byte[88][0-3] << 8) | byte[87]) / 16

90	Avg Cadence (rpm)
91	Max Cadence (rpm)

92	Min Alt (LSB)
93	Min Alt (MSB); alt = LSB + ((MSB & 0x7f)<<8); sign = (MSB & 0x80)
94	Avg Alt (LSB)
95	Avg Alt (MSB)
96	Max Alt (LSB)
97	Max Alt (MSB)

98	Min Temp ('C)
99	Avg Temp ('C)
100	Max Temp ('C)
	Metric: BCD -79..+79, 8th bit 1/0 = +/-)
     	English: temperature in degrees F, binary

101	Ascent (LSB)
102	Ascent (MSB)

103	Avg Power (lower 8 of 12 bits)
104	L - Avg Power (upper 4 of 12 bits)
	U - Max Power (lower 4 of 12 bits)
105	Max Power (upper 8 of 12 bits)
106	Avg Pedal Index (right shift the raw value by 1)
107	Max Pedal Index (right shift the raw value by 1)

108	Avg LR Balance (right shift the raw value by 1 to get the L of the LR)
109	(start of lap data) - this is byte 0 of first lap.

[Lap data]

Bytes consumed by lap data depends on number of laps and bytes per lap.  Bytes
per lap depends on what is being recorded, and varies from 6 to 20.  It goes
like this:

Lap bytes 0, 1 and 2 is the timestamp at the end of the lap. 
byte 2 is hours, bits 5-0 of byte 1 is minutes, bits 5-0 of byte 0 is seconds,
bits 7-6 of bytes 0 and 1 are combined to get tenths.

From buffer 2 to 0 we have
	
   hhhhhhhh ttmmmmmm ttssssss

where h = hours, m = minutes, s =seconds, t = tenths.

Lap byte 3 is the lap HR
Lap byte 4 is the avg HR for the lap
Lap byte 5 is the max HR for the lap

c = 6

if ( Alt ) {
  Lap byte c, c+1 = Alt at the end of the lap(lsb, then msb) (offset from 512)
  Lap byte c+2, c+3 = Running Ascent counter(lsb, then msb) at the end 
		      of the lap

                      Metric: in meters
                      English: in multiples of 5 ft.

  Lap byte c+4 = Temperature at the end of the lap

                 Metric: offset from -10 C
                 English: offset from 14 F

  c += 5
}

c = 11

if ( Bike1 || Bike2 ) {

  if ( Cadence ) {
    Lap byte c = Cadence at the end of the lap
    c += 1
  }

  c = 12

  if ( Power ) {
    Lap byte c, c+1, c+2, c+3 = Power data:
    Watts = byte(c) + (byte(c+1) << 8)    
    Pedal Index = byte(c+2) >> 1
    LR balance = byte(c+3) >> 1
    c += 4
  }

  c = 16

  Lap byte c, c+1 = total distance at the end of the lap in tenths of 
                    miles or km (msb, lsb)
 
  Lap byte c+2, c+3 = Speed data

     high_nibble(c+3) * 4 + high_nibble(c+2) determine the integer portion
     low_nibble(c+2)/16 is the fractional portion.  [RRE] - this seems
     to be right, but many times these bits are all zero - don't know
     why.  I also don't know what the the low nibble of c+3 represents.
     [JAA] - This 'lap speed' is the speed at the end of your lap, so could
     these bits be all zero because lap has been started when bike has been 
     stopped? [ref. Polar Manual S720i/S710i USA, pg. d72]

  c += 4

  c = 20
}


To read the lap data, read c bytes N times where N is the number of laps 
recorded.


[Sample data]

Samples are stored in reverse order (the most recent sample occurs first).
The number of bytes per sample varies from 1 to 9 depending on what is being
recorded.  It goes like this:

Sample byte 0: HR (bpm)

c = 1

if ( Alt ) {
  byte c: Alt (LSB)
  byte c+1 [Lower 5 bits]: Alt (MSB); altitude is offset from 512 m.
  c += 2
}

if ( Bike1 || Bike2 ) {

  if ( Alt ) { 
    c -= 1
  }

  byte c [Upper 3 bits]: Speed (MSB)
  byte c+1: Speed (LSB); speed (km/h) = Speed / 16.0
  
  c += 2

  if ( Power ) {
    byte c, c+1, c+2, c+3: Power data:
    Watts = byte(c) + (byte(c+1) << 8)    
    LR balance = byte(c+2) >> 1
    Pedal Index = byte(c+3) >> 1
    c += 4
  }

  if ( Cadence ) {
    byte c: Cadence (rpm)
    c += 1    
  }

}


The number of samples can be computed from either the exercise duration and
recording interval or the sample size and total length of the sample buffer.

The file ends after the sample data.  There is no trailer.

The watch stores files in reverse order (the most recently recorded file is
transmitted first).  The sum of the file sizes is equal to the "File bytes"
value stored in payload bytes 1 and 2 of the watch's response to request 
subtype 0x0b ("Get files").  The individual files are concatenated together
to form a stream of bytes whose length is thus a known quantity.  
