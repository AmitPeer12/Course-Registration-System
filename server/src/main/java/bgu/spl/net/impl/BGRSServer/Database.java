package bgu.spl.net.impl.BGRSServer;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

    private static class SingletonHolder {
        private static final Database instance = new Database();
    }

    //Hashmap that holds all the registered Admin users
    private final ConcurrentHashMap<String, Admin> adminUsers;
    //Hashmap that holds all the registered Student users
    private final ConcurrentHashMap<String, Student> studentUsers;
    //Array that holds all the courses, sorted by order of appearance in the text file
    private Course[] courses;
    //this HashMap helps find a course by it's number, the course number is the KEY and its respective serial is the VAL
    private final HashMap<Integer, Integer> courseSerials = new HashMap<>();

    //to prevent user from creating new Database
    private Database() {
        adminUsers = new ConcurrentHashMap<>();
        studentUsers = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * loads the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(coursesFilePath))) {
            List<String> temp = new LinkedList<>();
            String line = br.readLine();

            while (line != null) {
                temp.add(line);
                line = br.readLine();
            }
            this.courses = new Course[temp.size() + 1];
            courses[0] = null; //in case we try to input an illegal course number
            int i = 1;
            for (String course : temp) {
                //split each string to its 4 components as written in the file
                String[] parts = course.split("\\|");
                courses[i] = new Course(i, Integer.parseInt(parts[0]), parts[1], stringToIntArray(parts[2]), Integer.parseInt(parts[3]));
                courseSerials.put(courses[i].getNum(), i);
                i++;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private int[] stringToIntArray(String str) {
        String[] stringArr = str.substring(1, str.length() - 1).split(",");
        if (!stringArr[0].equals("")) {
            int[] intArr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++)
                intArr[i] = Integer.parseInt(stringArr[i]);
            return intArr;
        }
        return new int[0];
    }

    /**
     * @param name the name of the user preforming the action
     * @param pass the password to register with
     * @return true if registration successful, false otherwise
     */
    public boolean registerAdmin(String name, String pass) {
        return (adminUsers.putIfAbsent(name, new Admin(name, pass)) == null);
    }

    /**
     * @param name the name of the user preforming the action
     * @param pass the password to register with
     * @return true if registration successful, false otherwise
     */
    public boolean registerStudent(String name, String pass) {
        return (studentUsers.putIfAbsent(name, new Student(name, pass)) == null);
    }

    /**
     * login if provided a correct password, registered and not logged in already
     *
     * @param name the name of the user preforming the action
     * @param pass the password to login with
     * @return true if login successful, false otherwise
     */
    public boolean login(String name, String pass) {
        Admin tempA = adminUsers.get(name);
        //if no admin registered under that name, try a student
        if (tempA == null) {
            Student tempS = studentUsers.get(name);
            if (tempS == null || !pass.equals(tempS.getPsw()) || tempS.isLoggedIn())
                return false;
            tempS.logInOrOut();
        } else {
            if (!pass.equals(tempA.getPsw()) || tempA.isLoggedIn())
                return false;
            tempA.logInOrOut();
        }
        return true;
    }

    /**
     * logout if registered and logged in
     *
     * @param name the name of the user preforming the action
     * @return true if logout successful, false otherwise
     */
    public boolean logout(String name) {
        if (name == null)
            return false;
        Admin tempA = adminUsers.get(name);
        //if no admin registered under that name, try a student
        if (tempA == null) {
            Student tempS = studentUsers.get(name);
            if (tempS == null || !tempS.isLoggedIn())
                return false;
            tempS.logInOrOut();
        } else {
            if (!tempA.isLoggedIn())
                return false;
            tempA.logInOrOut();
        }
        return true;
    }

    /**
     * register if all kdam requirements are fulfilled
     *
     * @param name the name of the user preforming the action
     * @param num  the number of course to register
     * @return true if registered successfully, false otherwise
     */
    public boolean registerCourse(String name, int num) {
        if (name == null)
            return false;
        Student student = studentUsers.get(name);
        Course course = courses[courseSerials.getOrDefault(num, 0)];
        if (student == null || !student.isLoggedIn() || course == null)
            return false;
        //if the student is already registered return false
        if (student.isRegistered(course))
            return false;
        int[] kdams = course.getKdamCourses();
        for (int kdam : kdams) {
            Course kdamCourse = courses[courseSerials.get(kdam)];
            if (!student.isRegistered(kdamCourse))
                return false;
        }
        //if we got here 'student' fulfills all the kdam requirements

        //add 'student' to the students registered to 'course', this function can fail if there are no seats available
        if (!course.addStudent(name))
            return false;
        //add the course to the student's registered courses
        student.register(course);
        return true;
    }

    /**
     * @param num the number of course to check
     * @return the array of kdams required for course 'num'
     */
    public String kdamCheck(String name, int num) {
        if (name == null)
            return null;
        //check if the user preforming the action is logged in
        Student student = studentUsers.get(name);
        if (student == null || !student.isLoggedIn()) {
            Admin admin = adminUsers.get(name);
            if (admin == null || !admin.isLoggedIn())
                return null;
        }
        //check if the course exists in the database
        Course course = courses[courseSerials.getOrDefault(num, 0)];
        if (course == null)
            return null;
        //get the kdams and return them
        return "\n" + Arrays.toString(course.getKdamCourses());
    }

    /**
     * @param num the number of course to check
     * @print the information of the course 'num'
     */
    public String courseStat(String user, int num) {
        if (user == null)
            return null;
        Admin admin = adminUsers.get(user);
        Course course = courses[courseSerials.getOrDefault(num, 0)];
        if (admin == null || !admin.isLoggedIn() || course == null)
            return null;
        return ("\n" + "Course: " + "(" + num + ") " + course.getName() +
                "\n" + "Seats Available: " + (course.getMaxSeats() - course.getSeatsTaken().intValue()) + "/" + course.getMaxSeats() +
                "\n" + "Students Registered: " + course.getRegisteredStudents());
    }

    /**
     * @param name the name of the student to check
     * @print the information of the student 'name'
     */
    public String studentStat(String user, String name) {
        if (user == null)
            return null;
        Admin admin = adminUsers.get(user);
        Student student = studentUsers.get(name);
        if (admin == null || !admin.isLoggedIn() || student == null)
            return null;
        return ("\n" + "Student: " + student.getName() +
                "\n" + "Courses: " + student.getCourses());
    }

    /**
     * @param name the name of the user preforming the action
     * @param num  the number of course to check
     * @return "REGISTERED" if the student 'name' is registered to course 'num' or "UNREGISTERED" otherwise
     */
    public String isRegistered(String name, int num) {
        if (name == null)
            return null;
        Course course = courses[courseSerials.getOrDefault(num, 0)];
        if (course == null)
            return null;
        if (course.isRegistered(name))
            return "\n" + "REGISTERED";
        return "\n" + "NOT REGISTERED";
    }

    /**
     * unregister only if student 'name' is registered to course 'num'
     *
     * @param name the name of the user preforming the action
     * @param num  the number of course to unregister
     * @return true if unregistered successfully, false otherwise
     */
    public boolean unregister(String name, int num) {
        if (name == null)
            return false;
        Student student = studentUsers.get(name);
        Course course = courses[courseSerials.getOrDefault(num, 0)];
        if (course == null || student == null || !student.isLoggedIn())
            return false;
        if (!student.isRegistered(course))
            return false;
        if (!course.removeStudent(name))
            return false;
        student.unregister(course);
        return true;
    }

    /**
     * @param name the name of the user preforming the action
     * @return a list representing the courses that student 'name' is registered to
     */
    public String myCourses(String name) {
        if (name == null)
            return null;
        Student student = studentUsers.get(name);
        if (student == null || !student.isLoggedIn())
            return null;
        return "\n" + student.getCourses();
    }

    public void clear() {
        adminUsers.clear();
        studentUsers.clear();
    }
}