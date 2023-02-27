
# MobileApp
MobileApp has been created for small comapnies.

## Description
Main task the application is magament of tasks assigned to emplyees. Everyone can create and delete all tasks. Every employee has own color (they can be declared in the database) so task browsing is easier.

Add task - 
The application is executing the 'StringRequest' method by providing it with two arguments: 'POST method' and 'Web PHP link' then script update database on the same server.

Get tasks - 
Task are fetched from database by GET method. PHP script return JSON then are displayed in recycleView, application save this data on device memory so this allows you to view tasks without internet access.



The application enables the user to:

- Create new tasks 
- Read new and closed tasks 
- Read list of copies 
- Update unfinished tasks
- Delete unfinished tasks

## How to add new task?

If we want to add a new task we must:

- Add task content
- Chose emploee
- Chose company









![App Screenshot](https://cdn.discordapp.com/attachments/423225764629184512/1078280616895848539/Screenshot_20230223_094638.png)


## How to display acitve tasks?
To dispaly all tasks tap middle button in main view (bacgroud number - count of tasks).


![App Screenshot](https://cdn.discordapp.com/attachments/423225764629184512/1078281467282591784/main_page.png)

Button on the right site of task enables us to Edit, Delete, And Fisinh task.
To refresh page slide finger up to down.

![App Screenshot](https://media.discordapp.net/attachments/423225764629184512/1078262643552440370/tasks_page1.png)

