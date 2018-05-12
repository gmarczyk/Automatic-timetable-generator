package com.scheduler.application.teacher;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scheduler.domain.teacher.TeacherRepository;
import com.scheduler.domain.teacher.Teacher;
import com.scheduler.domain.teacher.events.TeacherCreatedEvent;

@Service
public class TeacherService {

    @Autowired
    TeacherRepository teacherRepository;

    public void create(TeacherCreatedEvent createCommand) {
        String shortcut = prepareShortcut(createCommand);
        Teacher teacher = new Teacher(createCommand.firstName, createCommand.lastName, shortcut);
        teacherRepository.create(teacher);
    }

    public void delete(Teacher teacher) {
        teacherRepository.delete(teacher);
    }

    public List<Teacher> allTeachers() {
        return this.teacherRepository.list();
    }

    private String prepareShortcut(final TeacherCreatedEvent createCommand) {
        int firstIter =1;
        int lastIter = 2;
        String shortcut = createCommand.firstName.substring(0,1) + createCommand.lastName.substring(0,lastIter);
        boolean found = true;
        while(teacherRepository.findByShortcut(shortcut) != null) {
            lastIter++;
            if(lastIter > createCommand.lastName.length()) {
                lastIter = 2;
                firstIter++;
                if(firstIter> createCommand.firstName.length()) {
                    found = false;
                    break;
                }
            }
            shortcut = createCommand.firstName.charAt(0) + createCommand.lastName.substring(0,lastIter);
        }

        return (found == false) ? null : shortcut;
    }

}
