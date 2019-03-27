# IoT-Security

**Project Relevance**
Our team comes from a variety of backgrounds. We have two members who are familiar with IoT/networking through some formal means (coursework, research, internship/job experience), one member who has an engineering background (which taught him specifically about MAC layer functionalities), and our final member has a background in math, specializing in machine learning. Even though half our team is familiar with the internet of things, we all have a lot to learn and are planning to do so with this project. For instance, we foresee the biggest challenge in this project to be figuring out how to target and access all connected devices on a network. This comes with multiple challenges, as resource discovery in IoT is non-trivial and there is no one protocol for auditing IoT devices (each company has their own ideas, etc). We believe learning how to accomplish those tasks will be critical not only for this project, but for our careers in computer science; as the number of connected devices increases rapidly, knowing how to work and interact with IoT devices will become quintessential for all computer scientists. 

[image1](chart.png)

**Key Educational Concepts** this project will stress include (but are not limited to):

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Object-Oriented Design

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Test-Driven-Development

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Design Patterns

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Debugging

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Code Optimization
		
**Goals and Milestones**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Our long-term goal with this project is to create a tool that can target all connected devices on a network and perform an audit to search for vulnerabilities on those devices.	
Some milestones we plan to achieve throughout the course of this project are:
Efficiently target all connected IoT devices instead of iterating through all possible IP addresses.
Gain access to all connected devices.
Scan each device, checking for any possible vulnerabilities
Create report, allowing user to see the security of their home’s devices in layman’s terms. 

**Work Plan**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;We are going to split our next two weeks into 2 one-week sprints. For the first week, Matt and Geno will start tackling the resource discovery task to optimize our IP-finder function, while Pat and Kevin mainly read up on IoT and get a better understanding of wireless networks. In addition to reading, Pat will be a consultant for Matt and Geno to discuss anything related to the MAC layer, while Kevin will be working on maintaining our TDD elements: both test-driven development (writing tests) and trunk-driven development (making sure we don’t leave “dead” branches hanging around on GitHub).
In the second week, we will begin by reviewing what we accomplished in the previous week. We hope that Kevin and Pat will be confident with their knowledge of IoT to the point where they will be able to make meaningful contributions to designs and approaches at our project. With Matt and Geno’s resource discovery implementation, we will then work towards figuring out how to access any and all possible devices connected to the network, keeping in mind that no one protocol exists for all devices. We will meet multiple times a week to talk about what we have done, what we need to do, if/how we need help, and how we can help each other. 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;For this project, we will be implementing an Agile system. This will not be apparent in our first week because it is just a reading stage, but in subsequent weeks we will hold standups multiple times per week to discuss progress, help whenever a member gets stuck, have weeklong sprints (see our second week work plan), and use a Kanban project board to track results.
**All Sections**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The feedback for our project on GitHub was most about excitement for the project and people discussing what they bring to the table. We think this is because the original description may have left a little to be desired. However, now that we have a more structured idea of what we want to accomplish, we can discuss the expectations and competencies of each team member:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Matt:** Matt’s background has experience with many security and networking courses at Temple. In his free time, he enjoys learning more about built in programs in Kali Linux like nmap, aircrack-ng, airgeddon, metasploit, OWASP ZAP, and is currently studying for CEH and Security+ certificates. Matt also has good experience with Java, C, SQL, and database management. Matt is aiming for a career in cyber security, and this project would greatly enhance his knowledge and experience. There are currently about 8.5 billion IoT devices connected worldwide, and that number is expected to rise to 22 billion by 2025. The current security flaws in many of the devices today definitely need to be improved very quickly.  

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Geno:** Geno’s background includes experience building software tools to allow security analysts to view network security. He currently works on a team that builds a tool to scan AWS (Amazon Web Services) cloud accounts for vulnerabilities. This coupled with his experience with networking and IoT security gained from coursework will be very relevant to this project. This has given him experience in tools and languages like python, Java, JavaScript, node, SQL, nmap, and wireshark which all have high likelihood of being needed to create a project of this scope. Geno expects that this project will not only be a very helpful learning experience especially for someone trying to get into cyber security as a career, but it will also result in a very useful application being created. IoT is on the rise and I expect its currently lacking security aspects will need to be improved in the near future and this project plans to help with that.  

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Pat(rick):** Patrick's background includes relevant computer networking experience, including reviews of various MAC layer back-off behaviors in heterogeneous IoT networks. He is competent in a variety of languages, including Java, C/C++, Javascript/nodeJs and Python, and has worked with Wireshark on various networking projects. In addition to a wide range of personal projects, he pursues a growing interest in network layer security and authentication protocols, as well as coursework in quality assurance and testing. The development of IoT devices and services is still dominated by poorly designed, highly vulnerable products. As more and more people choose to bring IoT devices into their homes, a project like this will be increasingly important for consumer network security.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Kevin:** Kevin’s background lies primarily in math and machine learning. He has very little experience with networks and security, but he is excited to learn about this and be able to apply what he reads directly into this project. Kevin is competent in a variety of languages that will be useful to this project, including Java, C, and Python, and feels his strong programming skills and algorithmic knowledge will be vital to the team’s success. Kevin expects that with his team’s cooperation, he will learn a lot about his devices, how they’re connected to the network, and what kinds of security risks there may be with those devices. In fact, we will probably be testing our program on his home network and auditing his devices!
