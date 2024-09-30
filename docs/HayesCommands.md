# Supported Hayes AT command set
See [Hayes AT Command Set on Wikipedia](https://en.wikipedia.org/wiki/Hayes_AT_command_set).

These are the commands and registers that are handled in the TCPSerial code.  
This is **not** a list of all AT commands and registers in existance.


| Command    | Description             | Comments                                                                                                                                                                                                                                                                                                           |
|------------|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| A0 or A    | Answer incoming call    |                                                                                                                                                                                                                                                                                                                    |
| B0 or B1   | 1200 Protocol           | 0 = CCITT V.22 at 1200 bps <br/> 1 = BELL 212A at 1200 bps                                                                                                                                                                                                                                                         |
| D          | Dial Command            | T = Tone Dial (use this one) <br> P = Pulse <br> L = Last number dialed                                                                                                                                                                                                                                            |
| E0 or E1   | Echo Command            | E0 = Disables Echo <br> E1 = Enables Echo                                                                                                                                                                                                                                                                          
| H or H0    | Hang up                 | 	On hook. Hangs up the phone, ending any call in progress                                                                                                                                                                                                                                                          |
| H1         | Hook Status             | Off hook. Answer Incoming Call.                                                                                                                                                                                                                                                                                    |
| L0..3      | Speaker Loudness        | Volume of the speaker (0=Off, 3= Loud)                                                                                                                                                                                                                                                                             |
| M0..3      | Speaker Control         | 0 = Off <br> 1 = Speaker on until remote carrier detected (user will hear dialing and the modem handshake, but once a full connection is established the speaker is muted) <br> 2 = Speaker always on (data sounds are heard after CONNECT) <br> 3= ?                                                              |
| V or V0    | Verbose                 | Numeric Result codes                                                                                                                                                                                                                                                                                               |
| V1         | Verbose                 | English Result codes (Connect, Bust etc..)                                                                                                                                                                                                                                                                         
| Q, Q0, Q1  | Quiet Mode              | Q or Q0 = Off – Displays result codes, user sees command responses (e.g. OK) <br> Q1 = On – Result codes are suppressed, user does not see responses.                                                                                                                                                              |
| Sn         | Select Default Register | See register table below                                                                                                                                                                                                                                                                                           |
| Sn?        | Displayu Register Value | See register table below                                                                                                                                                                                                                                                                                           |
| Sn=r       | Set Register Value      | See register table below                                                                                                                                                                                                                                                                                           |
| X0..4      | Set Response Level      | 0 = Hayes Smartmodem 300 compatible result codes <br> 1 = Hayes Smartmodem 300 compatible result codes <br> 2 = Usually adds dial tone detection (preventing blind dial, and sometimes preventing ATO) <br> 3 = Usually adds busy signal detection. <br> 4 = Usually adds both busy signal and dial tone detection |
| Z0 Z1 or Z | Reset                   | Reset modem to stored configuration. Z0 and Z1 are for multiple stored profiles.                                                                                                                                                                                                                                   |
| &C0..1     | Serial Port DCD Control | 0 = DCD Forced <br> 1 = DCD Not Forced                                                                                                                                                                                                                                                                             | 
| &D0..3     | DTR Control             | Data Terminal Ready <br> 0 = Ignores DTR <br> 1 = On-to-off DTR transition: modem enters command state. <br> 2 = On-to-off DTR transition: modem is set on-hook. <br> 3 = On-to-off DTR transition: modem reset.                                                                                                   |
| &K0,3..6   | Flow Control            | 0 = None <br> 3 = RTSCTSIN and RTSCTSOUT <br> 4 = XON/XOFF In and XON/XOFF Out <br> 5 = XON/XOFF In and XON/XOFF Out <br> 6 = 4 and 5 combined                                                                                                                                                                     |
  
## AT Registers:
| Register | Description | Range | Default |
| -------- | ----------- | ----- |--------|
| 0 | Number of rings before Auto-Answer | 0..255 (0=Never) |        |
| 2 | Escape Character | 0–255, ASCII decimal (+) | 43     | 
| 3 | Carriage Return Character | 0..127 (Ascii Character) | 13     |
| 4 | Line Feed Character | 0..127 (Ascii Character) | 10     |
| 5 | Backspace Character | 0..127 (Ascii Character) | 8      |
| 6 | Wait time before Blind Dialing | 2–255 seconds | 2      |
| 8 | Pause Time for Comma (Dial Delay) | 0..255 seconds | 2      |
| 9 | Carrier Detect Response Time | 1-255 Tenths of a second | 6      |
| 10 | Delay between Loss of Carrier and Hang-Up | 1–255 tenths of a second | 14     |
| 11 | DTMF Tone Duration | 0..255 Milliseconds | 95     |
| 12 | Escape Guard Time | 0–255 fiftieths of a second | 50 |
