![Task management app](https://m1multisite001.wpengine.com/wp-content/uploads/images/slide-tech1.jpg)
# 🛠️ AUTO SERVICE APP 🛠️
## 👋 Introduction 👋
Welcome to the project introduction with a short description of its functionality.

Auto service application is created for users who want to fix and purchase some goods for their cars.
Here, users can leave their cars in the service, and our masters can fix their cars, and sell to users needed goods.
After fixing a car, a master can obtain a percentage of money for his job, and owners must pay for that.
Below, I wrote a short description for each endpoint ⬇

## ⚡ Technology stack ⚡
* Programming Language: Java
* Application Configuration: Spring Boot, Spring, Maven
* Accessing Data Spring Data: JPA, Hibernate, MySQL, PostgreSQL
* Web Development: Spring MVC, Servlets, JSP, Tomcat
* Testing and Documentation: JUnit, Mockito, Swagger, TestContainers
* Version Control: Git
* Infrastructure: Docker
* Database migration: Liquibase
* Deploy: AWS

## 🚀 Functionality 🚀
1. **Car Controller**:
    - POST: /api/cars - Add a car
    - PUT: /api/cars/{id} - Update a car
2. **Good Controller**:
    - POST: /api/goods - Add a good
    - PUT: /api/goods/{id} - Update a good
3. **Job Controller**:
    - POST: /api/jobs - Add a job
    - PUT: /api/jobs/{id} - Update a job
    - PUT: /api/jobs/{id}/status - Update a status of the job
4. **Master Controller**:
    - POST: /api/masters - Add a master
    - PUT: /api/masters/{id} - Update a master
    - GET: /api/masters/{id}/orders - Get all orders by master id
    - GET: /api/masters/{id}/salary - Get a master's salary 
5. **Order Controller**:
    - POST: /api/orders/cars/{carId} - Place an order
    - PUT: /api/orders/{id} - Update an order
    - POST: /api/orders/{orderId}/goods/{goodId} - Add a good to the order
    - PUT: /api/orders/{id}/status - Update an order status
    - GET: /api/orders/{orderId}/payment/{carId} - Pay for an order
6. **Owner Controller**:
    - POST: /api/owners - Add an owner
    - PUT: /api/owners/{id} - Update an owner
    - GET: /api/owners/{id}/orders - Get all orders by owner id

## 🎮 How can requests be sent to the endpoints by Postman? 🎮
By the way, you also can try to send requests to the endpoints by using Postman.
I created public collection of endpoints, you can open that by clicking [here](https://www.postman.com/lunar-module-cosmologist-43034160/workspace/my-projects/collection/31108999-341d9115-11a0-48e4-a786-8efc412bb265?action=share&creator=31108999).
Here are all examples of requests, which can be sent by users. Moreover, you can send requests from swagger by clicking here.
To elaborate on that, I recorded a video where I showed how all endpoints work.
You can watch it by clicking [here](https://www.loom.com/share/f6001420d3da40a5a9f64f035bd82321) 😊🎥.

## ⌛ History of creating the project ⌛
During the development of the project, I encountered several challenges. 
Firstly, while creating the project, I deliberated on the fields required for the car owner. 
Contemplating how to best identify the owner, I concluded that the phone number is the best option. 
Throughout the project's development, there were issues with Liquibase scripts, involving conflicts that required resolution. 
Additionally, I faced difficulties connecting Docker to the PostgreSQL database. 
However, through online research, I managed to find solutions. Overall, I believe this project meets all functionality requirements and serves as an excellent solution for users to manage their car repairs.

## 🤌 Possible improvements 🤌 
Overall, I believe this project encompasses all the necessary functionality for interacting with clients. 
However, concerning potential improvements, integrating security for the authentication and authorization of owners and masters would be perfect. 
What's more, incorporating a payment system would allow users to securely make online payments for the service, while enabling masters to receive payments directly to their bank cards. 
Among other potential enhancements, allowing users to provide feedback on the service would be beneficial.

## 🙈 Thank you 🙈
Thank you for reading the presentation, good luck! 💫

![App Screenshot](https://gifdb.com/images/high/thank-you-meme-the-rock-w46sac8m8op8tynp.gif)