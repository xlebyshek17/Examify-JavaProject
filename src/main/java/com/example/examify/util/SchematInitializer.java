package com.example.examify.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchematInitializer {
    public static void init() {
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()
        ) {
            s.execute("""
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
            );
            """);
            s.execute("""
            CREATE TABLE IF NOT EXISTS questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                text TEXT NOT NULL,
                type TEXT NOT NULL,
                options TEXT
            );
            """);
            s.execute("""
            CREATE TABLE IF NOT EXISTS exams (
               id INTEGER PRIMARY KEY AUTOINCREMENT,
               student_id INTEGER NOT NULL,
               start_time DATETIME,
               end_time DATETIME,
               score REAL,
               FOREIGN KEY (student_id) REFERENCES students (id)
            );
           """);
            s.execute("""
            CREATE TABLE IF NOT EXISTS answers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                exam_id INTEGER NOT NULL,
                question_id INTEGER NOT NULL,
                answer TEXT NOT NULL,
                is_correct BOOLEAN,
                FOREIGN KEY (exam_id) REFERENCES exams (id),
                FOREIGN KEY (question_id) REFERENCES questions (id)
            );
        """);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
