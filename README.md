**Monitoring Agent- An [HLRS](https://www.hlrs.de/) monitoring service**
------------------------------------------------
The following agent is intended to monitor specific network metrics. The monitoring agent applies to both Windows and Linux machines. The code is developed in Java and use certain libraries like [Sigar](https://github.com/hyperic/sigar)$

Supported Network Metrics:


----------


 **Network Latency** ( miliseconds )

 - Calculate the average time that a package needs to reach his destination(ip).
 - You can define the inside the properties file the ip_address to ping, and the number of packages you want to send.
 - Config.properties file values: PING_IP, PING_PORT, PING_LOOP

**Free Disk Space**( Mb )

 - Number of unallocated Mb on the host.
 - You can define the host rooth path inside the properties file, for the metric calculation.
 - Config.properties file value: FS_ROOT

**Average rate of transmitted/received bytes** ( kBytes/sec )
 

 - Average of packages transmitted or received per second.

**I/O load**

 - Number of reads & writes in Disk.
 - You can define the host rooth path inside the properties file, for the metric calculation.
 - Config.properties file value: FS_ROOT
 
**Bandwidth** ( Mbit / sec )
 

 - Average rate of transmitted bytes / channel width

**NFS connection status** 
 - Check if mount point exist or not. Support versions 2 & 3. 
 - You can define the NFS mount point inside the properties file, for the metric calculation.
 - Config.properties file value: NFS_MOUNT_POINT


----------


**Getting started**

The code runs one Junit test for every available metric. These tests are declared at the SigarTest class inside the test folder.
For any details regarding the Junit tests structure, check the SigarTest class.

**Required Libraries**

 - Sigar(Inside the lib folder)
 - Log4j(Inside the lib folder)

**Installation**

    git clone https://github.com/yosandra/visor.git

**Eclipse Configuration**

 - Import the repository as a Maven project into your Eclipse workspace.

**Run the JUnit Test**
 - Edit the config.properties files values according to your host machine requirements.
 - Right click on the project folder and choose "Run as "
 - Select "JUnit Test"
 - See the output in the console.

**Bugs and Issues**

 - If Junit tests are run on Windows  hosts, please be sure to convert the properties file to dos compatible format, e.g. with unix2dos utility. Otherwise,  the file will not be read correctly.
 - For the NFS test, ensure to modify your mount point at the properties file.

**Communication**

Email: Pavel Skvortsov[skvortsov@hlrs.de] or Vadim Raskin[raskinvadim@gmail.com] 




