# Automatic-timetable-generator

(!) at certain moment I had to hurry up because of the deadline incomming. Therefore some parts of code are not role model in context of clean code. Simply put, some classes may be messy.

<b>Description:</b>

My engineers diploma thesis and the application. It is an web application designed for schools to create schedules. Provides functionality such as automatic generation of the schedule. Algorithm of the generator is an non-trivial heuristic designed to try to provide any satisfying solution to an NP-hard problem, which is the generation of optimal/ideal schedules. It tries to achieve an extreme of optimization function, which is the determinant of schedule quality. More about it can be read in the thesis, unfortunately it is written in polish.


<b>Repository contains:</b>
- the product itself <b>(schedule-creator-app)</b>
- project of thesis in LaTeX <b>(schedule-creator-doc)</b>
- thesis in <b>pdf</b> (only in polish language)

<b>Technologies used in application:</b>
- Maven
- Spring
- Hibernate
- Vaadin
- Apache Tomcat
- PostgreSQL + pgAdmin
- RabbitMQ
- BCrypt

<b>Architecture (techniques, concepts, patterns):</b>
- Domain Driven Design (DDD)
- Event Driven Architecture (EDA)
- Multitenancy
