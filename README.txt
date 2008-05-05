                                PLANNING POKER
                               ================

 About
-------     

The application was created as an example application to show how to create an
interface where the users could see what each other did "live", using Apache
Wicket.  The application is working though, even if it lacks some nice to have
features and it's therefore possible to use it for real. 

Read more about the Planning Poker concept on Wikipedia:

    http://en.wikipedia.org/wiki/Planning_Poker


 RUN
-----

To run the application you need java 1.5 and maven2 installed.  When you have
that run the following command to start the application:

    # mvn jetty:run
    
Then open a browser and go to:

    http://localhost:8080/planningpoker-wicket/
    

You can also package the application into a war file and deploy it yourself:

    # mvn package
    
 
 License
---------

Released under the Apache Software License 2.  See LICENSE.txt
