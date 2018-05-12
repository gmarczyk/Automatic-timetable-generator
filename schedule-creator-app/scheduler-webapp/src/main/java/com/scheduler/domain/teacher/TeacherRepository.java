package com.scheduler.domain.teacher;

import java.util.List;

import com.scheduler.domain.teacher.Teacher;

public interface TeacherRepository {

    public void create(Teacher teacher);

    public Teacher findByShortcut(String shortcut);

    public List<Teacher> list();

    void delete(Teacher teacher);
}
