package bgu.spl.net.impl.BGRSServer;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a Course from the course list
 * each course has a number, name, maxSeats, seatsTaken,
 * required kdams and a list of students registered to that course
 * <p>
 * this object is thread safe
 * </p>
 */

public class Course implements Comparable<Course> {

    private final int serial;
    private final int num;
    private final String name;
    private final int maxSeats;
    private final AtomicInteger seatsTaken = new AtomicInteger(0);
    private final int[] kdamCourses;
    private final SortedSet<String> registeredStudents;

    public Course(int serial, int num, String name, int[] kdamCourses, int maxSeats) {
        this.serial = serial;
        this.num = num;
        this.name = name;
        this.maxSeats = maxSeats;
        this.kdamCourses = kdamCourses;
        this.registeredStudents = new ConcurrentSkipListSet<>();
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public AtomicInteger getSeatsTaken() {
        return seatsTaken;
    }

    public int[] getKdamCourses() {
        return kdamCourses;
    }

    public SortedSet<String> getRegisteredStudents() {
        return registeredStudents;
    }

    /**
     * adds the student 'name' to the list of registered students if there's an available seat
     *
     * @param name the name of the student who wants to register to the course
     * @return true if registration successful, false otherwise
     */
    public synchronized boolean addStudent(String name) {
        //if there's no available seat return false
        if (registeredStudents.size() == maxSeats)
            return false;
        if (!registeredStudents.add(name))
            return false;
        seatsTaken.incrementAndGet();
        return true;
    }

    /***
     * @param name the name of the student who wants to unregister from the course
     * @return true if unregistered successfully, false otherwise
     */
    public synchronized boolean removeStudent(String name) {
        if (!registeredStudents.remove(name))
            return false;
        seatsTaken.decrementAndGet();
        return true;
    }

    /**
     * @param name the name of the student to check
     * @return true if the student is registered to the course, false otherwise
     */
    public boolean isRegistered(String name) {
        return registeredStudents.contains(name);
    }

    @Override
    public int compareTo(Course course) {
        return Integer.compare(this.serial, course.serial);
    }
}